package com.ashford.restaurant.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RestaurantSummaryDto {

    private String id;
    private String name;
    private  String cuisineType;
    private  String averageRating;
    private Float totalReviews;
    private AddressDto address;
    private List<PhotoDto> photos;
}
