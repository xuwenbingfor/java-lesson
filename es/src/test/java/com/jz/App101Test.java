package com.jz;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
public class App101Test {


    @Test
    public void test3() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
    }

    @Test
    public void test2() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        GetResponse<Product> response = esClient.get(g -> g
                        .index("products")
                        .id("bk-1"),
                Product.class
        );

        if (response.found()) {
            Product product = response.source();
            log.info("Product name " + product.getName());
        } else {
            log.info("Product not found");
        }
    }

    @Test
    public void test1() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        Product product = new Product("bk-1", "City bike", 123.0);

        IndexRequest<Product> request = IndexRequest.of(i -> i
                .index("products")
                .id(product.getSku())
                .document(product)
        );

        IndexResponse response = esClient.index(request);

        log.info("Indexed with version " + response.version());
    }

    public static void main(String[] args) throws IOException {
        ElasticsearchClient client = ElasticsearchUtil.createClient();
        client.indices().create(c -> c.index("products"));
    }
}
