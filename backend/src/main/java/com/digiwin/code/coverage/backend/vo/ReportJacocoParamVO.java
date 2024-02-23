package com.digiwin.code.coverage.backend.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @ProjectName: code-diff-parent
 * @Package: com.dr.code.diff.jacoco.bean
 * @Description: java类作用描述
 * @Author: duanrui
 * @CreateDate: 2023/4/16 17:08
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2023
 */
@Data
@ApiModel
public class ReportJacocoParamVO {


    /**
     * exec文件目录
     * 0
     */
    private List<String> executionDataFile;



    /**
     * diff代码文件
     */
    private String diffCodeFile;



    /**
     * appid
     */
    private String appId;

}
