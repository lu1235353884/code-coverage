package com.digiwin.code.coverage.backend.dto.codediff;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 14:53
 **/
@Data
public class CodeDiffDto {

    private String appId;

    private String baseBranch;

    private String compareBrach;

    private String baseLocalPath;

    private String compareLocalPath;

    private List<DiffEntryDto> diffClasses;

}
