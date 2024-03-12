package com.digiwin.code.coverage.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SprintMapper extends BaseMapper<SprintPO> {

    @Update("update sprint set is_compare = false")
    int updateCompare();

}
