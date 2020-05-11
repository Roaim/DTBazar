package app.roaim.dtbazar.mediaservice.domain;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "media")
public class MediaFile {
    @Id
    private String id;
    private String name;
    private String contentType;
    private long contentLength;
    private String url;
    @Indexed
	@JsonIgnore
    private String uid;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @PersistenceConstructor
    public MediaFile(String name, String uid) {
        super();
        this.name = name;
        this.uid = uid;
    }
}
