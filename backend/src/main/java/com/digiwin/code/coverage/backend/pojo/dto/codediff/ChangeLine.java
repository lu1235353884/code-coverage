package com.digiwin.code.coverage.backend.pojo.dto.codediff;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 15:30
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLine {

    /**
     * 变更类型
     */
    private String type;

    private Integer startLineNum;

    private Integer endLineNum;
}
