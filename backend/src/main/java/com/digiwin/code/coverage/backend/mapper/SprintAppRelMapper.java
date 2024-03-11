package com.digiwin.code.coverage.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.digiwin.code.coverage.backend.pojo.po.SprintAppRelPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SprintAppRelMapper extends MPJBaseMapper<SprintAppRelPO> {
    @Insert("insert into sprint_app_rel(sprint_id, sprint_code, app_id, app_code) " +
            "select ${sprintid}, '${sprintcode}', id, app_code from app")
    int insertAllApps(@Param("sprintid")Long sprintid,
                      @Param("sprintcode")String sprintcode);
}
