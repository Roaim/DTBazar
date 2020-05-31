package app.roaim.dtbazar.storeservice.controller;


import app.roaim.dtbazar.storeservice.domain.Donation;
import app.roaim.dtbazar.storeservice.dto.DonationDto;
import app.roaim.dtbazar.storeservice.jwt.JWTUtil;
import app.roaim.dtbazar.storeservice.jwt.JwtData;
import app.roaim.dtbazar.storeservice.service.DonationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfox.documentation.annotations.ApiIgnore;

// TODO move to a separate micro service
@RestController
@RequestMapping("/donation")
@AllArgsConstructor
public class DonationController {

    private final DonationService service;
    private final JWTUtil jwtUtil;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    Mono<Donation> saveDonation(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                @RequestBody DonationDto donationDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.saveDonation(jwtData, donationDto);
    }

    @GetMapping("/my")
    Flux<Donation> getMyDonations(
            @ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.getMyDonations(jwtData.getSub(), page, size);
    }

    @GetMapping("/pending")
    Flux<Donation> getPendingDonations(
            @RequestParam String storeId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getPendingDonation(storeId, page, size);
    }

    @GetMapping
    Flux<Donation> getDonations(
            @RequestParam(required = false) String storeId,
            @RequestParam(required = false) String donorId,
            @RequestParam(required = false) String storeFoodId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return service.getAllDonations(storeId, donorId, storeFoodId, page, size);
    }

    @GetMapping("/{donationId}")
    Mono<Donation> getDonationById(@PathVariable String donationId) {
        return service.getDonationById(donationId);
    }

    @PutMapping("/{donationId}")
    Mono<Donation> updateDonation(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                  @PathVariable String donationId, @RequestBody DonationDto donationDto) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.updateDonationId(jwtData.getSub(), donationId, donationDto);
    }

    @DeleteMapping("/{donationId}")
    Mono<Donation> deleteDonation(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                  @PathVariable String donationId) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.deleteDonationById(jwtData.getSub(), donationId);
    }

    @PatchMapping("/{donationId}")
    Mono<Donation> approveDonation(@ApiIgnore @RequestHeader(value = HttpHeaders.AUTHORIZATION) String bearerToken,
                                  @PathVariable String donationId) {
        JwtData jwtData = jwtUtil.decode(bearerToken);
        return service.approveDonationById(jwtData.getSub(), donationId);
    }
}
