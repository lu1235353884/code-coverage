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
import java.util.*;

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

        ResponseResult res = codeService.getRemoteBranchs(appId.toLowerCase());
        List<String> ls = (List<String>)res.getResult();
        List<Map<String, String>> rtn = new ArrayList<>();
        for(String s : ls){
            Map<String, String> map = new HashMap<>();
            map.put("branch", s);
            rtn.add(map);
        }
        rtn.sort(Comparator.comparing(
                (Map<String, String> m) -> m.get("branch"),
                (s1, s2) -> {
                    List<String> topPriorityBranches = Arrays.asList("develop", "test");

                    // Check if either branch is a top priority branch
                    boolean isTopPriority1 = topPriorityBranches.contains(s1);
                    boolean isTopPriority2 = topPriorityBranches.contains(s2);
                    if (isTopPriority1 && !isTopPriority2) {
                        return -1; // s1 is top priority, it should come first
                    } else if (!isTopPriority1 && isTopPriority2) {
                        return 1; // s2 is top priority, it should come first
                    } else if (isTopPriority1 && isTopPriority2) {
                        return s1.compareTo(s2); // Both are top priority, sort alphabetically
                    }

                    // For 'release' branches
                    boolean isRelease1 = s1.startsWith("release");
                    boolean isRelease2 = s2.startsWith("release");
                    if (isRelease1 && isRelease2) {
                        return s2.compareTo(s1);  // Release branches in descending order
                    } else if (isRelease1) {
                        return -1; // Release branches first
                    } else if (isRelease2) {
                        return 1;  // Non-release branches later
                    }

                    // Non-special cases handled last
                    return s1.compareTo(s2);  // Non-release branches in alphabetical order
                }
        ));

        return ResponseResult.ok(rtn);
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


        List<DiffClassInfoResult> diffCodeList = codeService.codeDiffInfo(CodeDiffParam.builder().appId(appId.toLowerCase()).baseBranch(compareBranch).compareBrach(baseBranch).codeManageTypeEnum(CodeManageTypeEnum.GIT).build()).getDiffClasses();
        List<CodeDiffResultVO> list = OrikaMapperUtil.mapList(diffCodeList, DiffClassInfoResult.class, CodeDiffResultVO.class);

        if(CollectionUtils.isEmpty(list)){
            return ResponseResult.ok("nodiff");
        }
        String diffCodePath = customizeConfig.getJacocoRootPath() + "/diff/" + appId+"_"+baseBranch+"_"+compareBranch+".json";
        if (!CollectionUtils.isEmpty(list)) {
            FileUtil.writeUtf8String(JSONUtil.toJsonStr(list), diffCodePath);
        }
        return ResponseResult.ok(diffCodePath);
    }



}
