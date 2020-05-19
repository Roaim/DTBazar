package app.roaim.dtbazar.authservice.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "user")
@NoArgsConstructor
public class User {
    @Id
    private String id;
    @Indexed
    private String fbId;
    private String name;
    private String email;
    private String gender;
    private String fbLocationId;
    private String fbLocationName;
    private String fbProfilePicture;
    @Indexed
    private String fbAccessToken;
    private boolean admin = false;
    @Indexed
    private boolean enabled = true;
    private String ip;
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

    @PersistenceConstructor
    public User(String fbId, String name, String email, String gender, String fbLocationId, String fbLocationName,
                String fbProfilePicture, String fbAccessToken, String ip) {
        super();
        update(fbId, name, email, gender, fbLocationId, fbLocationName, fbProfilePicture, fbAccessToken, ip);
    }

    public void update(String fbId, String name, String email, String gender, String fbLocationId, String fbLocationName,
                       String fbProfilePicture, String fbAccessToken, String ip) {
        this.fbId = fbId;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.fbLocationId = fbLocationId;
        this.fbLocationName = fbLocationName;
        this.fbProfilePicture = fbProfilePicture;
        this.fbAccessToken = fbAccessToken;
        this.ip = ip;
    }
}
