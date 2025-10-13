package npng.handdoc.hospital.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import npng.handdoc.hospital.domain.Hospital;
import npng.handdoc.hospital.domain.HospitalHour;
import npng.handdoc.hospital.domain.type.Day;
import npng.handdoc.hospital.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HospitalService {

    private final WebClient webClient;
    private final HospitalRepository hospitalRepository;

    @Value("${public-data.api.url}")
    private String apiUrl;

    private static final int API_PAGE_SIZE = 1000; // API 페이지당 요청할 데이터 수

    /**
     * 1. db의 기존 데이터 조회
     * 2. api를 페이지별로 순회하여 모든 데이터를 가져옴
     * 3. 각 데이터를 필터링하고 검증
     * 4. 신규 데이터는 저장하고, 기존 데이터는 변경점을 감지하여 업데이트
     * 5. api에서 사라진 데이터는 db에서 삭제 처리
     */
    @Transactional
    public void synchronizeHospitalData() {
        log.info("병원 데이터 동기화를 시작합니다. (필터: 서대문구, 병원)");

        // DB에서 기존 병원 데이터를 Map으로 조회 (Key: openapiId)
        Map<String, Hospital> existingHospitalsMap = hospitalRepository.findAll().stream()
                .collect(Collectors.toMap(Hospital::getOpenapiId, Function.identity()));
        log.info("DB에서 {}개의 기존 병원 정보를 조회했습니다.", existingHospitalsMap.size());

        List<Hospital> hospitalsToSave = new ArrayList<>();

        int startIndex = 1;
        while (true) {
            log.info("API 호출 중... (startIndex: {})", startIndex);
            String url = apiUrl + "/" + startIndex + "/" + (startIndex + API_PAGE_SIZE - 1);

            JsonNode responseNode = webClient.get().uri(url).retrieve().bodyToMono(JsonNode.class).block();
            JsonNode rows = responseNode != null ? responseNode.path("TbHospitalInfo").path("row") : null;

            if (rows == null || !rows.isArray() || rows.isEmpty()) {
                log.info("API로부터 더 이상 데이터를 받아오지 못했습니다. 동기화 루프를 종료합니다.");
                break;
            }

            for (JsonNode node : rows) {
                // 필수 데이터 검증 (이름, 주소, 위/경도)
                if (isInvalidNode(node)) {
                    continue; // 필수 데이터가 없으면 이 데이터를 무시
                }

                // 서비스 요구사항에 따른 필터링 (서대문구, 병원)
                if (!isTargetHospital(node)) {
                    continue; // 필터 조건에 맞지 않으면 무시
                }

                String openapiId = node.path("HPID").asText();
                Hospital existingHospital = existingHospitalsMap.get(openapiId);

                if (existingHospital == null) {
                    // 신규 병원 저장 목록에 추가
                    hospitalsToSave.add(createNewHospital(node));
                } else {
                    // 기존 병원 변경점 확인 후 업데이트
                    if (isModified(existingHospital, node)) {
                        updateHospital(existingHospital, node);
                    }
                    // 처리된 병원은 맵에서 제거
                    existingHospitalsMap.remove(openapiId);
                }
            }
            startIndex += API_PAGE_SIZE;
        }

        // 변경된 데이터 일괄 저장 및 삭제
        if (!hospitalsToSave.isEmpty()) {
            hospitalRepository.saveAll(hospitalsToSave);
            log.info("신규 '서대문구' 병원 {}개를 DB에 저장했습니다.", hospitalsToSave.size());
        }

        // 업데이트는
        if (!existingHospitalsMap.isEmpty()) {
            hospitalRepository.deleteAll(existingHospitalsMap.values());
            log.warn("API에서 사라지거나 필터 조건에 맞지 않게 된 병원 {}개를 DB에서 삭제했습니다.", existingHospitalsMap.size());
        }

        log.info("병원 데이터 동기화가 성공적으로 완료되었습니다.");
    }

    // JsonNode로부터 신규 Hospital 엔티티 생성
    private Hospital createNewHospital(JsonNode node) {
        Hospital hospital = Hospital.builder()
                .openapiId(node.path("HPID").asText())
                .name(node.path("DUTYNAME").asText())
                .address(node.path("DUTYADDR").asText())
                .longitude(node.path("WGS84LON").asDouble())
                .latitude(node.path("WGS84LAT").asDouble())
                .build();

        List<HospitalHour> hours = parseHospitalHours(node, hospital);
        hospital.getHospitalHourList().addAll(hours);

        return hospital;
    }

    // 기존 Hospital 엔티티의 정보를 API 데이터 기준으로 업데이트
    private void updateHospital(Hospital hospital, JsonNode node) {
        hospital.updateDetails(
                node.path("DUTYNAME").asText(),
                node.path("DUTYADDR").asText(),
                node.path("WGS84LON").asDouble(),
                node.path("WGS84LAT").asDouble()
        );
        hospital.getHospitalHourList().clear();
        List<HospitalHour> newHours = parseHospitalHours(node, hospital);
        hospital.getHospitalHourList().addAll(newHours);
    }

    // 기존 병원 데이터와 API 데이터를 비교하여 변경 여부 확인
    private boolean isModified(Hospital hospital, JsonNode node) {
        return !Objects.equals(hospital.getName(), node.path("DUTYNAME").asText()) ||
                !Objects.equals(hospital.getAddress(), node.path("DUTYADDR").asText()) ||
                !Objects.equals(hospital.getLongitude(), node.path("WGS84LON").asDouble()) ||
                !Objects.equals(hospital.getLatitude(), node.path("WGS84LAT").asDouble());
    }

    // JsonNode에서 요일별 운영시간 정보를 파싱하여 HospitalHour 객체 리스트로 반환
    private List<HospitalHour> parseHospitalHours(JsonNode node, Hospital hospital) {
        List<HospitalHour> hours = new ArrayList<>();
        Day[] days = Day.values();

        for (int i = 0; i < days.length; i++) {
            String openTimeStr = node.path("DUTYTIME" + (i + 1) + "S").asText(null);
            String closeTimeStr = node.path("DUTYTIME" + (i + 1) + "C").asText(null);

            if (openTimeStr != null && closeTimeStr != null && !openTimeStr.isBlank() && !closeTimeStr.isBlank()) {
                hours.add(HospitalHour.builder()
                        .hospital(hospital)
                        .day(days[i])
                        .openTime(parseTime(openTimeStr))
                        .closeTime(parseTime(closeTimeStr))
                        .build());
            }
        }
        return hours;
    }

    // "0900"과 같은 문자열 시간을 LocalTime 객체로 반환
    private LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HHmm"));
    }

    // API 데이터 노드에 필수적인 정보가 비어있는지 검사
    private boolean isInvalidNode(JsonNode node) {
        // asText()는 필드가 없으면 ""를 반환하므로 isBlank()로 체크
        // asDouble()은 필드가 없거나 숫자가 아니면 0.0을 반환하므로 0.0인지 체크
        return node.path("DUTYNAME").asText().isBlank() ||
                node.path("DUTYADDR").asText().isBlank() ||
                node.path("WGS84LON").asDouble() == 0.0 ||
                node.path("WGS84LAT").asDouble() == 0.0;
    }

    // API 데이터 노드가 '서대문구'에 위치한 '병원', '의원'인지 필터링
    private boolean isTargetHospital(JsonNode node) {
        String dutyDivNam = node.path("DUTYDIVNAM").asText();
        String dutyAddr = node.path("DUTYADDR").asText();
        boolean isHospitalOrClinic = "병원".equals(dutyDivNam) || "의원".equals(dutyDivNam);
        return isHospitalOrClinic && dutyAddr.contains("서대문구");
    }
}
