package com.jz;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class App104Test {
    /**
     * 桶聚合
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
/*        // 三级桶
        SumAggregation sumAggregation = SumAggregation.of(builder -> builder
                .field("price")
                .missing(0.0)
        );
        Aggregation subAggregation = Aggregation.of(builder -> builder
                .sum(sumAggregation)
        );*/
        Query query = Query.of(builder -> builder
                .range(b -> b
                        .field("price")
                        .lt(JsonData.of(700.0))
                )
        );
        Aggregation subAggregation = Aggregation.of(builder -> builder
                .filter(query)
        );
        // 二级桶
        TermsAggregation termsAggregation2 = TermsAggregation.of(builder -> builder
                .field("full_room")
        );
        Aggregation aggregation2 = Aggregation.of(builder -> builder
                .terms(termsAggregation2)
                // 组内聚合
                .aggregations("myagg2", subAggregation)
        );
        // 一级桶
        TermsAggregation termsAggregation = TermsAggregation.of(builder -> builder
                .field("city")
        );
        Aggregation aggregation = Aggregation.of(builder -> builder
                .terms(termsAggregation)
                // 组内聚合
                .aggregations("mysum", aggregation2)
        );
        SearchRequest searchRequest = SearchRequest.of(builder -> builder
                .index("hotel")
                .aggregations("myagg", aggregation)
        );
        SearchResponse<Object> searchResponse = esClient.search(searchRequest, Object.class);
        log.info(searchResponse.toString());
    }
}
