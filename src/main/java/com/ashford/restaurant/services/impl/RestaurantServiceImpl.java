package com.ashford.restaurant.services.impl;

import com.ashford.restaurant.domain.GeoLocation;
import com.ashford.restaurant.domain.RestaurantCreateUpdateRequest;
import com.ashford.restaurant.domain.entities.Address;
import com.ashford.restaurant.domain.entities.Photo;
import com.ashford.restaurant.domain.entities.Restaurant;
import com.ashford.restaurant.repositories.RestaurantRepository;
import com.ashford.restaurant.services.GeoLocationService;
import com.ashford.restaurant.services.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final GeoLocationService geoLocationService;



    @Override
    public Restaurant createRestaurant(RestaurantCreateUpdateRequest restaurantCreateUpdateRequest) {
        Address address= restaurantCreateUpdateRequest.getAddress();
        GeoLocation geoLocation=geoLocationService.geoLocate(address);
        GeoPoint geoPoint=new GeoPoint(geoLocation.getLatitude(),geoLocation.getLongitude());

        List<String> photoIds = restaurantCreateUpdateRequest.getPhotoIds();
        List<Photo> photos = photoIds.stream().map(photoUrl -> Photo.builder()
                .url(photoUrl)
                .uploadDate(LocalDateTime.now())
                .build()).toList();

        Restaurant restaurant= Restaurant.builder()
                .name(restaurantCreateUpdateRequest.getName())
                .cuisineType(restaurantCreateUpdateRequest.getCuisineType())
                .contactInformation(restaurantCreateUpdateRequest.getContactInformation())
                .address(address)
                .geoLocation(geoPoint)
                .operatingHours(restaurantCreateUpdateRequest.getOperatingHours())
                .averageRating(0f)
                .photos(photos)
                .build();

        return restaurantRepository.save(restaurant);

    }
}
