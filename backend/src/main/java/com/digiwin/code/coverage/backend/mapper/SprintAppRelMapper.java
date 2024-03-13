package com.digiwin.code.coverage.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.digiwin.code.coverage.backend.pojo.po.SprintAppRelPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SprintAppRelMapper extends MPJBaseMapper<SprintAppRelPO> {
    @Insert("insert into sprint_app_rel(sprint_id, sprint_code, app_id, app_code, app_owner) " +
            "select ${sprintid}, '${sprintcode}', id, app_code, owner from app")
    int insertAllApps(@Param("sprintid")Long sprintid,
                      @Param("sprintcode")String sprintcode);

    @Select({"<script>",
            "select T1.*, T2.compare_type, T2.all_count, T2.all_file_path, T2.diff_count, T2.diff_file_path " +
            "from sprint_app_rel AS T1 " +
            "         inner join app_branch AS T2 ON T1.id = T2.rel_id and T1.app_code = T2.app_code " +
            "where T1.sprint_id = #{sprintid} "+
            "<when test='type != null'>"+
            "and T1.app_owner = #{type} "+
            "</when>"+
            " order by T1.app_code",
            "</script>"})
    List<Map<String, Object>> getListBySprintId(@Param("sprintid")Long sprintid, @Param("type")String type);
}
