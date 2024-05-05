package com.digiwin.code.coverage.backend.controller;

import com.alibaba.fastjson.JSONObject;
import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.constant.CodeCoverageConstant;
import com.digiwin.code.coverage.backend.util.FileUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.MapUtils;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description 下载exec文件（华为测试区）
 * @CreateDate 2024-02-01 10:20
 **/
@Api(value = "/downLoadFile", tags = "下载exec文件（业务中台测试区）")
@RestController
@RequestMapping("/downLoadFile")
@Validated
public class DownLoadFileController {

    @Autowired
    private CustomizeConfig customizeConfig;

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    @Autowired
    public DownLoadFileController(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    @ApiOperation("下载exec文件")
    @RequestMapping(value = "file", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult downLoadCodeAndCompile(@ApiParam(required = true, name = "appId", value = "appId")
                                                 @NotEmpty
                                                 @RequestParam(value = "appId") String appId,
                                                 @RequestParam(value = "sprintCode") String sprintCode,
                                                 @RequestParam(value = "appPartition") String appPartition) {
        String appIdDownload = appId.toLowerCase().replace("-", "");
        String basePath = customizeConfig.getDownLoadPath() + "\\" + appId;
        if("test".equals(appPartition)){
            basePath = customizeConfig.getDownLoadTestPath() + "\\" + appId;
        }
        String filePath = "";
        try {
            FileUtils.restFileMkdirs(basePath);

            Resource resource = resourceLoader.getResource("classpath:download.json");
            String jsonStr = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            // 使用ObjectMapper将JSON字符串转换为Java对象
            if ("dev".equals(appPartition)) {
                Map jsonData = objectMapper.readValue(jsonStr, Map.class);
                FileUtils.downloadFile(MessageFormat.format(MapUtils.getString(jsonData, appId.toLowerCase()), new String[]{appIdDownload, sprintCode, appIdDownload}), basePath);
            } else if("test".equals(appPartition)) {
                FileUtils.downloadFile(MessageFormat.format(customizeConfig.getDownLoadUrl(), new String[]{appIdDownload, sprintCode, appIdDownload}), basePath);
            }
            FileUtils.unzip(basePath + CodeCoverageConstant.TEMPFILE_NAME, basePath);
            File fm = new File(basePath);
            if (fm.exists()) {
                File[] fs = fm.listFiles();
                for (File f : fs) {
                    if (f.getName().endsWith("exec")) {
                        filePath = f.getAbsolutePath();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseResult.ok(filePath);
    }
}
