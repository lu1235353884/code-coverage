package com.digiwin.code.coverage.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.digiwin.code.coverage.backend.pojo.po.AppBranchPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AppBranchMapper extends BaseMapper<AppBranchPO> {

    @Update("update app_branch " +
            "set all_file_path=null, " +
            "    all_file_date=null, " +
            "    diff_file_path=null, " +
            "    diff_file_date=null, " +
            "    download_branch_date=null, " +
            "    download_data_file_date=null, " +
            "    compile_date=null, " +
            "    compare_date=null, " +
            "    status='2', " +
            "    all_count=null," +
            "    diff_count=null " +
            "where id = #{id} ")
    int resetInfo(@Param("id")Long id);

}
