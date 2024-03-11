package com.digiwin.code.coverage.backend.pojo.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "app")
public class AppPO {

  @TableId(type = IdType.AUTO)
  private Long id;
  private String appCode;
  private String owner;
  private String sm;
  private String phoneNumber;

}
