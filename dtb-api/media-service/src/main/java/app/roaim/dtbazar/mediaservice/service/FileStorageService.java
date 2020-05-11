package app.roaim.dtbazar.mediaservice.service;

import app.roaim.dtbazar.mediaservice.domain.MediaFile;
import app.roaim.dtbazar.mediaservice.repository.MediaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RefreshScope
public class FileStorageService implements StorageService {
    @Value("${dtbazar.file.upload-dir}")
    private String uploadDir;
    @Value("${dtbazar.url.scheme}")
    private String scheme;
    @Value("${dtbazar.gateway.host}")
    private String host;
    @Value("${dtbazar.api.version}")
    private String apiVersion;

    private Path fileStorageLocation;
    private final MediaRepository mediaRepository;

    public FileStorageService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public Path getFileStorageLocation() {
        if (fileStorageLocation == null) {
            init();
        }
        return fileStorageLocation;
    }

    @Override
    public void init() {
        System.out.println("Upload dir: " + uploadDir);
        this.fileStorageLocation = Paths.get(uploadDir)
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
	
	private Mono<MediaFile> getMediaFileById(String id) {
		return mediaRepository.findById(id)
					.switchIfEmpty(Mono.error(
						new ResponseStatusException(HttpStatus.NOT_FOUND, "MediaFile does not exist")
					)
		);
	}

    @Override
    public Mono<String> getContentType(String id) {
        return getMediaFileById(id)
					.map(MediaFile::getContentType);
    }

    @Override
    public Mono<MediaFile> saveFile(FilePart file, String uid) {
        return mediaRepository.save(new MediaFile(file.filename(), uid)).flatMap(mediaFile -> {
            String id = mediaFile.getId();
            Path path = getFileStorageLocation().resolve(id);
            return file.transferTo(path).doOnSuccess(ignore -> {
                mediaFile.setUrl(getMediaUrl(id));
                try {
                    mediaFile.setContentLength(path.toFile().length());
                    String contentType = getContentType(file.filename(), path);
                    mediaFile.setContentType(contentType);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).thenReturn(mediaFile);
        }).flatMap(mediaRepository::save);
    }

    private String getMediaUrl(String id) {
        return String.format("%s://%s/api/%s/media/%s", scheme, host, apiVersion, id);
    }

    @Override
    public Mono<Resource> getFile(String id) {
        return Mono.fromCallable(() -> {
            Path resolve = getFileStorageLocation().resolve(id);
            URI uri = resolve.toUri();
            UrlResource urlResource = new UrlResource(uri);
            if (!urlResource.exists())
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Media does not exist");
            return urlResource;
        });
    }
	
	@Override
	public Flux<MediaFile> getAllByUid(String uid, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return mediaRepository.findAllByUid(uid, pageable);
	}

    @Override
    public Flux<MediaFile> deleteAllByUid(String uid) {
        return mediaRepository.deleteAllByUid(uid).doOnNext(this::deleteMediaFile);
    }

    @Override
    public Mono<MediaFile> deleteById(String id, String uid) {
        return getMediaFileById(id).flatMap(mediaFile -> {
            if (uid.equals(mediaFile.getUid())) {
                return mediaRepository.deleteById(id).thenReturn(mediaFile);
            } else {
                return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this media"));
            }
        }).doOnSuccess(this::deleteMediaFile);
    }

    private void deleteMediaFile(MediaFile mediaFile) {
        Path path = getFileStorageLocation().resolve(mediaFile.getId());
        if (path.toFile().delete()) {
            mediaFile.setUrl("DELETED");
        }
    }

    private String getContentType(String filename, Path path) throws IOException {
        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = URLConnection.guessContentTypeFromName(filename);
        }
        if (contentType == null) {
            contentType = path.toUri().toURL().openConnection().getContentType();
        }
        return contentType;
    }
}
