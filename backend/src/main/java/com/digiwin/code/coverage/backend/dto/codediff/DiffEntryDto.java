package com.digiwin.code.coverage.backend.dto.codediff;

import com.digiwin.code.coverage.backend.dto.codediff.ChangeLine;
import lombok.Data;
import org.eclipse.jgit.diff.DiffEntry;

import java.util.List;

/**
 * @ProjectName: code-coverage
 * @Author lujun
 * @Description todo
 * @CreateDate 2024-02-05 15:30
 **/
@Data
public class DiffEntryDto {

    /**
     * 文件包名
     */
    protected String newPath;

    /**
     * 文件变更类型
     */
    private DiffEntry.ChangeType changeType;


    /**
     * 变更行
     */
    private List<ChangeLine> lines;
}
