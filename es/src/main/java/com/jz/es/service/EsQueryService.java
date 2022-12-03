//package com.jz.es.service;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import com.hotel.search.constant.HotelSearchConstant;
//import com.hotel.search.dto.request.HotelFilter;
//import com.hotel.search.dto.request.HotelSearchRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.geo.GeoPoint;
//import org.elasticsearch.common.unit.DistanceUnit;
//import org.elasticsearch.index.query.BoolQueryBuilder;
//import org.elasticsearch.index.query.QueryBuilder;
//import org.elasticsearch.index.query.QueryBuilders;
//import org.elasticsearch.index.query.functionscore.ScriptScoreQueryBuilder;
//import org.elasticsearch.script.Script;
//import org.elasticsearch.search.aggregations.AggregationBuilder;
//import org.elasticsearch.search.aggregations.AggregationBuilders;
//import org.elasticsearch.search.aggregations.bucket.range.GeoDistanceAggregationBuilder;
//import org.elasticsearch.search.aggregations.bucket.range.Range;
//import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
//import org.elasticsearch.search.aggregations.metrics.*;
//import org.elasticsearch.search.builder.SearchSourceBuilder;
//import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
//import org.elasticsearch.search.sort.SortBuilders;
//import org.elasticsearch.search.sort.SortOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class EsQueryService {
////    @Autowired
////    RestHighLevelClient client;
//
//    @Autowired
//    ElasticsearchClient client;
//
//    public SearchSourceBuilder getSearchQuery(HotelSearchRequest hotelSearchRequest) {
//        BoolQueryBuilder boolQuery = getFilterQuery(hotelSearchRequest);//构建过滤条件
//        Script script = getScoreScript(hotelSearchRequest);//构建打分脚本
//        ScriptScoreQueryBuilder scriptScoreQueryBuilder = QueryBuilders.scriptScoreQuery(boolQuery, script);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
//        List<AggregationBuilder> aggs = getAggs(hotelSearchRequest);//构建聚合内容
//        // 1. 构造聚合
//        for (AggregationBuilder agg : aggs) {
//            searchSourceBuilder.aggregation(agg);
//        }
//        // 2. 构造查询
//        searchSourceBuilder.query(scriptScoreQueryBuilder);
//        setSort(searchSourceBuilder, hotelSearchRequest);//构建排序规则
//        setFromSize(searchSourceBuilder, hotelSearchRequest);//构建翻页请求
//        log.info("Query DSL:{}", searchSourceBuilder.toString());
//        return searchSourceBuilder;
//    }
//
//
//    public void setFromSize(SearchSourceBuilder searchSourceBuilder, HotelSearchRequest hotelSearchRequest) {
//        int pageSize = hotelSearchRequest.getPageSize();
//        int pageNo = hotelSearchRequest.getPageNo();
//        int esFrom = 0;//页码为1时的翻页起始位置
//        if (pageNo > 1) {
//            //页码不为1时，翻页的位置为当前页的起始位置
//            esFrom = (pageNo - 1) * pageSize;
//        }
//        searchSourceBuilder.from(esFrom);//设置翻页起始位置
//        searchSourceBuilder.size(pageSize);//设置翻页大小
//    }
//
//
//    public void setSort(SearchSourceBuilder searchSourceBuilder, HotelSearchRequest hotelSearchRequest) {
//        //判断是否有排序请求
//        if (hotelSearchRequest.getHotelSort() != null) {
//            String sortField = hotelSearchRequest.getHotelSort().getSortField();
//            String sortType = hotelSearchRequest.getHotelSort().getSortType();
//            if ("distance".equals(sortField)) {
//                //如果排序字段是距离，则按照距离由近及远排序
//                double lat = hotelSearchRequest.getLat();
//                double lon = hotelSearchRequest.getLon();
//                GeoDistanceSortBuilder geoDistanceSortBuilder = SortBuilders.geoDistanceSort("location", lat, lon)
//                        .point(lat, lon).unit(DistanceUnit.KILOMETERS).order(SortOrder.ASC);
//                searchSourceBuilder.sort(geoDistanceSortBuilder);
//            } else {
//                //按照指定字段进行降序排序
//                if ("asc".equals(sortType)) {
//                    searchSourceBuilder.sort(sortField, SortOrder.ASC);//设置按照字段升序
//                }
//                //按照指定字段进行升序排序
//                if ("desc".equals(sortType)) {
//                    searchSourceBuilder.sort(sortField, SortOrder.DESC);//设置按照字段降序
//                }
//            }
//        }
//    }
//
//
//    public List<AggregationBuilder> getAggs(HotelSearchRequest hotelSearchRequest) {
//        List<AggregationBuilder> aggs = new ArrayList<AggregationBuilder>();
//        //定义价格聚合为统计指标
//        StatsAggregationBuilder priceAgg = AggregationBuilders.stats(HotelSearchConstant.priceAggName).field("price");
//        aggs.add(priceAgg);//添加价格聚合
//        //定义星级聚合为文档计数
//        TermsAggregationBuilder starAgg = AggregationBuilders.terms(HotelSearchConstant.starAggName).field("star");
//        aggs.add(starAgg);//添加星级聚合
//
//        double lat = hotelSearchRequest.getLat();
//        double lon = hotelSearchRequest.getLon();
//        //定义GeoDistance聚合
//        GeoDistanceAggregationBuilder geoDistanceAgg = AggregationBuilders.geoDistance(HotelSearchConstant.distanceAggName, new GeoPoint(lat, lon));
//        geoDistanceAgg.unit(DistanceUnit.KILOMETERS);
//        geoDistanceAgg.field("location");
//        //指定距离分桶范围规则
//        geoDistanceAgg.addRange(new GeoDistanceAggregationBuilder.Range("0-3", 0d, 3d));
//        geoDistanceAgg.addRange(new GeoDistanceAggregationBuilder.Range("0-10", 0d, 10d));
//        geoDistanceAgg.addRange(new GeoDistanceAggregationBuilder.Range("0-50", 0d, 50d));
//        aggs.add(geoDistanceAgg);
//        return aggs;
//    }
//
//
//    public BoolQueryBuilder getFilterQuery(HotelSearchRequest hotelSearchRequest) {
//        //第一、二阶段搜索共用搜索条件
//        String keyword = hotelSearchRequest.getKeyword();
//        String city = hotelSearchRequest.getCity();
//        double lat = hotelSearchRequest.getLat();
//        double lon = hotelSearchRequest.getLon();
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        //设置城市
//        boolQueryBuilder.filter(QueryBuilders.termQuery("city", city));
//        //根据title进行match查询
//        boolQueryBuilder.must(QueryBuilders.matchQuery("title", keyword));
//        HotelFilter hotelFilter = hotelSearchRequest.getHotelFilter();
//        //判断第二阶段搜索条件是否为空
//        if (hotelFilter != null) {
//            if (null != hotelFilter.getDistance()) {
//                int distance = hotelFilter.getDistance();
//                boolQueryBuilder.must(QueryBuilders.geoDistanceQuery("location").
//                        distance(distance, DistanceUnit.KILOMETERS).point(lat, lon));
//            } else {
//                //新建geo_distance查询，设置基准点坐标和周边距离,默认50公里
//                boolQueryBuilder.must(QueryBuilders.geoDistanceQuery("location").
//                        distance(50, DistanceUnit.KILOMETERS).point(lat, lon));
//            }
//            if (null != hotelFilter.getLowPrice() && null != hotelFilter.getHighPrice()) {
//                double lowPrice = hotelFilter.getLowPrice();
//                double highPrice = hotelFilter.getHighPrice();
//                QueryBuilder priceRange = QueryBuilders.rangeQuery("price").gte(lowPrice).lte(highPrice);
//                boolQueryBuilder.must(priceRange);
//            }
//            if (null != hotelFilter.getStar()) {
//                String star = hotelFilter.getStar();
//                boolQueryBuilder.must(QueryBuilders.termQuery("star", star));
//            }
//        } else {
//            //新建geo_distance查询，设置基准点坐标和周边距离,默认50公里
//            boolQueryBuilder.must(QueryBuilders.geoDistanceQuery("location").
//                    distance(50, DistanceUnit.KILOMETERS).point(lat, lon));
//        }
//        return boolQueryBuilder;
//    }
//
//
//    public SearchSourceBuilder getSearchCountQuery(HotelSearchRequest hotelSearchRequest) {
//        BoolQueryBuilder boolQuery = getFilterQuery(hotelSearchRequest);
//        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//创建搜索builder
//        searchSourceBuilder.query(boolQuery);//构建query
//        return searchSourceBuilder;
//    }
//
//
//    public Script getScoreScript(HotelSearchRequest hotelSearchRequest) {
//        //定义打分脚本内容
//        String scoreScript = "double score=0;\n" +
//                "          double titleMatch=_score;\n" +
//                "          double distanceGauss=decayGeoGauss(params.origin, params.scale, params.offset, params.decay, doc['location'].value);\n" +
//                "          double favourableScore=doc['favourable_percent'].value*0.01;\n" +
//                "          double fullRoomScore=1;\n" +
//                "          if(doc['full_room'].value==true){\n" +
//                "            fullRoomScore=0.1;\n" +
//                "          }\n" +
//                "          score=params.titleWeight*titleMatch+params.favourableWeight*favourableScore+params.distanceWeight*distanceGauss+params.fullRoomWeight*fullRoomScore;\n" +
//                "           return score;";
//        //定义参数传递的Map
//        Map paraMap = new HashMap();
//        //设置距离高斯衰减的参数
//        paraMap.put("scale", "5km");
//        paraMap.put("offset", "0.5km");
//        paraMap.put("decay", 0.5);
//        paraMap.put("origin", hotelSearchRequest.getLat() + "," + hotelSearchRequest.getLon());
//
//        paraMap.put("titleWeight", 30);//设置标题相关度的权重
//        paraMap.put("favourableWeight", 20);//设置好评率的权重
//        paraMap.put("distanceWeight", 30);//设置距离的权重
//        paraMap.put("fullRoomWeight", 50);//设置满房状态的权重
//        //创建painless脚本对象
//        Script script = new Script(Script.DEFAULT_SCRIPT_TYPE, "painless", scoreScript, paraMap);
//        return script;
//    }
//}
