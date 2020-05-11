package app.roaim.dtbazar.authservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IpInfo{

	@JsonProperty("zip")
	private String zip;

	@JsonProperty("country")
	private String country;

	@JsonProperty("city")
	private String city;

	@JsonProperty("org")
	private String org;

	@JsonProperty("timezone")
	private String timezone;

	@JsonProperty("regionName")
	private String regionName;

	@JsonProperty("isp")
	private String isp;

	@JsonProperty("query")
	private String query;

	@JsonProperty("lon")
	private double lon;

	@JsonProperty("as")
	private String as;

	@JsonProperty("countryCode")
	private String countryCode;

	@JsonProperty("region")
	private String region;

	@JsonProperty("lat")
	private double lat;

	@JsonProperty("status")
	private String status;
}