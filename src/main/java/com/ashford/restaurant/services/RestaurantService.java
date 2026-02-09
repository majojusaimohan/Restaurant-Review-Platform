package com.ashford.restaurant.services;

import com.ashford.restaurant.domain.RestaurantCreateUpdateRequest;
import com.ashford.restaurant.domain.entities.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RestaurantService {

    Restaurant createRestaurant(RestaurantCreateUpdateRequest restaurantCreateUpdateRequest);

    Page<Restaurant> searchRestuarantts(
            String query,
            Float minRating,
            Float latitude,
            Float longitude,
            Float radius,
            Pageable pageable


    );

    Optional<Restaurant> getRestaurant(String id);

    Restaurant updateRestaurant(String id, RestaurantCreateUpdateRequest restaurantCreateUpdateRequest);

    void deleteRestaurant(String id);

}
