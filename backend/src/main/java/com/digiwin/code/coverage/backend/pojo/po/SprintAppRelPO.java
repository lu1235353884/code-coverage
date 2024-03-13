package com.digiwin.code.coverage.backend.pojo.po;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName(value = "sprint_app_rel")
public class SprintAppRelPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sprintId;
    private String sprintCode;
    private Long appId;
    private String appCode;

    @TableField(exist = false)
    private String allCount;
    @TableField(exist = false)
    private String allFilePath;
    @TableField(exist = false)
    private String diffCount;
    @TableField(exist = false)
    private String diffFilePath;
    @TableField(exist = false)
    private String compareType;

    public static SprintAppRelPO createRelPO(SprintAppRelPO rel, SprintPO sprintPO) {
        SprintAppRelPO relPO = new SprintAppRelPO();
        relPO.setAppId(rel.getAppId());
        relPO.setAppCode(rel.getAppCode());
        relPO.setSprintId(sprintPO.getId());
        relPO.setSprintCode(sprintPO.getSprintCode());
        return relPO;
    }
}
