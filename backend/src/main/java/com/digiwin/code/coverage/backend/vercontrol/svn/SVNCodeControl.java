package com.digiwin.code.coverage.backend.vercontrol.svn;

import com.digiwin.code.coverage.backend.pojo.dto.CodeInvokeDto;
import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import com.digiwin.code.coverage.backend.vercontrol.AbstractCodeControl;

import java.util.List;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-01-31 16:31
 **/
public class SVNCodeControl extends AbstractCodeControl {
    @Override
    public String downloadCode(CodeInvokeDto codeInvokeDto) {
        return null;
    }

    @Override
    public List<String> getRemoteBranchs(String appId) {
        return null;
    }

    @Override
    public void getDiffCode() {

    }


    @Override
    public CodeManageTypeEnum getType() {
        return null;
    }

    @Override
    public String getLocalComparePath(String filePackage) {
        return null;
    }

    @Override
    public String getLocalBasePath(String filePackage) {
        return null;
    }


}
