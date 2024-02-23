package com.digiwin.code.coverage.backend.vercontrol;


import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ProjectName: cmdb
 * @Package: com.dr.cmdb.application.filedcheck
 * @Description: java类作用描述
 * @Author: duanrui
 * @CreateDate: 2021/3/30 10:10
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2021
 */
@Component
public class CodeControlHandlerFactory implements CommandLineRunner, ApplicationContextAware {
    private volatile ApplicationContext applicationContext;


    private static Map<String, AbstractCodeControl> handlerMap;



    /**
     * 封装策略
     * CommandLineRunner.run
     *
     * The CommandLineRunner is an interface in Spring Boot. When a class implements this interface,
     * Spring Boot will automatically run its run method after loading the application context.
     * Usually, we use this CommandLineRunner to perform startup tasks like user or database initialization, seeding, or other startup activities.
     *
     * 【Here’s a simplified sequence to run CommandLineRunner. in Spring Boot.】
     * 1.Application starts.
     * 2.Spring Boot initializes and configures beans, properties, and the application context.
     * 3.CommandLineRunner (or ApplicationRunner) methods are executed.
     * 4.The application is now ready to serve connections or requests
     * @param args
     */
    @Override
    public void run(String... args) {
        Collection<AbstractCodeControl> checkHandlers = this.applicationContext.getBeansOfType(AbstractCodeControl.class).values();
        //Function.identity() = x->x 表示自身对象，此处list 转map，使用自身对象作为value为固定用法
        setHandlerMap(checkHandlers.stream().collect(Collectors.toMap(e -> e.getType().getValue(), Function.identity())));
    }

    /**
     * 设置应用程序上下文
     *
     * @param applicationContext 应用程序上下文
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }




    /**
     * 设置处理Map
     *
     * @param handlerMap
     */
    private static void setHandlerMap(Map<String, AbstractCodeControl> handlerMap) {
        CodeControlHandlerFactory.handlerMap = handlerMap;
    }


    /**
     *
     * @param codeManageTypeEnum
     * @return
     */
    public static AbstractCodeControl getCodeControl(CodeManageTypeEnum codeManageTypeEnum){
        return handlerMap.get(codeManageTypeEnum.getValue());
    }

//    /**
//     * 执行方法校验
//     *
//     * @param versionControlDto
//     */
//    public static DiffInfo processHandler(VersionControlDto versionControlDto) {
//        CodeManageTypeEnum codeManageTypeEnum = versionControlDto.getCodeManageTypeEnum();
//        if (handlerMap.containsKey(codeManageTypeEnum.getValue())) {
//            return handlerMap.get(codeManageTypeEnum.getValue()).handler(versionControlDto);
//        }
//        return null;
//    }

//
//    public static String downloadCode(MethodInvokeDto methodInvokeDto) {
//        CodeManageTypeEnum codeManageTypeEnum = methodInvokeDto.getCodeManageTypeEnum();
//        if (handlerMap.containsKey(codeManageTypeEnum.getValue())) {
//            return handlerMap.get(codeManageTypeEnum.getValue()).downloadCode(methodInvokeDto);
//        }
//        return null;
//    }

}
