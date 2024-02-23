package com.digiwin.code.coverage.backend.service;

import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.dto.CodeInvokeParam;
import com.digiwin.code.coverage.backend.dto.codediff.CodeDiffParam;
import com.digiwin.code.coverage.backend.dto.codediff.DiffInfo;

public interface CodeService {

    public ResponseResult downLoadAndCompile(CodeInvokeParam codeInvokeParam);

    /**
     * getRemoteBranchs
     * @param appId
     * @return
     */
    public ResponseResult   getRemoteBranchs(String appId);

    public DiffInfo codeDiffInfo(CodeDiffParam codeDiffParam);
}
