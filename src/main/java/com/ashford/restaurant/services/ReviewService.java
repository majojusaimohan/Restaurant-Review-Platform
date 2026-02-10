package com.ashford.restaurant.services;

import com.ashford.restaurant.domain.ReviewCreateUpdateRequest;
import com.ashford.restaurant.domain.entities.Review;
import com.ashford.restaurant.domain.entities.User;

public interface ReviewService {

    Review createReview(User author, String restaurantId, ReviewCreateUpdateRequest review);
}
