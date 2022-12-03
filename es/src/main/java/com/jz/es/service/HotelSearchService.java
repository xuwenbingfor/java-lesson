//package com.jz.es.service;
//
//import com.hotel.search.constant.HotelSearchConstant;
//import com.hotel.search.dto.response.Hotel;
//import com.hotel.search.dto.response.HotelAgg;
//import com.hotel.search.dto.request.HotelSearchRequest;
//import com.hotel.search.dto.response.HotelSearchResult;
//import com.hotel.search.util.GeoPointUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.action.search.SearchRequest;
//import org.elasticsearch.action.search.SearchResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.client.core.CountRequest;
//import org.elasticsearch.client.core.CountResponse;
//import org.elasticsearch.search.SearchHit;
//import org.elasticsearch.search.SearchHits;
//import org.elasticsearch.search.aggregations.Aggregations;
//import org.elasticsearch.search.aggregations.bucket.range.ParsedGeoDistance;
//import org.elasticsearch.search.aggregations.bucket.range.Range;
//import org.elasticsearch.search.aggregations.bucket.terms.Terms;
//import org.elasticsearch.search.aggregations.metrics.*;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class HotelSearchService {
//    @Autowired
//    EsQueryService esQueryService;
//    @Autowired
//    RestHighLevelClient client;
//
//    public HotelSearchResult getCommonSearch(HotelSearchRequest hotelSearchRequest){
//        HotelSearchResult hotelSearchResult=new HotelSearchResult();//创建//搜索结果对象
//        hotelSearchResult.setPageNo(hotelSearchRequest.getPageNo());//设置当前页码
//        long searchCount=getTogalCount(hotelSearchRequest);//根据搜索条件获取总页数
//        hotelSearchResult.setTotalCount(searchCount);
//        //开始进行搜索
//        SearchSourceBuilder searchSourceBuilder= esQueryService.getSearchQuery(hotelSearchRequest);//根据搜索条件创建搜索DSL
//        //定义需要返回的字段数组
//        String []fields=new String[]{"title","price","favourable_percent","impression","business_district","address","pic","location","star"};
//        //设定需要返回的字段数组
//        searchSourceBuilder.fetchSource(fields,null);
//        SearchRequest searchRequest = new SearchRequest("hotel");//新建搜索请求
//        searchRequest.source(searchSourceBuilder);
//        try {
//            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);//执行搜索
//            //对搜索结果进行处理和封装
//            List<Hotel> hotelList=getListFromEsResult(searchResponse,hotelSearchRequest.getLat(),hotelSearchRequest.getLon());
//            hotelSearchResult.setHotelList(hotelList);
//            Aggregations aggregations = searchResponse.getAggregations();//获取聚合结果
//            //对聚合结果进行处理和封装
//            Map<String,HotelAgg> aggMap= getAggMapFromEsAgg(aggregations);
//            hotelSearchResult.setAggMap(aggMap);
//        } catch (Exception e) {
//            log.error(e.getMessage(),e);
//        }
//        return hotelSearchResult;
//    }
//
//
//    public List<Hotel>  getListFromEsResult(SearchResponse searchResponse,double reqLat,double reqLon){
//        List<Hotel> hotelList=new ArrayList<Hotel>();
//        SearchHits searchHits = searchResponse.getHits();//获取搜索结果集
//        for (SearchHit searchHit : searchHits) {//遍历搜索结果集
//            Map sourceMap=searchHit.getSourceAsMap();
//            Hotel hotel=new Hotel();
//            hotel.setTitle((String)sourceMap.get("title"));
//            //获取酒店经纬度
//            Map<String,Double> location=(Map<String,Double>)sourceMap.get("location");
//            double hotelLat=location.get("lat");
//            double hotelLon=location.get("lon");
//            //计算酒店和用户的距离
//            double distancePre=GeoPointUtils.getGeoMeter(hotelLat,hotelLon,reqLat,reqLon);
//            BigDecimal distance=new BigDecimal(distancePre).setScale(2, RoundingMode.HALF_UP);
//            hotel.setDistance(distance);
//            //获取和封装酒店其他属性数据
//            hotel.setPrice((double)sourceMap.get("price"));
//            hotel.setFavourablePercent((int)sourceMap.get("favourable_percent"));
//            hotel.setImpression((String)sourceMap.get("impression"));
//            hotel.setBusinessDistrict((String)sourceMap.get("business_district"));
//            hotel.setAddress((String) sourceMap.get("address"));
//            hotel.setPic((String) sourceMap.get("pic"));
//            hotel.setStar((String) sourceMap.get("star"));
//            hotelList.add(hotel);
//        }
//        return hotelList;
//    }
//
//
//    public Map<String,HotelAgg>  getAggMapFromEsAgg(Aggregations aggregations){
//        Map<String,HotelAgg> aggMap=new HashMap<String,HotelAgg>();
//        Stats priceStats = aggregations.get(HotelSearchConstant.priceAggName);//获取s价格聚合返回的对象
//        //创建封装价格聚合的对象
//        HotelAgg hotelPriceAgg=new HotelAgg();
//        hotelPriceAgg.setMax(priceStats.getMax()-priceStats.getMax()%100+100);//获取价格聚合最大值
//        hotelPriceAgg.setMin(priceStats.getMin()-priceStats.getMin()%100+100);//获取价格聚合最小值
//        hotelPriceAgg.setAvg(priceStats.getAvg()-priceStats.getAvg()%100+100);//获取价格聚合平均值
//        aggMap.put("price",hotelPriceAgg);
//        //创建封装星级聚合的对象
//        HotelAgg hotelStarAgg=new HotelAgg();
//        Map<String,Long> starDocCountMap=new HashMap<String,Long>();
//        hotelStarAgg.setDocCountMap(starDocCountMap);
//        Terms terms = aggregations.get(HotelSearchConstant.starAggName);//获取星级聚合返回的对象
//        for (Terms.Bucket bucket : terms.getBuckets()) {
//            String bucketKey = bucket.getKeyAsString();//获取桶名称
//            long docCount = bucket.getDocCount();//获取文档个数
//            starDocCountMap.put(bucketKey,docCount);
//        }
//        aggMap.put("star",hotelStarAgg);
//        //创建封装距离聚合的对象
//        HotelAgg hotelDistanceAgg=new HotelAgg();
//        Map<String,Long> distanceDocCountMap=new HashMap<String,Long>();
//        ParsedGeoDistance range = aggregations.get(HotelSearchConstant.distanceAggName);
//        for (Range.Bucket  bucket : range.getBuckets()) {
//            String bucketKey = bucket.getKeyAsString();//获取bucket名称的字符串形式
//            long docCount = bucket.getDocCount();//获取文档个数
//            distanceDocCountMap.put(bucketKey,docCount);
//        }
//        hotelDistanceAgg.setDocCountMap(distanceDocCountMap);
//        aggMap.put("distance",hotelDistanceAgg);
//        return aggMap;
//    }
//
//
//
//    public long getTogalCount(HotelSearchRequest hotelSearchRequest) {
//        CountRequest countRequest = new CountRequest("hotel");//客户端count请求
//        SearchSourceBuilder searchSourceBuilder=esQueryService.getSearchCountQuery( hotelSearchRequest);
//        countRequest.source(searchSourceBuilder);//设置查询
//        try {
//            CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);//执行count
//            return countResponse.getCount();//返回count结果
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//}
