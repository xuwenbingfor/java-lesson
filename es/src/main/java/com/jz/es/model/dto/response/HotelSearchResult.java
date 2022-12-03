package com.jz.es.model.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class HotelSearchResult {
    private long totalCount;
    private int pageNo;
    private List<Hotel> hotelList;
    private Map<String,HotelAgg> aggMap;
}
