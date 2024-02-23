package com.digiwin.code.coverage.backend.dto;

import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Package: com.dr.code.diff.dto
 * @Description: java类作用描述
 * @Author: rayduan
 * @CreateDate: 2023/3/8 21:22
 * @Version: 1.0
 * <p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeInvokeDto {

    /**
     *
     */
    private String appId;

    /**
     * 分支或tag
     */
    private String branchName = "";



    /**
     * 版本控制类型
     */
    private CodeManageTypeEnum codeManageTypeEnum;

}
