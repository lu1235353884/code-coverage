package com.digiwin.code.coverage.backend.service.impl;

import com.digiwin.code.coverage.backend.common.exception.BaseException;

import com.digiwin.code.coverage.backend.common.log.LoggerUtil;
import com.digiwin.code.coverage.backend.common.response.ResultCode;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.pojo.dto.ReportJacocoParam;
import com.digiwin.code.coverage.backend.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.jacoco.cli.internal.core.analysis.Analyzer;
import org.jacoco.cli.internal.core.analysis.CoverageBuilder;
import org.jacoco.cli.internal.core.analysis.IBundleCoverage;
import org.jacoco.cli.internal.core.internal.diff.JsonReadUtil;
import org.jacoco.cli.internal.core.tools.ExecFileLoader;
import org.jacoco.cli.internal.report.*;
import org.jacoco.cli.internal.report.html.HTMLFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 16:52
 **/
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    private CustomizeConfig customizeConfig;

    @Override
    public void reportJacoco(ReportJacocoParam reportJacocoParam)  {

        //有APPid，无视出入路径
        reportJacocoParam.setClassesDirectory(new ArrayList<String>());
        reportJacocoParam.setSourceDirectory(new ArrayList<String>());
        //根据是否有diff的入参来确认全量报告还是增量报告
        reportJacocoParam.setReportDirectory(customizeConfig.getReportDir()+"/"+reportJacocoParam.getAppId()+(StringUtils.isEmpty(reportJacocoParam.getDiffCodeFile())?"":"-diff"));
        reportJacocoParam.setReportName(reportJacocoParam.getAppId());
        String appPath = customizeConfig.getGitLocalBaseRepoDir()+"/"+reportJacocoParam.getAppId()+"_backend/develop/develop/module/";

        try{
            Stream<Path> stream = Files.walk(Paths.get(appPath), 1);
            List<String> moduleList= stream.filter(Files::isDirectory) // 确保是目录
                    .filter(p -> !p.equals(Paths.get(appPath))) // 排除根目录
                    .map(Path::getFileName) // 获取文件或文件夹名称
                    .map(Path::toString) // 转换为字符串
                    .collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(moduleList)){
                moduleList.forEach(m->{
                    reportJacocoParam.getClassesDirectory().add(appPath+m+"/"+reportJacocoParam.getAppId()+"-service-impl-"+m+"/target/classes");
                    reportJacocoParam.getSourceDirectory().add(appPath+m+"/"+reportJacocoParam.getAppId()+"-service-impl-"+m+"/src/main/java");
                });
                //解析exec
                ExecFileLoader execFileLoader = loadExecutionData(reportJacocoParam.getExecutionDataFile());
                //对比exec和class类，生成覆盖数据
                IBundleCoverage bundleCoverage = analyzeStructure(reportJacocoParam, execFileLoader);
                //生成报告
                createReport(bundleCoverage, execFileLoader, reportJacocoParam);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 第一步解析exec文件
     */
    public ExecFileLoader loadExecutionData(List<String> executionDataFile) {
        if (CollectionUtils.isEmpty(executionDataFile)) {
            throw new BaseException(ResultCode.JACOCO_EXEC_NOT_EXIST);
        }
        // 解析exec
        ExecFileLoader execFileLoader = new ExecFileLoader();
        executionDataFile.forEach(e -> {
            try {
                execFileLoader.load(new File(e));
            } catch (IOException ex) {
                throw new BaseException(ResultCode.JACOCO_REPORT_FAIL.getCode(), ex.getMessage());
            }
        });
        return execFileLoader;
    }

    /**
     * 分析结构
     *
     * @param reportJacocoParam 报告jacoco参数
     * @param execFileLoader    exec文件加载器
     * @return {@link IBundleCoverage}
     */
    public IBundleCoverage analyzeStructure(ReportJacocoParam reportJacocoParam, ExecFileLoader execFileLoader) {
        try {

            CoverageBuilder builder;
            // 如果有增量参数将其设置进去
            if (null != reportJacocoParam.getDiffCodeFile()) {
                builder = new CoverageBuilder(
                        JsonReadUtil.readJsonToString(reportJacocoParam.getDiffCodeFile()), reportJacocoParam.getExcludedClassesDirectory());
            } else {
                builder = new CoverageBuilder(reportJacocoParam.getExcludedClassesDirectory());
            }
            final Analyzer analyzer = new Analyzer(execFileLoader.getExecutionDataStore(), builder);
            List<String> classesDirectory = reportJacocoParam.getClassesDirectory();
            // class类用于类方法的比较，源码只用于最后的着色
            for (String f : classesDirectory) {
                File file = new File(f);
                analyzer.analyzeAll(file);
            }
            return builder.getBundle(reportJacocoParam.getReportName());

        } catch (Exception e) {
            LoggerUtil.error(log, "解析class失败", e);
        }
        return null;
    }

    /**
     * 创建报告
     *
     * @param bundleCoverage    包覆盖
     * @param execFileLoader    exec文件加载器
     * @param reportJacocoParam 报告jacoco参数
     * @throws IOException ioexception
     */
    public void createReport(final IBundleCoverage bundleCoverage, ExecFileLoader execFileLoader, ReportJacocoParam reportJacocoParam) {
        try {
            // Create a concrete report visitor based on some supplied
            // configuration. In this case we use the defaults
            final HTMLFormatter htmlFormatter = new HTMLFormatter();
            final IReportVisitor visitor = htmlFormatter.createVisitor(new FileMultiReportOutput(new File(reportJacocoParam.getReportDirectory())));

            // Initialize the report with all of the execution and session
            // information. At this point the report doesn't know about the
            // structure of the report being created
            visitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(), execFileLoader.getExecutionDataStore().getContents());

            // Populate the report structure with the bundle coverage information.
            // Call visitGroup if you need groups in your report.
            visitor.visitBundle(bundleCoverage, getSourceLocator(reportJacocoParam.getSourceDirectory()));

            visitor.visitEnd();
        } catch (Exception e) {
            LoggerUtil.error(log, "创建html报告失败", e);
            throw new BaseException(ResultCode.JACOCO_REPORT_FAIL.getCode(), e.getMessage());
        }

    }



    /**
     * 得到源代码资源
     *
     * @param sourceList 源列表
     * @return {@link ISourceFileLocator}
     */
    private ISourceFileLocator getSourceLocator(List<String> sourceList) {
        final MultiSourceFileLocator multi = new MultiSourceFileLocator(
                4);
        for (String f : sourceList) {
            File file = new File(f);
            multi.add(new DirectorySourceFileLocator(file, "utf-8", 4));
        }
        return multi;
    }
}
