package com.ashford.restaurant.services.impl;

import com.ashford.restaurant.domain.ReviewCreateUpdateRequest;
import com.ashford.restaurant.domain.entities.Photo;
import com.ashford.restaurant.domain.entities.Restaurant;
import com.ashford.restaurant.domain.entities.Review;
import com.ashford.restaurant.domain.entities.User;
import com.ashford.restaurant.exceptions.RestaurantNotFoundException;
import com.ashford.restaurant.exceptions.ReviewNotAllowedException;
import com.ashford.restaurant.repositories.RestaurantRepository;
import com.ashford.restaurant.services.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final RestaurantRepository restaurantRepository;

    @Override
    public Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review) {

        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        boolean hasExistingReview = restaurant.getReviews().stream()
                .anyMatch(r -> r.getWrittenBy().getId().equals(author.getId()));

        if(hasExistingReview){
            throw  new ReviewNotAllowedException("User has already reviewed this restaurant.");
        }

        LocalDateTime now = LocalDateTime.now();

        List<Photo> photos = review.getPhotoIds().stream().map(url -> {

            return Photo.builder()
                    .url(url)
                    .uploadDate(now)
                    .build();
        }).toList();
        String reviewId = UUID.randomUUID().toString();
        Review reviewToCreate = Review.builder()
                .id(reviewId)
                .content(review.getContent())
                .rating(review.getRating())
                .datePosted(now)
                .lastEdited(now)
                .photos(photos)
                .writtenBy(author)
                .build();

        restaurant.getReviews().add(reviewToCreate);
        updateRestuarantAverageRating(restaurant);
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);

        return savedRestaurant.getReviews().stream()
                .filter(r->reviewId.equals(r.getId()))
                .findFirst()
                .orElseThrow(()->new RuntimeException("Error retrieving the created review."));


    }

    private Restaurant getRestaurantOrThrow(String restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant with id not found:" + restaurantId));
    }

    private void updateRestuarantAverageRating(Restaurant restaurant) {
        List<Review> reviews = restaurant.getReviews();
        if(reviews.isEmpty()){
            restaurant.setAverageRating(0.0f);
        }
        double average = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        restaurant.setAverageRating((float) average);
    }
}
