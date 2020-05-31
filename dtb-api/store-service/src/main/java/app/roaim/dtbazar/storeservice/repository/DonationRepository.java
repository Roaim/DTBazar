package app.roaim.dtbazar.storeservice.repository;

import app.roaim.dtbazar.storeservice.domain.Donation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

// TODO move to a separate micro service
public interface DonationRepository extends ReactiveMongoRepository<Donation, String> {
    Flux<Donation> findAllBy(Pageable pageable);

    Flux<Donation> findAllByStoreId(String storeId, Pageable pageable);

    Flux<Donation> findAllByStoreIdAndEnabledFalseOrderByIdDesc(String storeId, Pageable pageable);

    Flux<Donation> findAllByDonorId(String donorId, Pageable pageable);

    Flux<Donation> findAllByStoreFoodId(String foodId, Pageable pageable);

    Flux<Donation> findAllByStoreIdAndDonorIdAndStoreFoodId(String storeId, String donorId, String foodId, PageRequest pageable);

    Flux<Donation> findAllByStoreIdAndDonorId(String storeId, String donorId, PageRequest pageable);

    Flux<Donation> findAllByDonorIdAndStoreFoodId(String donorId, String foodId, PageRequest pageable);

    Flux<Donation> findAllByStoreIdAndStoreFoodId(String storeId, String foodId, PageRequest pageable);
}
