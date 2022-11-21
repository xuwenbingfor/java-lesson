package com.jz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hotel {
    private String title;
    private String city;
    private Double price;
    private String create_time;
    private String amenities;
    private boolean full_room;
    private Location location;
    private int praise;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Location {
        private double lat;
        private double lon;
    }
}
