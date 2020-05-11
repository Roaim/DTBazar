package app.roaim.dtbazar.storeservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ThrowableUtil {
    public static ResponseStatusException denyUpdate(String resource) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this " + resource);
    }

    public static ResponseStatusException denyDelete(String resource) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this " + resource);
    }
}
