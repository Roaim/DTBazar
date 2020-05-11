package app.roaim.dtbazar.mediaservice.repository;

import app.roaim.dtbazar.mediaservice.domain.MediaFile;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface MediaRepository extends ReactiveMongoRepository<MediaFile, String> {
    Flux<MediaFile> deleteAllByUid(String uid);
	Flux<MediaFile> findAllByUid(String uid, Pageable pageable);
}
