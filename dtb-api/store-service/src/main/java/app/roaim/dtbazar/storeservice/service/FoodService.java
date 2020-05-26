package app.roaim.dtbazar.storeservice.service;

import app.roaim.dtbazar.storeservice.domain.Food;
import app.roaim.dtbazar.storeservice.domain.FoodDetail;
import app.roaim.dtbazar.storeservice.repository.FoodDetailRepository;
import app.roaim.dtbazar.storeservice.repository.FoodRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static app.roaim.dtbazar.storeservice.ThrowableUtil.*;
import static java.lang.String.format;
import static reactor.core.publisher.Mono.error;

// TODO move to a separate micro service
@Service
@AllArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodDetailRepository foodDetailRepository;

    public Mono<Food> saveFood(Food food) {
        return foodRepository.save(food).flatMap(f -> saveFoodDetail(new FoodDetail(f)).thenReturn(f));
    }

    public Mono<FoodDetail> saveFoodDetail(FoodDetail foodDetail) {
        return foodDetailRepository.save(foodDetail);
    }

    public Flux<Food> getFoods(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return foodRepository.findAllBy(pageable);
    }

    public Mono<FoodDetail> getFoodById(String foodId) {
        return foodDetailRepository.findFirstByFood_Id(foodId)
                .switchIfEmpty(
                        error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                format("FoodId: %s not found", foodId)))
                );
    }

    public Mono<Food> updateFoodById(String foodId, Food food) {
        return getFoodById(foodId).flatMap(fd -> {
            Food f = fd.getFood();
            if (f.getUid().equals(food.getUid())) {
                f.update(food);
                fd.update(f);
                return foodDetailRepository.save(fd)
                        .then(foodRepository.save(f));
            } else {
                return Mono.error(denyUpdate("food"));
            }
        });
    }

    public Mono<FoodDetail> deleteFoodById(String uid, String foodId) {
        return getFoodById(foodId).flatMap(fd -> {
            if (fd.getFood().getUid().equals(uid)) {
                double donationLeft = fd.getTotalDonation() - fd.getSpentDonation();
                return donationLeft <= 0 ? foodRepository.deleteById(foodId)
                        .then(foodDetailRepository.deleteById(fd.getId()))
                        .thenReturn(fd)
                        : error(denyLeftDonationDelete(fd.getFood().getName(), fd.getFood().getCurrency().name(), donationLeft));
            } else {
                return error(denyDelete("food"));
            }
        });
    }
}
