package app.roaim.dtbazar.mediaservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class ErrorBody {

    @JsonProperty("path")
    String path;

    @JsonProperty("requestId")
    String requestId;

    @JsonProperty("error")
    String error;

    @JsonProperty("message")
    String message;

    @JsonProperty("timestamp")
    LocalDateTime timestamp;

    @JsonProperty("status")
    int status;

}