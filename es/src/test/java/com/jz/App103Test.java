package com.jz;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.AnalyzeRequest;
import co.elastic.clients.elasticsearch.indices.AnalyzeResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class App103Test {
    /**
     * 查询时使用同义词
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
    }

    /**
     * 分析器测试
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        // 1
        AnalyzeRequest analyzeRequest = AnalyzeRequest.of(builder -> builder
                        .analyzer("standard")
//                cannot define extra components on a named analyzer
//                .tokenizer(Tokenizer.of(b -> b.name("standard")))
                        .text("金都嘉怡假日酒店")
        );
        // 2
//        AnalyzeRequest analyzeRequest = AnalyzeRequest.of(builder -> builder
//                        .tokenizer(Tokenizer.of(b -> b.name("standard")))
//                        .filter(TokenFilter.of(b -> b.name("lowercase")))
//                        .text("The letter tokenizer is not configurable.")
//        );
        // ik分析器
        // "D:\Program_Files\elasticsearch-7.17.0\plugins\analysis-ik\config\my.dic"
        // 嘉怡
/*        {
            "tokens": [
            {
                "end_offset": 2,
                    "position": 0,
                    "start_offset": 0,
                    "token": "金都",
                    "type": "CN_WORD"
            },
            {
                "end_offset": 3,
                    "position": 1,
                    "start_offset": 2,
                    "token": "嘉",
                    "type": "CN_CHAR"
            },
            {
                "end_offset": 4,
                    "position": 2,
                    "start_offset": 3,
                    "token": "怡",
                    "type": "CN_CHAR"
            },
            {
                "end_offset": 8,
                    "position": 3,
                    "start_offset": 4,
                    "token": "假日酒店",
                    "type": "CN_WORD"
            }
	]
        }*/
        // 3
//        AnalyzeRequest analyzeRequest = AnalyzeRequest.of(builder -> builder
//                .analyzer("ik_smart")
//                .text("金都嘉怡假日酒店")
//        );
        AnalyzeResponse analyzeResponse = esClient.indices().analyze(analyzeRequest);
        log.info(analyzeResponse.toString());
    }
}
