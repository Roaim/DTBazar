package app.roaim.dtbazar.storeservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static java.lang.String.format;

public class ThrowableUtil {
    public static ResponseStatusException denyUpdate(String resource) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this " + resource);
    }

    public static ResponseStatusException denyDelete(String resource) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this " + resource);
    }

    public static ResponseStatusException denyLeftDonationDelete(String name, String currency, double leftDonation) {
        return new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                format("%s can not be deleted. %s has %s %s donation left.", name, name, currency, leftDonation)
        );
    }
}
