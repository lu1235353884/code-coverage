package com.digiwin.code.coverage.backend.vercontrol.git;

import com.alibaba.fastjson.JSON;
import com.digiwin.code.coverage.backend.common.exception.BaseException;
import com.digiwin.code.coverage.backend.common.log.LoggerUtil;
import com.digiwin.code.coverage.backend.common.response.ResultCode;
import com.digiwin.code.coverage.backend.config.CustomizeConfig;
import com.digiwin.code.coverage.backend.constant.CodeCoverageConstant;
import com.digiwin.code.coverage.backend.dto.codediff.ChangeLine;
import com.digiwin.code.coverage.backend.dto.codediff.DiffEntryDto;
import com.digiwin.code.coverage.backend.dto.GitInfoDto;
import com.digiwin.code.coverage.backend.dto.CodeInvokeDto;
import com.digiwin.code.coverage.backend.enums.CodeManageTypeEnum;
import com.digiwin.code.coverage.backend.enums.GitUrlTypeEnum;
import com.digiwin.code.coverage.backend.util.FileUtils;
import com.digiwin.code.coverage.backend.util.GitRepoUtil;
import com.digiwin.code.coverage.backend.util.OrikaMapperUtil;
import com.digiwin.code.coverage.backend.vercontrol.AbstractCodeControl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-01-31 14:18
 **/
@Component
@Slf4j
public class GitCodeControl extends AbstractCodeControl {

    @Override
    public CodeManageTypeEnum getType() {
        return CodeManageTypeEnum.GIT;
    }

    @Autowired
    private CustomizeConfig customizeConfig;

    @Override
    public String downloadCode(CodeInvokeDto codeInvokeDto) {

        GitInfoDto gitInfo = getGitInfo(getRepoUrl(codeInvokeDto.getAppId())+codeInvokeDto.getAppId()+"/"+codeInvokeDto.getAppId()+ CodeCoverageConstant.GIT_REPOSITORY_ADD, codeInvokeDto.getBranchName());
        return gitInfo.getLocalBaseRepoDir();
    }

    @Override
    public List<String> getRemoteBranchs(String appId) {
        return GitRepoUtil.getRemoteBranchs(getRepoUrl(appId)+appId+"/"+appId+ CodeCoverageConstant.GIT_REPOSITORY_ADD,customizeConfig.getGitUserName(), customizeConfig.getGitPassWord());
    }

    public  String getRepoUrl(String appId){
        if(appId.startsWith("bm")){
            return customizeConfig.getBmUrl();
        }else{
            return customizeConfig.getAppUrl();
        }
    }

    /**
     * git获取信息
     *
     * @param repoUrl    仓库url
     * @param branchName 分支机构名称
     * @return {@link GitInfoDto}
     */
    public GitInfoDto getGitInfo(String repoUrl, String branchName) {
        Git git = null;
        String localBaseRepoDir = GitRepoUtil.getLocalDir(repoUrl, customizeConfig.getGitLocalBaseRepoDir(), branchName);
        GitUrlTypeEnum gitUrlTypeEnum = GitRepoUtil.judgeUrlType(repoUrl);
        if (null == gitUrlTypeEnum) {
            throw new BaseException(ResultCode.UNKNOWN_REPOSITY_URL);
        }
        switch (Objects.requireNonNull(gitUrlTypeEnum)) {
            case HTTP: {
                //原有代码git对象
                git = GitRepoUtil.httpCloneRepository(repoUrl, localBaseRepoDir, branchName, customizeConfig.getGitUserName(), customizeConfig.getGitPassWord());
                break;
            }
            case SSH: {
                localBaseRepoDir += GitUrlTypeEnum.SSH.getValue();
                //原有代码git对象
                git = GitRepoUtil.sshCloneRepository(repoUrl, localBaseRepoDir, branchName, customizeConfig.getGitSshPrivateKey());
                break;
            }
            default: {
                LoggerUtil.error(log, "未知类型仓库地址");
                throw new BaseException(ResultCode.UNKNOWN_REPOSITY_URL);
            }
        }
        return GitInfoDto.builder().git(git).localBaseRepoDir(localBaseRepoDir).build();
    }

    /**
     *
     */
    @Override
    public void getDiffCode() {
        try {
            GitInfoDto baseGitInfo = getGitInfo(
                    getRepoUrl(super.codeDiffDto.getAppId())+super.codeDiffDto.getAppId()+"/"+super.codeDiffDto.getAppId()+ CodeCoverageConstant.GIT_REPOSITORY_ADD,
                    super.codeDiffDto.getBaseBranch());

            Git baseGit = baseGitInfo.getGit();
            GitInfoDto nowGitInfo = getGitInfo(
                    getRepoUrl(super.codeDiffDto.getAppId())+super.codeDiffDto.getAppId()+"/"+super.codeDiffDto.getAppId()+ CodeCoverageConstant.GIT_REPOSITORY_ADD,
                    super.codeDiffDto.getCompareBrach());
            Git nowGit = nowGitInfo.getGit();
            super.codeDiffDto.setBaseLocalPath(baseGitInfo.getLocalBaseRepoDir());
            super.codeDiffDto.setCompareLocalPath(nowGitInfo.getLocalBaseRepoDir());
            AbstractTreeIterator baseTree = GitRepoUtil.prepareTreeParser(baseGit.getRepository(), super.codeDiffDto.getBaseBranch());
            AbstractTreeIterator nowTree = GitRepoUtil.prepareTreeParser(nowGit.getRepository(), super.codeDiffDto.getCompareBrach());
            //获取两个版本之间的差异代码
            List<DiffEntry> diff = null;
            diff = nowGit.diff().setOldTree(baseTree).setNewTree(nowTree).setShowNameAndStatusOnly(true).call();
            //过滤出有效的差异代码
            Collection<DiffEntry> validDiffList = diff.stream()
                    //只计算java文件和xml
                    .filter(e -> e.getNewPath().endsWith(".java") || e.getNewPath().endsWith(".xml"))
                    //排除测试文件
                    .filter(e -> {
                        if (e.getNewPath().endsWith(".java")) {
                            return e.getNewPath().contains(customizeConfig.getRootCodePath());
                        }
                        return Boolean.TRUE;
                    })
                    //只计算新增和变更文件
                    .filter(e -> DiffEntry.ChangeType.ADD.equals(e.getChangeType()) || DiffEntry.ChangeType.MODIFY.equals(e.getChangeType()))
                    .collect(Collectors.toList());
            //计算xml变更引起的mapper方法变更
            if (CollectionUtils.isEmpty(validDiffList)) {
                LoggerUtil.info(log, "没有需要对比的类");
                return;
            }
            List<DiffEntryDto> diffEntries = OrikaMapperUtil.mapList(validDiffList, DiffEntry.class, DiffEntryDto.class);

            Map<String, DiffEntryDto> diffMap = diffEntries.stream().collect(Collectors.toMap(DiffEntryDto::getNewPath, Function.identity()));
            LoggerUtil.info(log, "需要对比的差异类为：", JSON.toJSON(diffEntries));
            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(nowGit.getRepository());
            diffFormatter.setContext(0);
            //此处是获取变更行，有群友需求新增行或变更行要在类中打标记，此处忽略删除行
            for (DiffEntry diffClass : validDiffList) {
                //获取变更行
                EditList edits = diffFormatter.toFileHeader(diffClass).toEditList();
                if (CollectionUtils.isEmpty(edits)) {
                    continue;
                }
                //获取出新增行和变更行
                List<Edit> list = edits.stream().filter(e -> Edit.Type.INSERT.equals(e.getType()) || Edit.Type.REPLACE.equals(e.getType())).collect(Collectors.toList());
                List<ChangeLine> lines = new ArrayList<>(list.size());
                list.forEach(
                        edit -> {
                            ChangeLine build = ChangeLine.builder().startLineNum(edit.getBeginB()).endLineNum(edit.getEndB()).type(edit.getType().name()).build();
                            lines.add(build);
                        }
                );
                if (diffMap.containsKey(diffClass.getNewPath())) {
                    DiffEntryDto diffEntryDto = diffMap.get(diffClass.getNewPath());
                    diffEntryDto.setLines(lines);
                }
            }
            //设置变更行
            super.codeDiffDto.setDiffClasses(new ArrayList<DiffEntryDto>(diffMap.values()));
        } catch (IOException |
                 GitAPIException e) {
            e.printStackTrace();
            throw new BaseException(ResultCode.GET_DIFF_CLASS_ERROR);
        }

    }

    @Override
    public String getLocalBasePath(String filePackage) {
        return FileUtils.getClassFilePath(super.codeDiffDto.getBaseLocalPath(), filePackage);
    }
    @Override
    public String getLocalComparePath(String filePackage) {
        return FileUtils.getClassFilePath(super.codeDiffDto.getCompareLocalPath(), filePackage);
    }

}
