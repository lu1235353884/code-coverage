package com.digiwin.code.coverage.backend.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-01 10:42
 **/
public class ShellScriptScheduler {

    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    public void runShellScript() {
        try {
            String scriptPath = "/path/to/working/directory/script.sh"; // 替换为脚本路径
            ProcessBuilder processBuilder = new ProcessBuilder(scriptPath);
            processBuilder.directory(new File("/path/to/working/directory")); // 设置工作目录
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
