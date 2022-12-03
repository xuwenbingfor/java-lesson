package com.jz.es.model.dto.response;

import lombok.Data;

import java.util.Map;

@Data
public class HotelAgg {
    private double max;
    private double min;
    private double avg;
    private Map<String,Long> docCountMap;
}
