package com.jz.es.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jz.es.model.form.SugReq;
import com.jz.es.service.SugInitService;
import com.jz.es.service.SugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 加权的搜索建议
 */
@RestController
@CrossOrigin //解决跨域问题
public class HotelSearchSugController {
    @Autowired
    SugInitService pyProcessService;//处理拼音
    @Autowired
    SugService sugService;//调用搜索建议逻辑

    //处理搜索建议请求
    @PostMapping(value = "/search-sug", produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getSug(@RequestBody SugReq sugReq) throws Exception {
        List<String> sugList = sugService.suggestSearch(sugReq.getPrefixWord());//获取搜索建议内容
        JSONObject result = new JSONObject();
        //封装结果
        result.put("requestId", sugReq.getRequestId());
        result.put("sugList", sugList);
        return JSONUtil.toJsonStr(result);//转换为Json字符串
    }

    //处理索引初始化请求
    @GetMapping("/init-sug-index")
    public String initIndex() throws Exception {
        pyProcessService.startProcess();
        return "process sucesss!";
    }

    /**
     * 新建索引
     *
     * @return
     * @throws Exception
     */
    @GetMapping("/create-sug-index")
    public String createIndex() throws Exception {
        pyProcessService.startIndex();
        return "create index success!";
    }
}
