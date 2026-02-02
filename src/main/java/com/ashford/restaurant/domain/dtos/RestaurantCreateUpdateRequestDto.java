package com.ashford.restaurant.domain.dtos;


import com.ashford.restaurant.domain.entities.Address;
import com.ashford.restaurant.domain.entities.OperatingHours;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantCreateUpdateRequestDto {

    @NotBlank(message = "Restaurant Name is required")
    private String name;
    @NotBlank(message = "Cuisine Name is required")
    private String cuisineType;

    @NotBlank(message = "Contact Information Name is required")
    private String contactInformation;
    @Valid
    private AddressDto address;
    @Valid
    private OperatingHoursDto operatingHours;
    @Size(min=1, message ="Atleast one photo is required")
    private List<String> photoIds;
}
