package com.digiwin.code.coverage.backend.pojo.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "sprint")
public class SprintPO {

  @TableId(type = IdType.AUTO)
  private Long id;
  private String sprintCode;
  private Long isCompare;

  @TableField(exist = false)
  @JSONField(name = "rels")
  private List<SprintAppRelPO> relPOS;
}
