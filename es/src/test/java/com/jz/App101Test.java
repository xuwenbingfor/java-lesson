package com.jz;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.InlineScript;
import co.elastic.clients.elasticsearch._types.Script;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggester;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.indices.GetMappingRequest;
import co.elastic.clients.elasticsearch.indices.GetMappingResponse;
import co.elastic.clients.elasticsearch.indices.PutMappingRequest;
import co.elastic.clients.elasticsearch.indices.PutMappingResponse;
import co.elastic.clients.elasticsearch.indices.get_mapping.IndexMappingRecord;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * es 基本操作
 */
@Slf4j
public class App101Test {
    /**
     * 搜索建议
     *
     * @throws IOException
     */
    @Test
    public void test11() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        CompletionSuggester completionSuggester = CompletionSuggester.of(
                builder -> builder.field("query_word")
        );
        FieldSuggester fieldSuggester = FieldSuggester.of(builder -> builder
//                .prefix("如家")
                        .prefix("格林")
                        .completion(completionSuggester)
        );
        Suggester suggester = Suggester.of(builder -> builder
                .suggesters("product_sug", fieldSuggester)
        );
        SearchRequest searchRequest = SearchRequest.of(builder -> builder
                .index("products")
                .suggest(suggester)
        );
        SearchResponse<Product> searchResponse = esClient.search(searchRequest, Product.class);
        log.info(searchResponse.toString());
    }

    /**
     * 按条件删除
     *
     * @throws IOException
     */
    @Test
    public void test10() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        // query
        TermQuery termQuery = TermQuery.of(builder -> builder
                .field("price")
                .value("9.9")
        );
        Query query = Query.of(builder -> builder
                .term(termQuery)
        );
        DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest.of(builder -> builder
                .index("products")
                .query(query)
        );
        DeleteByQueryResponse deleteByQueryResponse = esClient.deleteByQuery(deleteByQueryRequest);
        log.info(deleteByQueryResponse.toString());
    }

    /**
     * 批量删除
     *
     * @throws IOException
     */
    @Test
    public void test9() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        List<BulkOperation> bulkOperationList = new LinkedList<>();
        List<Product> products = fetchProducts();
        for (Product product : products) {
            DeleteOperation deleteOperation = DeleteOperation.of(builder -> builder
                    .id(product.getSku())
            );
            BulkOperation bulkOperation = BulkOperation.of(builder -> builder
                    .delete(deleteOperation)
            );
            bulkOperationList.add(bulkOperation);
        }
        BulkRequest bulkRequest = BulkRequest.of(builder -> builder
                .index("products")
                .operations(bulkOperationList)
        );
        esClient.bulk(bulkRequest);
    }

    /**
     * 单文档删除
     *
     * @throws IOException
     */
    @Test
    public void test8() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        DeleteRequest deleteRequest = DeleteRequest.of(builder -> builder
                .index("products")
                .id("bk-3")
        );
        DeleteResponse deleteResponse = esClient.delete(deleteRequest);
        log.info(deleteResponse.toString());
    }

    /**
     * 根据条件更新文档
     *
     * @throws IOException
     */
    @Test
    public void test7() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        // query
        TermQuery termQuery = TermQuery.of(builder -> builder
                .field("price")
                .value("9.9")
        );
        Query query = Query.of(builder -> builder
                .term(termQuery)
        );
        // script
        InlineScript inlineScript = InlineScript.of(builder -> builder
                .source("ctx._source.name=\"99vip\"")
        );
        Script script = Script.of(builder -> builder
                .inline(inlineScript)
        );
        UpdateByQueryRequest updateByQueryRequest = UpdateByQueryRequest.of(builder -> builder
                .index("products")
                .query(query)
                .script(script)
        );
        UpdateByQueryResponse updateByQueryResponse = esClient.updateByQuery(updateByQueryRequest);
        log.info(updateByQueryResponse.toString());
    }

    /**
     * 更新文档
     *
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
/*//        Product product = new Product("car-1", "bmw", 1.0);
        Product product = new Product("car-1", "bmw", 10.0);

        IndexRequest<Product> request = IndexRequest.of(i -> i
                .index("products")
                .id(product.getSku())
                .document(product)
        );

        IndexResponse response = esClient.index(request);

        log.info("Indexed with version " + response.version());*/

//        Product product = new Product("car-1", "bmw", 9.9);
        Product product = new Product("bk-3", "bmw", 9.9);
        product.setQuery_word("格林豪泰上海");
        UpdateRequest<Product, Product> updateRequest = UpdateRequest.of(builder -> builder
                .index("products")
                .id(product.getSku())
                .doc(product)
        );

        UpdateResponse<Product> updateResponse = esClient.update(updateRequest, Product.class);
        log.info(updateResponse.toString());
    }

    /**
     * 批量操作
     *
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        List<BulkOperation> bulkOperationList = new LinkedList<>();
        List<Product> products = fetchProducts();
        for (Product product : products) {
/*            // 1、批量更新
            product.setPrice(10000.0);
            UpdateAction<Object, Object> updateAction = UpdateAction.of(builder -> builder
                    .doc(product)
            );
            UpdateOperation<Object, Object> updateOperation = UpdateOperation.of(builder -> builder
                    .id(product.getSku())
                    .index("products")
                    .action(updateAction)
            );
            BulkOperation bulkOperation = BulkOperation.of(builder -> builder
                    .update(updateOperation)
            );*/
            // 2、批量index
            IndexOperation<Product> indexOperation = IndexOperation.of(builder -> builder
                            .id(product.getSku())
//                    .index("test")
                            .index("products")
                            .document(product)
            );
            BulkOperation bulkOperation = BulkOperation.of(builder -> builder
                    .index(indexOperation)
            );
            bulkOperationList.add(bulkOperation);
        }
        //
        BulkRequest bulkRequest = BulkRequest.of(builder -> builder
//                POST /products/_bulk HTTP/1.1
//                POST /_bulk HTTP/1.1
//                .index("products")
                        .operations(bulkOperationList)
        );
        esClient.bulk(bulkRequest);
    }

    private List<Product> fetchProducts() {
        List<Product> list = new ArrayList<>();
        list.add(new Product("bk-10", "Qingjv Bike", 9.9));
        list.add(new Product("bk-11", "Hello Bike", 9.9));
        return list;
    }

    /**
     * 扩展映射
     *
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        // Property为了统一TextProperty等不同类型，为了实现 “PutMappingRequest#properties(Map<String, Property> map)”
/*        TextProperty textProperty = TextProperty.of(builder -> builder);
        Property property = Property.of(builder -> builder.text(textProperty));
        PutMappingRequest putMappingRequest = PutMappingRequest.of(builder -> builder
                .index("products")
                .properties("tag", property)
        );*/
        Property property = Property.of(
                builder -> builder.completion(
                        b -> b
                )
        );
        PutMappingRequest putMappingRequest = PutMappingRequest.of(builder -> builder
                .index("products")
                .properties("query_word", property)
        );
        //
        PutMappingResponse putMappingResponse = esClient.indices().putMapping(putMappingRequest);
        log.info(putMappingResponse.toString());
    }

    /**
     * 查询映射
     *
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        GetMappingRequest getMappingRequest = GetMappingRequest.of(builder -> builder.index("products"));

        GetMappingResponse getMappingResponse = esClient.indices().getMapping(getMappingRequest);
        IndexMappingRecord products = getMappingResponse.get("products");
        log.info(products.toString());
    }

    /**
     * 根据id查询文档
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        ElasticsearchClient esClient = ElasticsearchUtil.createClient();
        //
        GetResponse<Product> response = esClient.get(g -> g
                        .index("products")
                        .id("bk-1"),
                Product.class
        );

        log.info(response.toString());
        if (response.found()) {
            Product product = response.source();
            log.info("Product name " + product.getName());

        } else {
            log.info("Product not found");
        }
    }

    /**
     * 添加文档
     *
     * @throws IOException
     */
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

    /**
     * 创建索引
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        ElasticsearchClient client = ElasticsearchUtil.createClient();
        client.indices().create(c -> c.index("products"));
    }
}
