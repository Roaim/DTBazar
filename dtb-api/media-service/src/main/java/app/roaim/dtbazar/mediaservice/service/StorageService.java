package app.roaim.dtbazar.mediaservice.service;

import app.roaim.dtbazar.mediaservice.domain.MediaFile;
import org.springframework.core.io.Resource;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StorageService {

    void init();

    Mono<String> getContentType(String id);

    Mono<MediaFile> saveFile(FilePart file, String uid);

    Mono<Resource> getFile(String id);
	
	Flux<MediaFile> getAllByUid(String uid, int page, int size);

    Flux<MediaFile> deleteAllByUid(String uid);

    Mono<MediaFile> deleteById(String id, String uid);
}
