package com.digiwin.code.coverage.backend.pojo.po;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "app_branch")
public class AppBranchPO {

  @TableId(type = IdType.AUTO)
  private Long id;
  private Long relId;
  private String appCode;
  private String compareType;
  private String status;
  private String sourceBranchName;
  private String targetBranchName;
  private String allFilePath;
  private Date allFileDate;
  private String allCount;
  private String diffFilePath;
  private Date diffFileDate;
  private String diffCount;
  private Date downloadBranchDate;
  private Date downloadDataFileDate;
  private Date compileDate;
  private Date compareDate;
  
}
