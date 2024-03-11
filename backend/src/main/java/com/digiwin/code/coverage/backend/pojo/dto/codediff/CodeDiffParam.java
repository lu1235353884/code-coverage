package com.digiwin.code.coverage.backend.pojo.dto.codediff;

import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 14:37
 **/
@Data
@Builder
public class CodeDiffParam {

    private String appId;

    private String baseBranch;

    private String compareBrach;

    /**
     * 版本控制类型
     */
    private CodeManageTypeEnum codeManageTypeEnum;

}
