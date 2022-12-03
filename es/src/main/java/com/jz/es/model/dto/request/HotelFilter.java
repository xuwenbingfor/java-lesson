package com.jz.es.model.dto.request;

import lombok.Data;

@Data
public class HotelFilter {
    private Integer distance;
    private  Double lowPrice;
    private  Double highPrice;
    private String star;
}
