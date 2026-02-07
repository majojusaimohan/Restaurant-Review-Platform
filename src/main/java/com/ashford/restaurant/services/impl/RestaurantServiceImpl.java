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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Page<Restaurant> searchRestuarantts(
            String query, Float minRating, Float latitude, Float longitude,
            Float radius, Pageable pageable) {

        if(null!=minRating && (null==query || query.isEmpty())){
            return restaurantRepository.findByAverageRatingGreaterThanEqual(minRating, pageable);
        }

        Float searchMinRating = null ==minRating?0f:minRating;

        if(null!=query && !query.trim().isEmpty()){
            return restaurantRepository.findByQueryAndMinRating(query, searchMinRating, pageable);
        }

        if(null!=latitude && null!=longitude && radius!=null){
            return restaurantRepository.findByLocationNear(latitude,longitude,radius,pageable);
        }

        return restaurantRepository.findAll(pageable);

    }

    @Override
    public Optional<Restaurant> getRestaurant(String id) {
       return restaurantRepository.findById(id);
    }
}
