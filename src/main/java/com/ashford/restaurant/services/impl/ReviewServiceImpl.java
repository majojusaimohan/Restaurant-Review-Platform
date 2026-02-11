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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public Page<Review> listReviews(String restaurantId, Pageable pageable) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        List<Review> reviews = restaurant.getReviews();
        Sort sort = pageable.getSort();

       if(sort.isSorted()){

           Sort.Order order= sort.iterator().next();
           String property = order.getProperty();
           boolean isAscending = order.getDirection().isAscending();

           Comparator<Review> reviewComparator = switch (property) {
               case "datePosted" -> Comparator.comparing(Review::getDatePosted);
               case "rating" -> Comparator.comparing(Review::getRating);
               default -> Comparator.comparing(Review::getDatePosted);
           };


           reviews.sort(isAscending? reviewComparator : reviewComparator.reversed());
       }
       else{
           reviews.sort(Comparator.comparing(Review::getDatePosted).reversed());
       }

       int start = (int) pageable.getOffset();

       if(start >=reviews.size()){
           return new PageImpl<>(Collections.emptyList(),pageable,reviews.size());
       }

       int end =Math.min((start+pageable.getPageSize()),reviews.size());

       return new PageImpl<>(reviews.subList(start,end),pageable,reviews.size());


    }

    @Override
    public Optional<Review> getReview(String restaurantId, String reviewId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
      return   getReviewFromRestaurant(reviewId, restaurant);

    }

    private static Optional<Review> getReviewFromRestaurant(String reviewId, Restaurant restaurant){
        return restaurant.getReviews().stream()
                .filter(r->reviewId.equals(r.getId()))
                .findFirst();
    }

    @Override
    public Review updateReview(User author, String restaurantId, String reviewId, ReviewCreateUpdateRequest review) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);


        Review existingReview = getReviewFromRestaurant(reviewId, restaurant)
                .orElseThrow(() -> new ReviewNotAllowedException("Review with id not found:" + reviewId));

        String authorId = author.getId();

        if(!authorId.equals(existingReview.getWrittenBy().getId())){
            throw new ReviewNotAllowedException("Cannot update a review written by another user.");
        }

        if(LocalDateTime.now().isAfter(existingReview.getDatePosted().plusHours(48))){
            throw new ReviewNotAllowedException("Cannot update a review after 48 hours of posting.");
        }

        existingReview.setContent(review.getContent());
        existingReview.setRating(review.getRating());
        existingReview.setLastEdited(LocalDateTime.now());

       existingReview.setPhotos(review.getPhotoIds().stream()
                       .map(photoId->Photo.builder()
                               .url(photoId)
                                 .uploadDate(LocalDateTime.now())
                                 .build()).toList());
       
       


        List<Review> updatedReviews = restaurant.getReviews().stream().filter(r -> !reviewId.equals(r.getId())).collect(Collectors.toList());
        updatedReviews.add(existingReview);
        restaurant.setReviews(updatedReviews);

        restaurant.setReviews(updatedReviews);
        updateRestuarantAverageRating(restaurant);
        restaurantRepository.save(restaurant);
        return existingReview;



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
