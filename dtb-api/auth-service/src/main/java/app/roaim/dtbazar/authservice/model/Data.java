package app.roaim.dtbazar.authservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Data {

    @JsonProperty("url")
    public String url;

}