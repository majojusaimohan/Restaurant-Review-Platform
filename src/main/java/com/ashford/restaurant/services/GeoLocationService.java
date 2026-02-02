package com.ashford.restaurant.services;

import com.ashford.restaurant.domain.GeoLocation;
import com.ashford.restaurant.domain.entities.Address;

public interface GeoLocationService {
    GeoLocation geoLocate(Address address);
}
