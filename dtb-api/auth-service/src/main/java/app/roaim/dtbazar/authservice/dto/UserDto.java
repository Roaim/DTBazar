package app.roaim.dtbazar.authservice.dto;

import app.roaim.dtbazar.authservice.domain.User;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class UserDto {
    String id;
    String name;
    String email;
    String gender;
    String fbLocationName;
    String fbProfilePicture;
    LocalDateTime createdAt;

    public static UserDto fromUser(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getGender(),
                user.getFbLocationName(), user.getFbProfilePicture(), user.getCreatedAt());
    }
}
