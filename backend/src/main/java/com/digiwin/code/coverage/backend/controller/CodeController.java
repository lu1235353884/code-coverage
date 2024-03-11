package com.digiwin.code.coverage.backend.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.pojo.dto.CodeInvokeParam;
import com.digiwin.code.coverage.backend.pojo.dto.codediff.CodeDiffParam;
import com.digiwin.code.coverage.backend.pojo.dto.codediff.DiffClassInfoResult;
import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import com.digiwin.code.coverage.backend.service.CodeService;
import com.digiwin.code.coverage.backend.util.OrikaMapperUtil;
import com.digiwin.code.coverage.backend.pojo.vo.CodeDiffResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-01-30 15:40
 **/

@Api(value = "/code",tags = "代码管理")
@RestController
@RequestMapping("/code")
@Validated
public class CodeController {


    @Autowired
    private CodeService codeService;

    @Autowired
    private CustomizeConfig customizeConfig;

    @ApiOperation("获取分支列表")
    @RequestMapping(value = "branchList", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getBranchList(@ApiParam(required = true, name = "appId", value = "appId")
                                                   @NotEmpty
                                                   @RequestParam(value = "appId") String appId){


        return ResponseResult.ok(codeService.getRemoteBranchs(appId.toLowerCase()));
    }

    @ApiOperation("下载代码并编译")
    @RequestMapping(value = "downLoadCodeAndCompile", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult downLoadCodeAndCompile(
            @ApiParam(required = true, name = "appId", value = "appId")
            @NotEmpty
            @RequestParam(value = "appId") String appId,
            @ApiParam(required = true, name = "branchName", value = "branchName")
            @NotEmpty
            @RequestParam(value = "branchName") String branchName){

        return ResponseResult.ok(codeService.downLoadAndCompile(CodeInvokeParam.builder().appId(appId.toLowerCase()).branchName(branchName).codeManageTypeEnum(CodeManageTypeEnum.GIT).build()));
    }

    @ApiOperation("比较分支差异生成json文件")
    @RequestMapping(value = "generateCodeDiffJsonFile", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult generateCodeDiffJsonFile( @ApiParam(required = true, name = "appId", value = "appId")
                                                    @NotEmpty
                                                    @RequestParam(value = "appId") String appId,
                                                    @ApiParam(required = true, name = "baseBranch", value = "baseBranch")
                                                    @NotEmpty
                                                    @RequestParam(value = "baseBranch") String baseBranch,
                                                    @ApiParam(required = true, name = "compareBranch", value = "compareBranch")
                                                    @NotEmpty
                                                    @RequestParam(value = "compareBranch") String compareBranch){


        List<DiffClassInfoResult> diffCodeList = codeService.codeDiffInfo(CodeDiffParam.builder().appId(appId.toLowerCase()).baseBranch(baseBranch).compareBrach(compareBranch).codeManageTypeEnum(CodeManageTypeEnum.GIT).build()).getDiffClasses();
        List<CodeDiffResultVO> list = OrikaMapperUtil.mapList(diffCodeList, DiffClassInfoResult.class, CodeDiffResultVO.class);
        String diffCodePath = customizeConfig.getJacocoRootPath() + "/diff/" + appId+"_"+baseBranch+"_"+compareBranch+".json";
        if (!CollectionUtils.isEmpty(list)) {
            FileUtil.writeUtf8String(JSONUtil.toJsonStr(list), diffCodePath);
        }
        return ResponseResult.ok(diffCodePath);
    }



}
