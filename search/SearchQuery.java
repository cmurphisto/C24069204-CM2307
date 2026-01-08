package studentrentals.search;

import studentrentals.model.property.RoomType;

import java.time.LocalDate;
import java.util.Optional;

public record SearchQuery(
        Optional<String> cityOrArea,
        Optional<Integer> minPrice,
        Optional<Integer> maxPrice,
        Optional<LocalDate> startDate,
        Optional<LocalDate> endDate,
        Optional<RoomType> roomType
) {}
