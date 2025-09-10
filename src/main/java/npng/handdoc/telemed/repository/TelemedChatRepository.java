package npng.handdoc.telemed.repository;

import npng.handdoc.telemed.domain.TelemedChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TelemedChatRepository extends MongoRepository<TelemedChatLog, String> {
    Optional<TelemedChatLog> findByRoomId(String roomId);
}
