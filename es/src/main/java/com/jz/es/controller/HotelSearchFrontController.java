package com.jz.es.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@CrossOrigin //解决跨域问题
public class HotelSearchFrontController {
    //处理索引初始化请求
    @GetMapping("/hotel-search")
    public String hotelSearch() throws Exception {
        return "hotel-search";
    }
}
