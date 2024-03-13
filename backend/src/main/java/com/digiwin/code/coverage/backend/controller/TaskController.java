package com.digiwin.code.coverage.backend.controller;

import com.digiwin.code.coverage.backend.common.log.LoggerUtil;
import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.mapper.AppBranchMapper;
import com.digiwin.code.coverage.backend.mapper.SprintAppRelMapper;
import com.digiwin.code.coverage.backend.pojo.po.AppBranchPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintAppRelPO;
import com.digiwin.code.coverage.backend.pojo.vo.ReportJacocoParamVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Executor;

/**
 * 执行任务
 *
 * @Author: 夏锐
 * @CreateTime: 2024-03-12  10:57
 */
@Api(value = "/task",tags = "task")
@RestController
@RequestMapping("/task")
@Validated
@Slf4j
public class TaskController {

    @Resource(name = "asyncExecutor")
    private Executor executor;

    @Autowired
    private AppBranchMapper appBranchMapper;

    @Autowired
    private SprintAppRelMapper sprintAppRelMapper;

    @Autowired
    private DownLoadFileController downLoadFileController;

    @Autowired
    private CodeController codeController;

    @Autowired
    private ReportController reportController;

    @Autowired
    private APIController apiController;

    @Autowired
    private CustomizeConfig customizeConfig;

    @ApiOperation("执行任务")
    @RequestMapping(value = "alltask", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult excuteAllTask(@RequestParam(value = "sprintId", required = true) Long sprintId){
        List<Map<String, Object>> ls = sprintAppRelMapper.getListBySprintId2(sprintId);
        for(Map<String, Object> map : ls){
            Long id = (Long)map.get("id");
            Long relId = (Long)map.get("rel_id");
            String appcode = (String)map.get("app_code");
            excuteTask(id, relId, sprintId, appcode);
        }
        return ResponseResult.ok();
    }

    @ApiOperation("执行任务")
    @RequestMapping(value = "task", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult excuteTask(@RequestParam(value = "id", required = false) Long id,
                                     @RequestParam(value = "relid", required = true) Long relid,
                                     @RequestParam(value = "sprintId", required = true) Long sprintId,
                                     @RequestParam(value = "appcode", required = true) String appcode){
        AppBranchPO poT = null;
        Date downDataFileDate = null;
        if(id == null){
            poT = new AppBranchPO();
            poT.setRelId(relid);
            poT.setSprintId(sprintId);
            poT.setCompareType("all");
            poT.setAppCode(appcode);
            poT.setStatus("2");
            appBranchMapper.insert(poT);
            id = poT.getId();
        }else {
            AppBranchPO po = appBranchMapper.selectById(id);
            downDataFileDate = po.getDownloadDataFileDate();
            appBranchMapper.resetInfo(id);
        }
        final Long idT = id;
        final Date downDataFileDateT = downDataFileDate;
        executor.execute(() -> {
            // 此处需要获取下载文件
            AppBranchPO po = appBranchMapper.selectById(idT);

            SprintAppRelPO relPo = sprintAppRelMapper.selectById(relid);
            try {
                boolean isWithinTwoHours = false;
                if(downDataFileDateT != null){
                    LocalDateTime curTime = LocalDateTime.now();
                    Instant targetTime = downDataFileDateT.toInstant();
                    LocalDateTime targetLocalTime = LocalDateTime.ofInstant(targetTime, ZoneId.systemDefault());
                    isWithinTwoHours = curTime.isBefore(targetLocalTime.plusHours(2));
                }

                String filePath = po.getDataFilePath();
                if(!isWithinTwoHours){
                    appBranchMapper.resetDataFileDate(idT);
                    LoggerUtil.info(log, String.format("应用%s，下载exec文件，开始", appcode));
                    // 下载文件
                    ResponseResult downRes = downLoadFileController.downLoadCodeAndCompile(appcode, relPo.getSprintCode());

                    filePath = Objects.toString(downRes.getMessage(), "");
                    po.setDataFilePath(filePath);
                    po.setDownloadDataFileDate(new Date());
                    appBranchMapper.updateById(po);

                    LoggerUtil.info(log, String.format("应用%s，下载exec文件，保存路径为%s，结束", appcode, filePath));
                }else {
                    LoggerUtil.info(log, String.format("应用%s，下载exec文件在两小时内，无需重新下载", appcode));
                }

                ReportJacocoParamVO reportJacocoParamVO = new ReportJacocoParamVO();
                downLoadCodeAndCompile(reportJacocoParamVO, po, appcode);
                appBranchMapper.updateById(po);

                LoggerUtil.info(log, String.format("应用%s，生成全量报告，开始", appcode));
                String allReportPath = createReport(appcode, filePath, reportJacocoParamVO, "all");
                Map<String, Object> reportMap = apiController.getResponseCount(allReportPath);
                if(reportMap.containsKey("tfootThirdTdContent")){
                    po.setAllCount(Objects.toString(reportMap.get("tfootThirdTdContent")));
                }
                String reportBasePath = customizeConfig.getReportDir();
                allReportPath = allReportPath.replace(reportBasePath, "").replace("\\", "/");
                LoggerUtil.info(log, String.format("应用%s，生成全量报告，保存路径为%s，结束", appcode, allReportPath));
                po.setAllFilePath(allReportPath);
                po.setAllFileDate(new Date());
                if(StringUtils.equals(po.getCompareType(), "all")){
                    po.setStatus("3");
                }
                appBranchMapper.updateById(po);
                if(StringUtils.equals(po.getCompareType(), "branch")){
                    LoggerUtil.info(log, String.format("应用%s，生成增量报告，开始", appcode));
                    String branchReportPath = createReport(appcode, filePath, reportJacocoParamVO, "branch");
                    LoggerUtil.info(log, String.format("应用%s，生成增量报告，保存路径为%s，结束", appcode, branchReportPath));
                    reportMap = apiController.getResponseCount(branchReportPath);
                    if(reportMap.containsKey("tfootThirdTdContent")){
                        po.setDiffCount(Objects.toString(reportMap.get("tfootThirdTdContent")));
                    }
                    branchReportPath = branchReportPath.replace(reportBasePath, "").replace("\\", "/");
                    po.setDiffFilePath(branchReportPath);
                    po.setDiffFileDate(new Date());
                    po.setStatus("3");
                    appBranchMapper.updateById(po);
                }
            } catch (Exception e) {
                LoggerUtil.error(log, "执行失败", e);
                po.setStatus("4");
                appBranchMapper.updateById(po);
            }
        });
        return ResponseResult.ok();
    }

    private String createReport(String appcode, String filePath, ReportJacocoParamVO reportJacocoParamVO, String type) throws Exception {
        ReportJacocoParamVO reportJacocoParamVOT = new ReportJacocoParamVO();
        reportJacocoParamVOT.setAppId(appcode);
        List<String> pathLs = new ArrayList<>();
        pathLs.add(filePath);
        reportJacocoParamVOT.setExecutionDataFile(pathLs);
        if(StringUtils.equals(type, "all")){
            reportJacocoParamVOT.setSourceBranchName("develop");
        }else if(StringUtils.equals(type, "branch")){
            reportJacocoParamVOT.setSourceBranchName(reportJacocoParamVO.getSourceBranchName());
            reportJacocoParamVOT.setDiffCodeFile(reportJacocoParamVO.getDiffCodeFile());
        }
        // 生成报告
        ResponseResult reportRes = reportController.report(reportJacocoParamVOT);
        String reportPath = reportRes.getMessage();
        return reportPath + "\\index.html";
    }

    public void downLoadCodeAndCompile(ReportJacocoParamVO reportJacocoParamVO, AppBranchPO po, String appcode){
        String diffPath = "";
        String sourceBranchName = "";
        // 拉取代码并编译
        LoggerUtil.info(log, String.format("应用%s，全量拉取，开始", appcode));
        sourceBranchName = "develop";
        po.setDownloadBranchDate(new Date());
        codeController.downLoadCodeAndCompile(appcode, "develop");
        po.setCompileDate(new Date());
        LoggerUtil.info(log, String.format("应用%s，全量拉取，结束", appcode));
        if(StringUtils.equals(po.getCompareType(), "branch")){
            // 拉取代码并编译
            sourceBranchName = po.getSourceBranchName();
            LoggerUtil.info(log, String.format("应用%s，源分支%s拉取，开始", appcode, po.getSourceBranchName()));
            codeController.downLoadCodeAndCompile(appcode, po.getSourceBranchName());
            LoggerUtil.info(log, String.format("应用%s，源分支%s拉取，结束", appcode, po.getSourceBranchName()));
            LoggerUtil.info(log, String.format("应用%s，目标分支%s拉取，开始", appcode, po.getTargetBranchName()));
            codeController.downLoadCodeAndCompile(appcode, po.getTargetBranchName());
            LoggerUtil.info(log, String.format("应用%s，目标分支%s拉取，结束", appcode, po.getTargetBranchName()));

            po.setCompileDate(new Date());

            // 比对分支代码
            LoggerUtil.info(log, String.format("应用%s，生成比对文件，开始", appcode));
            ResponseResult diffRes = codeController.generateCodeDiffJsonFile(appcode, po.getSourceBranchName(), po.getTargetBranchName());
            diffPath = Objects.toString(diffRes.getMessage(), "");
            reportJacocoParamVO.setDiffCodeFile(diffPath);
            LoggerUtil.info(log, String.format("应用%s，生成比对文件，保存路径为%s，结束", appcode, diffPath));
            po.setCompareDate(new Date());
        }
        reportJacocoParamVO.setSourceBranchName(sourceBranchName);
    }

}
