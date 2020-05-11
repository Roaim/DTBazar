package app.roaim.dtbazar.authservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Location{

	@JsonProperty("name")
	private String name;

	@JsonProperty("id")
	private String id;
}