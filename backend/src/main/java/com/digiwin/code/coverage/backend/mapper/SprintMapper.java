package com.digiwin.code.coverage.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SprintMapper extends BaseMapper<SprintPO> {

    @Update("update sprint set is_compare = false where app_partition=#{appPartition}")
    int updateCompare(@Param("appPartition") String appPartition);

    @Update("update sprint set is_compare = true where id = #{id} ")
    int updateIsCompareById(@Param("id") Long id);

}
