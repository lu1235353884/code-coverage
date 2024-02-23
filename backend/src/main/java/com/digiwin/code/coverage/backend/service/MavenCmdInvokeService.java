package com.digiwin.code.coverage.backend.service;


import cn.hutool.core.io.FileUtil;
import com.digiwin.code.coverage.backend.common.exception.BaseException;
import com.digiwin.code.coverage.backend.common.log.LoggerUtil;
import com.digiwin.code.coverage.backend.common.response.ResultCode;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Collections;

/**
 * @Package: com.dr.code.diff.analyze
 * @Description: 使用maven invoker编译目标代码为class文件
 * @Author: rayduan
 * @CreateDate: 2023/2/27 20:47
 * @Version: 1.0
 * <p>
 */
@Service
@Slf4j
public class MavenCmdInvokeService {


    @Autowired
    private CustomizeConfig customizeConfig;

    /**
     * 运行maven cmd
     *
     * @param pomPath 要操控的pom文件的系统路径 如：D:\coding\**\pom.xml
     * @param cmd     maven命令如：clean
     */
    public void operationMavenCmd(String pomPath, String cmd) {
        InvocationRequest request = new DefaultInvocationRequest();
        //想要操控的pom文件的位置
        request.setPomFile(new File(pomPath));
        //操控的maven命令
        request.setGoals(Collections.singletonList(cmd));
        InvocationOutputHandler outputHandler = s -> LoggerUtil.info(log, s);
        request.setOutputHandler(outputHandler);
        request.setUserSettingsFile(new File(customizeConfig.getUserSetting()));
        Invoker invoker = new DefaultInvoker();
        //maven的位置
        invoker.setMavenHome(new File(customizeConfig.getMavenHome()));
        try {
            invoker.execute(request);
        } catch (MavenInvocationException e) {
            LoggerUtil.error(log, "编译项目失败:", pomPath, "失败原因：", e);
            throw new BaseException(ResultCode.COMPILE_CODE_FAIL);
        }
    }


    /**
     * 编译代码
     *
     * @param pomPath pom路径
     */
    public void compileCode(String pomPath) {
        LoggerUtil.info(log, "开始编译代码");
        //判断文件是否存在
        boolean exist = FileUtil.exist(pomPath);
        if (!exist) {
            LoggerUtil.info(log, "pom文件不存在:",pomPath);
            throw new BaseException(ResultCode.POM_NOT_EXIST_FAIL);
        }
        operationMavenCmd(pomPath, "clean compile -Dmaven.test.skip=true");
        LoggerUtil.info(log, "代码编译完成");
    }
}
