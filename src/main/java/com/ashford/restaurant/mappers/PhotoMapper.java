package com.ashford.restaurant.mappers;

import com.ashford.restaurant.domain.dtos.PhotoDto;
import com.ashford.restaurant.domain.entities.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PhotoMapper {

    PhotoDto toDto (Photo photo);

}
