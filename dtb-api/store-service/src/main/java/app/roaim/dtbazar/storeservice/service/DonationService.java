package app.roaim.dtbazar.storeservice.service;

import app.roaim.dtbazar.storeservice.domain.Donation;
import app.roaim.dtbazar.storeservice.dto.DonationDto;
import app.roaim.dtbazar.storeservice.jwt.JwtData;
import app.roaim.dtbazar.storeservice.repository.DonationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static app.roaim.dtbazar.storeservice.ThrowableUtil.denyDelete;
import static app.roaim.dtbazar.storeservice.ThrowableUtil.denyUpdate;
import static reactor.core.publisher.Mono.error;

// TODO move to a separate micro service
@Service
@AllArgsConstructor
public class DonationService {
    private final DonationRepository repository;
    private final IntercomService intercomService;

    public Mono<Donation> saveDonation(JwtData jwtData, DonationDto donationDto) {
        return intercomService.getStoreFoodById(donationDto.getStoreFoodId())
                .flatMap(storeFood -> {
                    Donation donation = donationDto.toDonation(jwtData.getSub(), jwtData.getName(), storeFood);
                    return repository.save(donation);
                });
    }

    public Mono<Donation> approveDonationById(String uid, String donationId) {
        return getDonationById(donationId).flatMap(donation -> intercomService.getStoreFoodById(donation.getStoreFoodId()).flatMap(storeFood -> {
            if (storeFood.getUid().equals(uid)) {
                donation.setEnabled(true);
                return intercomService.onAddDonation(donation, storeFood).then(repository.save(donation));
            } else return error(new ResponseStatusException(HttpStatus.FORBIDDEN, "You can't approve this donation."));
        }));
    }

    public Flux<Donation> getMyDonations(String uid, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return repository.findAllByDonorId(uid, pageable);
    }

    public Flux<Donation> getAllDonations(String storeId, String donorId, String storeFoodId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        if (storeId != null && donorId != null && storeFoodId != null) {
            return repository.findAllByStoreIdAndDonorIdAndStoreFoodId(storeId, donorId, storeFoodId, pageable);
        } else if (storeId != null && donorId != null) {
            return repository.findAllByStoreIdAndDonorId(storeId, donorId, pageable);
        } else if (donorId != null && storeFoodId != null) {
            return repository.findAllByDonorIdAndStoreFoodId(donorId, storeFoodId, pageable);
        } else if (storeId != null && storeFoodId != null) {
            return repository.findAllByStoreIdAndStoreFoodId(storeId, storeFoodId, pageable);
        } else if (storeId != null) {
            return repository.findAllByStoreId(storeId, pageable);
        } else if (donorId != null) {
            return repository.findAllByDonorId(donorId, pageable);
        } else if (storeFoodId != null) {
            return repository.findAllByStoreFoodId(storeFoodId, pageable);
        }
        return repository.findAllBy(pageable);
    }

    public Mono<Donation> getDonationById(String donationId) {
        return repository.findById(donationId).switchIfEmpty(
                error(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("DonationId: %s not found", donationId))
                )
        );
    }

    public Mono<Donation> deleteDonationById(String uid, String donationId) {
        return getDonationById(donationId).flatMap(donation -> {
            if (donation.getDonorId().equals(uid)) {
                return repository.deleteById(donationId)
                        .thenReturn(donation);
            } else {
                return error(denyDelete("donation"));
            }
        });
    }

    public Mono<Donation> updateDonationId(String uid, String donationId, DonationDto donationDto) {
        return getDonationById(donationId).flatMap(donation -> {
            if (donation.getDonorId().equals(uid)) {
                donation.setAmount(donationDto.getAmount());
                donation.setCurrency(donationDto.getCurrency());
                return repository.save(donation);
            } else {
                return error(denyUpdate("donation"));
            }
        });
    }
}
