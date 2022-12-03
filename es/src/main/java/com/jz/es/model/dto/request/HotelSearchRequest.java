package com.jz.es.model.dto.request;

import lombok.Data;

@Data
public class HotelSearchRequest {
    private String requestId;
    private String city;
    private String keyword;
    private double lat;
    private double lon;
    private int pageNo;
    private int pageSize;
    private HotelFilter hotelFilter;
    private HotelSort hotelSort;
}
