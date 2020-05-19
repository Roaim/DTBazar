package app.roaim.dtbazar.storeservice.service;

import app.roaim.dtbazar.storeservice.ThrowableUtil;
import app.roaim.dtbazar.storeservice.domain.Store;
import app.roaim.dtbazar.storeservice.repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static app.roaim.dtbazar.storeservice.ThrowableUtil.denyDelete;
import static app.roaim.dtbazar.storeservice.ThrowableUtil.denyLeftDonationDelete;
import static reactor.core.publisher.Mono.error;

@Service
@AllArgsConstructor
public class StoreService {
    private final StoreRepository repository;

    public Mono<Store> saveStore(Store store) {
        return repository.save(store);
    }

    public Flux<Store> getMyStores(String sub, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        return repository.findAllByUid(sub, pageable);
    }

    public Flux<Store> getAllStores(int page, int size) {
        return repository.findAllBy(PageRequest.of(page, size));
    }

    public Mono<Store> getStoreById(String storeId) {
        return repository.findById(storeId)
                .switchIfEmpty(
                        error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                                String.format("StoreId: %s not found", storeId)))
                );
    }

    public Flux<Store> getStoresByName(String storeName, int page, int size) {
        return repository.findByNameStartsWithIgnoreCase(storeName, PageRequest.of(page, size));
    }

    public Flux<Store> getNearByStores(double lat, double lon, int page, int size) {
        return repository.findByLocationNear(new Point(lat, lon), PageRequest.of(page, size));
    }

    public Mono<Store> updateStoreId(String id, Store store) {
        return getStoreById(id)
                .map(s -> {
                    if (!s.getUid().equals(store.getUid()))
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this store");
                    s.update(store);
                    return s;
                })
                .flatMap(repository::save);
    }

    public Mono<Store> deleteStoreById(String id, String uid) {
        return getStoreById(id).flatMap(store -> {
            if (store.getUid().equals(uid)) {
                double leftDonation = store.getTotalDonation() - store.getSpentDonation();
                return leftDonation == 0 ? repository.deleteById(id).thenReturn(store) :
                        error(denyLeftDonationDelete(store.getName(), "", leftDonation));
            } else {
                return error(denyDelete(store.getName()));
            }
        });
    }
}
