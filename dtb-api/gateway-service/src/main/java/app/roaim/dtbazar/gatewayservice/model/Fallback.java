package app.roaim.dtbazar.gatewayservice.model;

public class Fallback {
    private int status;
    private String message;

    public Fallback(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
