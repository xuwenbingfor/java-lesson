package com.jz.es.model.entity;

import lombok.Data;

/**
 * 酒店搜索建议
 */
@Data
public class HotelSug {
    private int id;
    private Field chinese;
    private Field head_pinyin;
    private Field full_pinyin;

    @Data
    public static class Field {
        private int weight;
        private String input;
    }
}
