package com.digiwin.code.coverage.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.digiwin.code.coverage.backend.pojo.po.AppBranchPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SprintMapper extends BaseMapper<SprintPO> {

}
