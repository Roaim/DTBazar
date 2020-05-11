package app.roaim.dtbazar.authservice.model;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class FbAccessTokenError {
    String error;
    String errorCode;
    String errorDescription;
    String errorReason;
}
