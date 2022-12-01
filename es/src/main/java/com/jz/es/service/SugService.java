package com.jz.es.service;

import cn.hutool.core.collection.CollectionUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.FieldSuggester;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import com.jz.es.model.entity.HotelSug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by GaoYinHui on 2021/6/3.
 */
@Service
public class SugService {
    //定义三种搜索建议的名称
    public static final String chineseSugName = "hotel_chinese_sug";
    public static final String fullPinyinSugName = "hotel_full_pinyin_sug";
    public static final String headPinyinSugName = "hotel_head_pinyin_sug";
    @Autowired
    ElasticsearchClient client;
//    RestHighLevelClient client;

    public List<String> suggestSearch(String prefixWord) throws IOException {
        FieldSuggester chineseSug = FieldSuggester.of(builder -> builder
                .prefix(prefixWord)
                .completion(b -> b.field("chinese"))
        );
        FieldSuggester fullPinyinSug = FieldSuggester.of(builder -> builder
                .prefix(prefixWord)
                .completion(b -> b.field("full_pinyin"))
        );
        FieldSuggester headPinyinSug = FieldSuggester.of(builder -> builder
                .prefix(prefixWord)
                .completion(b -> b.field("head_pinyin"))
        );
        Suggester suggester = Suggester.of(builder -> builder
                .suggesters(chineseSugName, chineseSug)
                .suggesters(fullPinyinSugName, fullPinyinSug)
                .suggesters(headPinyinSugName, headPinyinSug)
        );
        SearchRequest searchRequest = SearchRequest.of(builder -> builder
                .index("hotel_suggest")
                .suggest(suggester)
        );
        SearchResponse<HotelSug> searchResponse = client.search(searchRequest, HotelSug.class);
        System.out.println(searchResponse.toString());
        // 组合返回结果
        Map<String, List<Suggestion<HotelSug>>> suggestMap = searchResponse.suggest();
        HashSet<String> sugSet = new HashSet<String>();
        List<String> sugList = new ArrayList<String>();

        List<Suggestion<HotelSug>> suggestion1 = suggestMap.get(chineseSugName);
        List<CompletionSuggestOption<HotelSug>> options1 = suggestion1.get(0).completion().options();
        if (CollectionUtil.isNotEmpty(options1)) {
            for (CompletionSuggestOption<HotelSug> o : options1) {
                HotelSug sug = o.source();
                String ch = sug.getChinese().getInput();
                if (!sugSet.contains(ch)) {
                    sugList.add(ch);
                    sugSet.add(ch);
                }
            }
        }

        List<Suggestion<HotelSug>> suggestion2 = suggestMap.get(fullPinyinSugName);
        List<CompletionSuggestOption<HotelSug>> options2 = suggestion2.get(0).completion().options();
        if (CollectionUtil.isNotEmpty(options2)) {
            for (CompletionSuggestOption<HotelSug> o : options2) {
                HotelSug sug = o.source();
                String ch = sug.getChinese().getInput();
                if (!sugSet.contains(ch)) {
                    sugList.add(ch);
                    sugSet.add(ch);
                }
            }
        }

        List<Suggestion<HotelSug>> suggestion3 = suggestMap.get(headPinyinSugName);
        List<CompletionSuggestOption<HotelSug>> options3 = suggestion3.get(0).completion().options();
        if (CollectionUtil.isNotEmpty(options3)) {
            for (CompletionSuggestOption<HotelSug> o : options3) {
                HotelSug sug = o.source();
                String ch = sug.getChinese().getInput();
                if (!sugSet.contains(ch)) {
                    sugList.add(ch);
                    sugSet.add(ch);
                }
            }
        }
        return sugList;
    }


/*    public List<String> suggestSearch(String prefixWord) throws IOException {
        SearchRequest searchRequest = new SearchRequest("hotel_suggest");//创建搜索请求,指定索引名称为hotel_sug
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        //创建completion类型搜索建议
        CompletionSuggestionBuilder chineseSug = SuggestBuilders.completionSuggestion("chinese").prefix(prefixWord);
        suggestBuilder.addSuggestion(chineseSugName, chineseSug);//添加搜索建议

        //创建completion类型搜索建议
        CompletionSuggestionBuilder fullPinyinSug = SuggestBuilders.completionSuggestion("full_pinyin").prefix(prefixWord);
        //添加拼音全拼搜索建议
        suggestBuilder.addSuggestion(fullPinyinSugName, fullPinyinSug);

        //创建completion类型搜索建议
        CompletionSuggestionBuilder headPinyinSug = SuggestBuilders.completionSuggestion("head_pinyin").prefix(prefixWord);
        //添加拼音首字母搜索建议
        suggestBuilder.addSuggestion(headPinyinSugName, headPinyinSug);

        searchSourceBuilder.suggest(suggestBuilder);//设置suggest请求
        searchRequest.source(searchSourceBuilder);//设置查询请求
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);//进行搜索,获取搜索结果
        Suggest suggest = response.getSuggest();
        HashSet sugSet = new HashSet<String>();
        CompletionSuggestion chineseSuggestion = suggest.getSuggestion(chineseSugName);//获取suggest结果
        List<String> sugList = new ArrayList<String>();
        //遍历中文suggest结果
        for (CompletionSuggestion.Entry.Option option : chineseSuggestion.getOptions()) {
            Map<String, Object> sourceMap = option.getHit().getSourceAsMap();//获取文档
            Map<String, Object> chineseText = (Map<String, Object>) sourceMap.get("chinese");//获取文档中的中文
            String sugChinese = (String) chineseText.get("input");
            if (!sugSet.contains(sugChinese)) {
                sugList.add(sugChinese);
                sugSet.add(sugChinese);
            }
        }

        CompletionSuggestion fullPinyinSuggestion = suggest.getSuggestion(fullPinyinSugName);//获取suggest结果
        //遍历拼音全拼suggest结果
        for (CompletionSuggestion.Entry.Option option : fullPinyinSuggestion.getOptions()) {
            Map<String, Object> sourceMap = option.getHit().getSourceAsMap();//获取文档
            Map<String, Object> chineseText = (Map<String, Object>) sourceMap.get("chinese");//获取文档中的中文
            String sugChinese = (String) chineseText.get("input");
            if (!sugSet.contains(sugChinese)) {
                sugList.add(sugChinese);
                sugSet.add(sugChinese);
            }
        }

        CompletionSuggestion headPinyinSuggestion = suggest.getSuggestion(headPinyinSugName);//获取suggest结果
        //遍历拼音首字母suggest结果
        for (CompletionSuggestion.Entry.Option option : headPinyinSuggestion.getOptions()) {
            Map<String, Object> sourceMap = option.getHit().getSourceAsMap();//获取文档
            Map<String, Object> chineseText = (Map<String, Object>) sourceMap.get("chinese");//获取文档中的中文
            String sugChinese = (String) chineseText.get("input");
            if (!sugSet.contains(sugChinese)) {
                sugList.add(sugChinese);
                sugSet.add(sugChinese);
            }
        }
        return sugList;
    }*/
}
