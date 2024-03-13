package com.digiwin.code.coverage.backend.controller;

import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.pojo.dto.ReportJacocoParam;
import com.digiwin.code.coverage.backend.service.ReportService;
import com.digiwin.code.coverage.backend.util.OrikaMapperUtil;
import com.digiwin.code.coverage.backend.pojo.vo.ReportJacocoParamVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-01-30 15:41
 **/
@Api(value = "/code",tags = "报告管理")
@RestController
@RequestMapping("/code")
@Validated
public class ReportController {

    @Autowired
    private ReportService reportService;

    @ApiOperation("report生成html文件")
    @RequestMapping(value = "report", method = RequestMethod.POST)
    public ResponseResult report(
            @ApiParam(value = "生成报告参数", required = true)
            @RequestBody ReportJacocoParamVO reportJacocoParamVO) throws Exception {
        ReportJacocoParam reportJacocoParam = OrikaMapperUtil.map(reportJacocoParamVO, ReportJacocoParam.class);
        reportJacocoParam.setAppId(reportJacocoParam.getAppId().toLowerCase());
        reportService.reportJacoco(reportJacocoParam);
        return  ResponseResult.ok(reportJacocoParam.getReportDirectory());
    }
}
