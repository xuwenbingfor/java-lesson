// todo 参考：https://pan.baidu.com/s/1XQaHieTBIGOg5JeQbSDkyA（提取码0trh），完成demo编写
//package com.jz.es.controller;
//
//import com.alibaba.fastjson.JSONObject;
//import com.hotel.search.dto.request.HotelSearchRequest;
//import com.hotel.search.dto.response.HotelSearchResult;
//import com.hotel.search.service.HotelSearchService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//@Controller
//@CrossOrigin //解决跨域问题
//public class HotelSearchWebController {
//    @Autowired
//    HotelSearchService hotelSearchService;
//
//    //响应搜索动作
//    @PostMapping(value = "/start-search", produces = "application/json;charset=UTF-8")
//    @ResponseBody
//    public String startSearch(@RequestBody HotelSearchRequest hotelSearchRequest) throws Exception {
//        JSONObject result = new JSONObject();
//        //搜索酒店
//        HotelSearchResult hotelSearchResult = hotelSearchService.getCommonSearch(hotelSearchRequest);
//        //封装搜索结果
//        result.put("requestId", hotelSearchRequest.getRequestId());
//        result.put("searchResult", hotelSearchResult);
//        return result.toJSONString();//转换为Json字符串
//    }
//
//    //处理索引初始化请求
//    @GetMapping("/init-hotel-index")
//    public String initIndex() throws Exception {
//        hotelIndexService.startInit();
//        return "process sucesss!";
//    }
//}
