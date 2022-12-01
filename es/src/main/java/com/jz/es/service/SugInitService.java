package com.jz.es.service;

import cn.hutool.core.io.IoUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class SugInitService {
    @Autowired
    ElasticsearchClient client;

    //读取文件内容
    public List<String> getListFromFile(String file) {
        List<String> wordList = new ArrayList<String>();
        InputStream inputStream = null;
        try {
            ClassPathResource resource = new ClassPathResource(file);
            //获取文件流
            inputStream = resource.getInputStream();
            IoUtil.readUtf8Lines(inputStream, wordList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
        }
        return wordList;
    }

    /**
     * <pre>
     *     将字符串处理为doc
     *     参考：https://www.elastic.co/guide/en/elasticsearch/reference/8.5/search-suggesters.html#completion-suggester
     * </pre>
     *
     * @param id
     * @param text
     * @param weight
     * @return
     */
    public static Map<String, Object> processPy(int id, String text, int weight) {
        Map dataMap = new HashMap();
        //使用hanlp对中文进行处理
        List<Pinyin> pinyinList = HanLP.convertToPinyinList(text);
        //定义提示词拼音全拼StringBuffer
        StringBuffer fullPinyinBufer = new StringBuffer();
        //定义提示词拼音首字母StringBuffer
        StringBuffer headPinyinBufer = new StringBuffer();
        for (Pinyin pinyin : pinyinList) {
            fullPinyinBufer.append(pinyin.getPinyinWithoutTone());
            headPinyinBufer.append(pinyin.getHead());
        }
        dataMap.put("id", id);

        //提示词中文处理
        Map chineseMap = new HashMap();
        chineseMap.put("input", text);
        chineseMap.put("weight", weight);
        dataMap.put("chinese", chineseMap);

        //提示词全拼处理
        Map fullPinyinMap = new HashMap();
        fullPinyinMap.put("input", fullPinyinBufer.toString());
        fullPinyinMap.put("weight", weight);
        dataMap.put("full_pinyin", fullPinyinMap);

        //提示词拼音首字母处理
        Map headPinyinMap = new HashMap();
        headPinyinMap.put("input", headPinyinBufer.toString());
        headPinyinMap.put("weight", weight);
        dataMap.put("head_pinyin", headPinyinMap);

        return dataMap;
    }

    //批量写入索引
    public void bulkIndexDoc(String indexName, String docIdKey, List<Map<String, Object>> recordMapList) {
/*        BulkRequest bulkRequest = new BulkRequest(indexName);//构建批量操作BulkRequest对象
        for (Map<String, Object> dataMap : recordMapList) {//遍历数据
            String docId = dataMap.get(docIdKey).toString();//获取主键作为Elasticsearch索引的主键
            IndexRequest indexRequest = new IndexRequest().id(docId).source(dataMap);//构建IndexRequest对象
            bulkRequest.add(indexRequest);//添加IndexRequest
        }
        bulkRequest.timeout(TimeValue.timeValueSeconds(5));//设置超时时间
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);//执行批量写入
            if (bulkResponse.hasFailures()) {//判断执行状态
                System.out.println("bulk fail,message:" + bulkResponse.buildFailureMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        List<BulkOperation> operationList = new LinkedList<>();
        for (Map<String, Object> dataMap : recordMapList) {//遍历数据
            String docId = dataMap.get(docIdKey).toString();//获取主键作为Elasticsearch索引的主键
            BulkOperation operation = BulkOperation.of(builder -> builder
                    .create(b -> b
                            .id(docId)
                            .document(dataMap)
                    )
            );
            operationList.add(operation);
        }
        BulkRequest bulkRequest = BulkRequest.of(builder -> builder
                .index(indexName)
                .operations(operationList)
        );
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest);
            //判断执行状态
            if (bulkResponse.errors()) {
                System.out.println("bulk fail,message:" + bulkResponse.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startProcess() {
        List<String> wordList = getListFromFile("suggest_words.csv");//从文件中获取提示词列表
        List<Map<String, Object>> recordMapList = new ArrayList<Map<String, Object>>();//存储所有提示词和提示词对应的拼音形式
        int i = 0;//id字段对应的值
        //遍历提示词列表
        for (String word : wordList) {
            i += 1;
            String[] fields = word.split(",");
            String keyWord = fields[0];
            int weight = Integer.parseInt(fields[1]);//获取提示词权重值
            Map<String, Object> dataMap = processPy(i, keyWord, weight);//得到单条提示词和提示词对应的拼音形式
            recordMapList.add(dataMap);
        }
        bulkIndexDoc("hotel_suggest", "id", recordMapList);
    }

    /**
     * 新建索引
     */
    public void startIndex() {
        Property id = Property.of(
                builder -> builder.keyword(b -> b)
        );
        Property chinese = Property.of(
                builder -> builder.completion(
                        b -> b
                )
        );
        Property full_pinyin = Property.of(
                builder -> builder.completion(
                        b -> b
                )
        );
        Property head_pinyin = Property.of(
                builder -> builder.completion(
                        b -> b
                )
        );
        Map<String, Property> map = new HashMap<>();
        map.put("id", id);
        map.put("chinese", chinese);
        map.put("full_pinyin", full_pinyin);
        map.put("head_pinyin", head_pinyin);
        try {
            client.indices().create(c -> c
                    .index("hotel_suggest")
                    .mappings(builder -> builder
                            .properties(map)
                    )
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
