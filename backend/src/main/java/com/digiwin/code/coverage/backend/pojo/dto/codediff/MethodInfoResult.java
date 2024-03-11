package com.digiwin.code.coverage.backend.pojo.dto.codediff;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 15:53
 **/
@Builder
@Data
public class MethodInfoResult {

    /**
     * 方法的md5
     */
    public String md5;
    /**
     * 方法名
     */
    public String methodName;
    /**
     * 方法参数
     */
    public List<String> parameters;

    public String methodSign;

    /**
     * 开始行
     */
    private Integer startLine;

    /**
     * 结束行
     */
    private Integer endLine;
}
