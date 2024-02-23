package com.digiwin.code.coverage.backend.controller;

import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.constant.CodeCoverageConstant;
import com.digiwin.code.coverage.backend.dto.CodeInvokeParam;
import com.digiwin.code.coverage.backend.util.FileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description 下载exec文件（华为测试区）
 * @CreateDate 2024-02-01 10:20
 **/
@Api(value = "/downLoadFile",tags = "下载exec文件（华为测试区）")
@RestController
@RequestMapping("/downLoadFile")
@Validated
public class DownLoadFileController {

    @Autowired
    private  CustomizeConfig customizeConfig;

    @ApiOperation("下载exec文件")
    @RequestMapping(value = "file", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult downLoadCodeAndCompile(@ApiParam(required = true, name = "appId", value = "appId")
                                                     @NotEmpty
                                                     @RequestParam(value = "appId")String appId){

        try {
            String appIdDownload = appId.toLowerCase().replace("-","");
            FileUtils.downloadFile(MessageFormat.format(customizeConfig.getDownLoadUrl(),new String[]{appIdDownload,appIdDownload}),customizeConfig.getDownLoadPath());
            FileUtils.unzip(customizeConfig.getDownLoadPath()+CodeCoverageConstant.TEMPFILE_NAME,customizeConfig.getDownLoadPath());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseResult.ok();
    }



}
