package npng.handdoc.telemed.util.naver;

import com.nbp.cdncp.nest.grpc.proto.v1.NestServiceGrpc;
import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.MetadataUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLException;

@Configuration
public class NaverGrpcClient {

    @Value("${naver.clova.api-key-id}")
    private String apiKeyId;

    @Value("${naver.clova.api-key}")
    private String apiKey;

    @Value("${naver.clova.grpc.host}")
    private String host;

    @Value("${naver.clova.grpc.port}")
    private int port;

    /**
     * TLS gRPC 채널
     */
    @Bean
    public ManagedChannel clovaChannel() throws SSLException {
        return NettyChannelBuilder
                .forAddress(host, port)
                .sslContext(GrpcSslContexts.forClient().build())
                .build();
    }

    /**
     * NCP API Gateway 인증 헤더
     */
    @Bean
    public Metadata clovaMetadata() {
        Metadata md = new Metadata();
        Metadata.Key<String> KEY_ID = Metadata.Key.of("X-NCP-APIGW-API-KEY-ID", Metadata.ASCII_STRING_MARSHALLER);
        Metadata.Key<String> KEY = Metadata.Key.of("X-NCP-APIGW-API-KEY", Metadata.ASCII_STRING_MARSHALLER);
        md.put(KEY_ID, apiKeyId);
        md.put(KEY, apiKey);
        return md;
    }

    /**
     * 인증 헤더를 자동 첨부하는 인터셉터
     */
    @Bean
    public ClientInterceptor clovaAuthInterceptor(Metadata clovaMetadata) {
        return MetadataUtils.newAttachHeadersInterceptor(clovaMetadata);
    }

    /**
     * 인증 인터셉터가 붙은 비동기 Stub
     */
    @Bean
    public NestServiceGrpc.NestServiceStub nestServiceStub(ManagedChannel ch, ClientInterceptor auth) {
        Channel authed = ClientInterceptors.intercept(ch, auth);
        return NestServiceGrpc.newStub(authed);
    }
}
