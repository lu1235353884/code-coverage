package com.digiwin.code.coverage.backend.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "app_branch")
public class AppBranchPO {

  @TableId(type = IdType.AUTO)
  private Long id;
  private Long relId;
  private String sourceBranchName;
  private String targetBranchName;
  private String filePath;

}
