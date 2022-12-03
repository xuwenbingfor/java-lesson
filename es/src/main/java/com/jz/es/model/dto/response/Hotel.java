package com.jz.es.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Hotel {
    private String title;
    private BigDecimal distance;
    private Double price;
    private int favourablePercent;
    private String impression;
    private String businessDistrict;
    private String address;
    private String pic;
    private String star;
}
