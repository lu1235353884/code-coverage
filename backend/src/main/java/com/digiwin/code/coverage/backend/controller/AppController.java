package com.digiwin.code.coverage.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.mapper.AppMapper;
import com.digiwin.code.coverage.backend.pojo.po.AppPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 应用管理页面
 *
 * @Author: 夏锐
 * @CreateTime: 2024-03-05  17:19
 */
@Api(value = "/app",tags = "app")
@RestController
@RequestMapping("/app")
@Validated
public class AppController {

    @Autowired
    private AppMapper appMapper;

    @ApiOperation("获取应用列表")
    @RequestMapping(value = "applist", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getAppList(@RequestParam(value = "size", required = false) Integer size,
                                     @RequestParam(value = "page", required = false) Integer pageNum,
                                     @RequestParam(value = "appcode", required = false) String appCode,
                                     @RequestParam(value = "sortfield", required = false) String sortField,
                                     @RequestParam(value = "sortorder", required = false) String sortOrder){
        String checkStr = checkAppListParam(size, pageNum);
        if(StringUtils.isNotEmpty(checkStr)){
            return ResponseResult.fail("入参"+checkStr+"不可为空");
        }
        QueryWrapper<AppPO> conds = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(appCode)){
            conds.lambda().eq(AppPO::getAppCode, appCode);
        }
        if(StringUtils.isNotEmpty(sortField)){
            if(StringUtils.equals(sortOrder, "ascend")){
                conds.orderByAsc("app_code");
            }else if(StringUtils.equals(sortOrder, "descend")){
                conds.orderByDesc("app_code");
            }
        }
        Page<AppPO> page = new Page<>(pageNum, size);
//        page.setSize(size);
//        page.setPages(pageNum);
        Page<AppPO> pageRtn = appMapper.selectPage(page, conds);
        return ResponseResult.ok(pageRtn);
    }

    private String checkAppListParam(Integer size, Integer pageNum) {
        List<String> ls = new ArrayList<>();
        if(size==null){
            ls.add("size");
        }
        if(pageNum==null){
            ls.add("pageNum");
        }
        if(org.apache.commons.collections4.CollectionUtils.isEmpty(ls)){
            return null;
        }
        return String.join("、", ls);
    }

    @ApiOperation("增加应用")
    @RequestMapping(value = "app", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult addApp(@RequestBody AppPO appPO){
        QueryWrapper<AppPO> cond = new QueryWrapper<>();
        cond.lambda().eq(AppPO::getAppCode, appPO.getAppCode());
        AppPO t = appMapper.selectOne(cond);
        if(t!=null){
            return ResponseResult.fail(String.format("应用%s已存在，不可重复添加", appPO.getAppCode()));
        }
        return ResponseResult.ok(appMapper.insert(appPO));
    }

    @ApiOperation("查询单个应用")
    @RequestMapping(value = "app", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getApp(@RequestParam(value = "appid", required = false) Long appid){
        if(appid == null){
            return ResponseResult.fail("入参appid不可为空");
        }
        QueryWrapper<AppPO> conds = new QueryWrapper<>();
        conds.lambda().eq(AppPO::getId, appid);
        return ResponseResult.ok(appMapper.selectOne(conds));
    }

    @ApiOperation("删除应用")
    @RequestMapping(value = "app", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseResult deleteApp(@RequestParam(value = "ids", required = true) List<String> ids){
        if(CollectionUtils.isEmpty(ids)){
            return ResponseResult.fail("入参ids不可为空");
        }
        QueryWrapper<AppPO> conds = new QueryWrapper<>();
        conds.lambda().and( wq -> {
            for(String id : ids){
                wq.or().eq(AppPO::getId, id);
            }
        });
        return ResponseResult.ok(appMapper.delete(conds));
    }

    @ApiOperation("更新应用")
    @RequestMapping(value = "app", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseResult updateApp(@RequestBody AppPO appPO){
        if(appPO == null || appPO.getId() == null){
            return ResponseResult.fail("入参不可为空");
        }
        return ResponseResult.ok(appMapper.updateById(appPO));
    }
}
