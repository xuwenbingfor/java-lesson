//package com.jz.es.service;
//
//import org.apache.commons.io.IOUtils;
//import org.elasticsearch.action.bulk.BulkRequest;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.unit.TimeValue;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class HotelInitService {
//    @Autowired
//    RestHighLevelClient client;
//    //读取文件内容
//    public List<String> getListFromFile(String file) {
//        List<String> wordList=new ArrayList<String>();
//        try {
//            byte[] bytes;
//            ClassPathResource resource = new ClassPathResource(file);
//            //获取文件流
//            InputStream inputStream = resource.getInputStream();
//            bytes = IOUtils.toByteArray(inputStream);
//            inputStream.read(bytes);
//            inputStream.close();
//
//            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
//            InputStreamReader input = new InputStreamReader(byteArrayInputStream);
//            BufferedReader bf = new BufferedReader(input);
//            String line = null;
//            while((line=bf.readLine()) != null){
//                wordList.add(line.trim());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return wordList;
//    }
//
//    public  Map<String,Object> processHotelData(String hotelLine){
//        Map dataMap=new HashMap();
//        String[]fields=hotelLine.split("#");
//        if(fields.length<12){
//            return dataMap;
//        }
//        dataMap.put("id",fields[0]);
//        dataMap.put("title",fields[1]);
//        dataMap.put("business_district",fields[2]);
//        dataMap.put("address",fields[3]);
//        Map<String,Double> location=new HashMap<String,Double>();
//        String[] lonLat=fields[4].split(",");
//        location.put("lon",Double.parseDouble(lonLat[0]));
//        location.put("lat",Double.parseDouble(lonLat[1]));
//        dataMap.put("location",location);
//        dataMap.put("city",fields[5]);
//        dataMap.put("price",Double.parseDouble(fields[6]));
//        dataMap.put("star",fields[7]);
//        dataMap.put("full_room",Boolean.parseBoolean(fields[8]));
//        dataMap.put("impression",fields[9]);
//        dataMap.put("favourable_percent",Integer.parseInt(fields[10]));
//        dataMap.put("pic",fields[11]);
//        return dataMap;
//    }
//
//    public  void startInit(){
//        List<String> hotelList= getListFromFile("hotel.txt");//从文件中获取酒店信息列表
//        List<Map<String,Object>> recordMapList =new ArrayList<Map<String, Object>>();
//        //遍历酒店列表
//        for(String hotelLine:hotelList){
//            //封装酒店信息
//            Map<String,Object> dataMap= processHotelData(hotelLine);
//            if(dataMap.size()==0){
//                continue;
//            }
//            //将酒店信息添加到列表中
//            recordMapList.add(dataMap);
//        }
//        //将酒店信息批量添加到索引中
//        bulkIndexDoc("hotel", "id", recordMapList);
//    }
//
//    public void bulkIndexDoc(String indexName, String docIdKey, List<Map<String, Object>> recordMapList) {
//        BulkRequest bulkRequest = new BulkRequest(indexName);//构建批量操作BulkRequest对象
//        for (Map<String, Object> dataMap : recordMapList) {//遍历数据
//            String docId = dataMap.get(docIdKey).toString();//获取主键作为Elasticsearch索引的主键
//            IndexRequest indexRequest = new IndexRequest().id(docId).source(dataMap);//构建IndexRequest对象
//            bulkRequest.add(indexRequest);//添加IndexRequest
//        }
//        bulkRequest.timeout(TimeValue.timeValueSeconds(5));//设置超时时间
//        try {
//            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);//执行批量写入
//            if (bulkResponse.hasFailures()) {//判断执行状态
//                System.out.println("bulk fail,message:" + bulkResponse.buildFailureMessage());
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
