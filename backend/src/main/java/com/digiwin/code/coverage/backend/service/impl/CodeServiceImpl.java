package com.digiwin.code.coverage.backend.service.impl;

import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.constant.CodeCoverageConstant;
import com.digiwin.code.coverage.backend.dto.codediff.CodeDiffDto;
import com.digiwin.code.coverage.backend.dto.codediff.CodeDiffParam;
import com.digiwin.code.coverage.backend.dto.CodeInvokeDto;
import com.digiwin.code.coverage.backend.dto.CodeInvokeParam;
import com.digiwin.code.coverage.backend.dto.codediff.DiffInfo;
import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import com.digiwin.code.coverage.backend.service.CodeService;
import com.digiwin.code.coverage.backend.service.MavenCmdInvokeService;
import com.digiwin.code.coverage.backend.util.OrikaMapperUtil;
import com.digiwin.code.coverage.backend.vercontrol.CodeControlHandlerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description CodeServiceImpl
 * @CreateDate 2024-01-31 13:23
 **/
@Service
public class CodeServiceImpl implements CodeService {

    /**
     * maven complie
     */
    @Autowired
    MavenCmdInvokeService mavenCmdInvokeService;

    public DiffInfo codeDiffInfo(CodeDiffParam codeDiffParam){

        CodeDiffDto codeDiffDto = OrikaMapperUtil.map(codeDiffParam, CodeDiffDto.class);
        return CodeControlHandlerFactory.getCodeControl(codeDiffParam.getCodeManageTypeEnum()).getDiffCodeHandler(codeDiffDto);
    }


    /**
     * downLoadCode And Compile
     * @param codeInvokeParam
     * @return
     */
    @Override
    public ResponseResult downLoadAndCompile(CodeInvokeParam codeInvokeParam) {

        CodeInvokeDto codeInvoke = OrikaMapperUtil.map(codeInvokeParam, CodeInvokeDto.class);

        //code download
        String path = CodeControlHandlerFactory.getCodeControl(codeInvoke.getCodeManageTypeEnum()).downloadCode(codeInvoke);

        //maven compile
        mavenCmdInvokeService.compileCode(path+ CodeCoverageConstant.POM_LOCATION_ADD);

        return ResponseResult.ok();
    }

    /**
     * getRemoteBranchs
     * @param appId
     * @return
     */
    public ResponseResult   getRemoteBranchs(String appId){

        return ResponseResult.ok(CodeControlHandlerFactory.getCodeControl(CodeManageTypeEnum.GIT).getRemoteBranchs(appId));
    }
}
