package app.roaim.dtbazar.mediaservice.controller;

import app.roaim.dtbazar.mediaservice.domain.MediaFile;
import app.roaim.dtbazar.mediaservice.jwt.JWTUtil;
import app.roaim.dtbazar.mediaservice.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/media")
@AllArgsConstructor
public class MediaController {
    private final StorageService storageService;
    private final JWTUtil jwtUtil;

    @PostMapping
	@ResponseStatus(HttpStatus.CREATED)
    Mono<MediaFile> uploadFile(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                               @ApiIgnore @RequestPart("file") FilePart file,
                               @RequestParam(value = "file", required = false) MultipartFile ignore /*used for swagger doc*/) {
        String uid = jwtUtil.decode(bearerToken).getSub();
        return storageService.saveFile(file, uid);
    }

    @GetMapping("/{id}")
    ResponseEntity<Resource> getFile(@PathVariable String id) {
		return storageService.getContentType(id).flatMap(contentType -> 
					storageService.getFile(id).map(resource -> ResponseEntity.ok()
						.contentType(MediaType.parseMediaType(contentType))
						.cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
						// Uncomment to make the file downloadable instead of inline
                		.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
						.body(resource)
					)
				).block();
    }
	
	@GetMapping
    Flux<MediaFile> getAllUserMedia(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
							@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size
	) {
        String uid = jwtUtil.decode(bearerToken).getSub();
        return storageService.getAllByUid(uid, page, size);
    }

    @DeleteMapping
    Flux<MediaFile> deleteAllUserMedia(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken) {
        String uid = jwtUtil.decode(bearerToken).getSub();
        return storageService.deleteAllByUid(uid);
    }

    @DeleteMapping("/{id}")
    Mono<MediaFile> deleteById(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                               @PathVariable String id) {
        String uid = jwtUtil.decode(bearerToken).getSub();
        return storageService.deleteById(id, uid);
    }


}
