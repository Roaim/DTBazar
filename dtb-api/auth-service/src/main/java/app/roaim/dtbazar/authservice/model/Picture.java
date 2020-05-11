package app.roaim.dtbazar.authservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.Data
public class Picture {

    @JsonProperty("data")
    public Data data;

}