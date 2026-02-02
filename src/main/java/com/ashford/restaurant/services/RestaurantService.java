package com.ashford.restaurant.services;

import com.ashford.restaurant.domain.RestaurantCreateUpdateRequest;
import com.ashford.restaurant.domain.entities.Restaurant;

public interface RestaurantService {

    Restaurant createRestaurant(RestaurantCreateUpdateRequest restaurantCreateUpdateRequest);
}
