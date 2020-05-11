package app.roaim.dtbazar.authservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FbUserProfile{

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("name")
	private String name;

	@JsonProperty("location")
	private Location location;

	@JsonProperty("id")
	private String id;

	@JsonProperty("email")
	private String email;

	@JsonProperty("picture")
	private Picture picture;
}