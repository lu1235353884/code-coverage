package com.digiwin.code.coverage.backend.dto.codediff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 15:56
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DiffInfo {

    /**
     * 老项目路径
     */
    private String baseLocalPath;
    /**
     * 新项目路径
     */
    private String compareLocalPath;

    /**
     * diff类
     */
    private List<DiffClassInfoResult> diffClasses;
}
