package com.digiwin.code.coverage.backend.controller;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONObject;
import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-03-02 21:01
 **/

@Api(value = "/",tags = "api测试")
@RestController
@RequestMapping("/")
@Validated
public class APIController {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    @Autowired
    public APIController(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }
    @ApiOperation("获取用户信息")
    @GetMapping("/user/info")
    public ResponseResult  userInfo() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:user-info.json");
        String jsonStr = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        // 使用ObjectMapper将JSON字符串转换为Java对象
        Object jsonData = objectMapper.readValue(jsonStr, Object.class);
        // 返回ResponseResult，其中包含了作为对象的JSON数据
        return ResponseResult.ok(jsonData);
    }
    @ApiOperation("获取导航信息")
    @GetMapping("/user/nav")
    public ResponseResult  userNav() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:user-nav.json");
        String jsonStr = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        // 使用ObjectMapper将JSON字符串转换为Java对象
        Object jsonData = objectMapper.readValue(jsonStr, Object.class);
        // 返回ResponseResult，其中包含了作为对象的JSON数据
        return ResponseResult.ok(jsonData);
    }

    @ApiOperation("页面信息")
    @GetMapping("/list/article")
    public ResponseResult  article() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:article.json");
        String jsonStr = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        // 使用ObjectMapper将JSON字符串转换为Java对象
        Object jsonData = objectMapper.readValue(jsonStr, Object.class);
        // 返回ResponseResult，其中包含了作为对象的JSON数据
        return ResponseResult.ok(jsonData);
    }

    @ApiOperation("api测试")
    @RequestMapping(value = "coverage", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult downLoadCodeAndCompile(@ApiParam(required = true, name = "count", value = "count")
                                                 @NotEmpty
                                                 @RequestParam(value = "count")String appId){

        return ResponseResult.ok(JSONObject.toJSON(MapUtil.newHashMap().put("aa","11")));
    }

    @ApiOperation("获取html值")
    @GetMapping("/extract-data")
    @ResponseBody
    public ResponseResult extractHtmlData() {
        Map<String, Object> result = new HashMap<>();
        File file = new File("D:\\jacoco\\report_all\\bm-opsc-diff\\index.html");

        if (!file.exists()) {
            result.put("error", "File not found");
            return ResponseResult.ok(result);
        }

        try {
            String htmlString = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(htmlString);

            Elements tfoot = doc.select("tfoot");

            if (!tfoot.isEmpty()) {
                Elements tds = tfoot.select("td");
                if (tds.size() >= 3) {
                    Element thirdTd = tds.get(2);
                    result.put("tfootThirdTdContent", thirdTd.text());
                } else {
                    result.put("error", "Less than 3 <td> elements found in <tfoot>");
                }
            } else {
                result.put("error", "<tfoot> not found in the HTML content");
            }
        } catch (IOException e) {
            result.put("error", "Error reading the HTML file: " + e.getMessage());
            return ResponseResult.ok(result);
        }

        return ResponseResult.ok(result);
    }

}
