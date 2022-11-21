package com.jz;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * es 搜索
 */
@Slf4j
public class App102Test {
    /**
     * 查询所有文档
     *
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        Query query = Query.of(builder -> builder
                .matchAll(b -> b.boost(9.0F))
        );
        SearchRequest searchRequest = SearchRequest.of(builder -> builder
                .index("hotel")
                .query(query)
        );
        SearchResponse<Hotel> searchResponse = esClient.search(searchRequest, Hotel.class);
        log.info(searchResponse.toString());
    }

    /**
     * Explain
     *
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        Query query = Query.of(builder -> builder
                .term(TermQuery.of(b -> b.field("city").value("北京")))
        );
        ExplainRequest explainRequest = ExplainRequest.of(builder -> builder
                        .index("hotel")
//                .id("1")
                        .id("2")
                        .query(query)
        );
        ExplainResponse<Hotel> explainResponse = esClient.explain(explainRequest, Hotel.class);
        log.info(explainResponse.toString());
    }

    /**
     * 统计
     *
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        Query query = Query.of(builder -> builder
                .term(TermQuery.of(b -> b.field("city").value("北京")))
        );
        CountRequest countRequest = CountRequest.of(builder -> builder
                .index("hotel")
                .query(query)
        );
        CountResponse countResponse = esClient.count(countRequest);
        log.info(countResponse.toString());
    }

    /**
     * 指定返回字段&分页
     *
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        Query query = Query.of(builder -> builder
                .term(TermQuery.of(b -> b.field("city").value("北京")))
        );
//        FieldAndFormat city = FieldAndFormat.of(builder -> builder
//                .field("city")
//        );
//        FieldAndFormat title = FieldAndFormat.of(builder -> builder
//                .field("title")
//        );
        SourceConfig sourceConfig = SourceConfig.of(builder -> builder
                .filter(b -> b.includes("city", "title"))
        );
        SearchRequest searchRequest = SearchRequest.of(builder -> builder
                        .index("hotel")
                        .query(query)
                        .source(sourceConfig)
//                        .from(0)
//                        .size(2)
//                .fields(city, title)
        );
        SearchResponse<Hotel> searchResponse = esClient.search(searchRequest, Hotel.class);
        log.info(searchResponse.toString());
    }


    /**
     * 添加文档
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        List<Hotel> hotelList = this.getHotelList();
        for (int index = 0; index < hotelList.size(); index++) {
            final Hotel hotel = hotelList.get(index);
            //
            final int tmpIndex = index;
            IndexRequest<Hotel> request = IndexRequest.of(i -> i
                    .index("hotel")
                    .id(tmpIndex + "")
                    .document(hotel)
            );
            IndexResponse response = esClient.index(request);
        }
    }

    /**
     * 创建索引
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        Map<String, Property> properties = new HashMap<>();
        properties.put("title", Property.of(builder -> builder.text(TextProperty.of(b -> b))));
        properties.put("city", Property.of(builder -> builder.keyword(KeywordProperty.of(b -> b))));
        properties.put("price", Property.of(builder -> builder.double_(DoubleNumberProperty.of(b -> b))));
        properties.put("create_time", Property.of(builder -> builder.date(DateProperty.of(b -> b))));
        properties.put("amenities", Property.of(builder -> builder.text(TextProperty.of(b -> b))));
        properties.put("full_room", Property.of(builder -> builder.boolean_(BooleanProperty.of(b -> b))));
        properties.put("location", Property.of(builder -> builder.geoPoint(GeoPointProperty.of(b -> b))));
        properties.put("praise", Property.of(builder -> builder.integer(IntegerNumberProperty.of(b -> b))));
        TypeMapping typeMapping = TypeMapping.of(builder -> builder
                .properties(properties)
        );
        CreateIndexRequest createIndexRequest = CreateIndexRequest.of(builder -> builder
                .index("hotel")
                .mappings(typeMapping)
        );
        CreateIndexResponse createIndexResponse = esClient.indices().create(createIndexRequest);
        log.info(createIndexResponse.toString());
    }

    private List<Hotel> getHotelList() {
        List<Hotel> list = new LinkedList<>();
        Hotel hotel1 = Hotel.builder()
                .title("文雅酒店")
                .city("青岛")
                .price(556.00)
                .create_time("20200418120000")
                .amenities("浴池，普通停车场/充电停车场")
                .full_room(false)
                .praise(10)
                .location(new Hotel.Location(36.083078, 120.37566))
                .build();
        Hotel hotel2 = Hotel.builder()
                .title("金都嘉怡假日酒店")
                .city("北京")
                .price(337.00)
                .create_time("20210315200000")
                .amenities("充电停车场/可升降停车场")
                .full_room(false)
                .praise(60)
                .location(new Hotel.Location(39.915153, 116.4030))
                .build();
        Hotel hotel3 = Hotel.builder()
                .title("金都欣欣酒店")
                .city("天津")
                .price(200.00)
                .create_time("20210509160000")
                .amenities("提供假日party，免费早餐，可充电停车场")
                .full_room(true)
                .praise(30)
                .location(new Hotel.Location(39.186555, 117.162007))
                .build();
        Hotel hotel4 = Hotel.builder()
                .title("金都酒店")
                .city("北京")
                .price(500.00)
                .create_time("20210218080000")
                .amenities("浴池（假日需预订），室内游泳池，普通停车场")
                .full_room(true)
                .praise(20)
                .location(new Hotel.Location(39.915343, 116.4239))
                .build();
        Hotel hotel5 = Hotel.builder()
                .title("文雅精选酒店")
                .city("北京")
                .price(800.00)
                .create_time("20210101080000")
                .amenities("浴池（假日需预订），wifi，室内游泳池，普通停车场")
                .full_room(true)
                .praise(20)
                .location(new Hotel.Location(39.918229, 116.422011))
                .build();
        list.add(hotel1);
        list.add(hotel2);
        list.add(hotel3);
        list.add(hotel4);
        list.add(hotel5);
        return list;
    }
}
