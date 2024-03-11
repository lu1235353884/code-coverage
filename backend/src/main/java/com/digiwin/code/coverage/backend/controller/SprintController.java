package com.digiwin.code.coverage.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.digiwin.code.coverage.backend.common.response.ResponseResult;
import com.digiwin.code.coverage.backend.mapper.SprintAppRelMapper;
import com.digiwin.code.coverage.backend.mapper.SprintMapper;
import com.digiwin.code.coverage.backend.pojo.dto.SprintDTO;
import com.digiwin.code.coverage.backend.pojo.po.AppPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintAppRelPO;
import com.digiwin.code.coverage.backend.pojo.po.SprintPO;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 冲刺管理页面
 *
 * @Author: 夏锐
 * @CreateTime: 2024-03-05  18:05
 */
@Api(value = "/sprint",tags = "sprint")
@RestController
@RequestMapping("/sprint")
@Validated
public class SprintController {

    @Autowired
    private SprintMapper sprintMapper;

    @Autowired
    private SprintAppRelMapper sprintAppRelMapper;

    @ApiOperation("获取冲刺列表")
    @RequestMapping(value = "sprintlist", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getSprintList(@RequestParam(value = "size", required = false) Integer size,
                                        @RequestParam(value = "page", required = false) Integer pageNum,
                                        @RequestParam(value = "sprintcode", required = false) String sprintCode){
        String checkStr = checkSpListParam(size, pageNum);
        if(StringUtils.isNotEmpty(checkStr)){
            return ResponseResult.fail("入参"+checkStr+"不可为空");
        }
        QueryWrapper<SprintPO> conds = new QueryWrapper<>();
        if(StringUtils.isNotEmpty(sprintCode)){
            conds.lambda().eq(SprintPO::getSprintCode, sprintCode);
        }
        Page<SprintPO> page = new Page<>();
        page.setSize(size);
        page.setPages(pageNum);
        Page<SprintPO> pageRtn = sprintMapper.selectPage(page, conds);
        return ResponseResult.ok(pageRtn);
    }

    private String checkSpListParam(Integer size, Integer pageNum) {
        List<String> ls = new ArrayList<>();
        if(size==null){
            ls.add("size");
        }
        if(pageNum==null){
            ls.add("pageNum");
        }
        if(CollectionUtils.isEmpty(ls)){
            return null;
        }
        return String.join("、", ls);
    }

    @Transactional
    @ApiOperation("增加冲刺")
    @RequestMapping(value = "sprint", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult addApp(@RequestBody SprintPO sprintPO){
        if(sprintPO == null){
            return ResponseResult.fail("入参不可为空");
        }
        List<SprintAppRelPO> rels = sprintPO.getRelPOS();
        sprintMapper.insert(sprintPO);
        sprintAppRelMapper.insertAllApps(sprintPO.getId(), sprintPO.getSprintCode());
//        if(CollectionUtils.isNotEmpty(rels)){
//            for(SprintAppRelPO rel : rels){
//                sprintAppRelMapper.insert(SprintAppRelPO.createRelPO(rel, sprintPO));
//            }
//        }
        return ResponseResult.ok();
    }

    @ApiOperation("查询单个冲刺")
    @RequestMapping(value = "sprint", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getApp(@RequestParam(value = "sprintid", required = false) Long sprintId){
        if(sprintId == null){
            return ResponseResult.fail("入参sprintid不可为空");
        }
        SprintPO po = sprintMapper.selectById(sprintId);
        return ResponseResult.ok(po);
    }

    @ApiOperation("分页查询冲刺包含的应用")
    @RequestMapping(value = "sprint/rels", method = RequestMethod.GET)
    @ResponseBody
    public ResponseResult getRelAppOnPage(@RequestParam(value = "size", required = false) Integer size,
                                          @RequestParam(value = "page", required = false) Integer pageNum,
                                          @RequestParam(value = "sprintid", required = false) Long sprintId){
        String checkStr = checkParam(size, pageNum, sprintId);
        if(StringUtils.isNotEmpty(checkStr)){
            return ResponseResult.fail("入参"+checkStr+"不可为空");
        }
        QueryWrapper<SprintAppRelPO> conds = new QueryWrapper<>();
        conds.lambda().eq(SprintAppRelPO::getSprintId, sprintId);
        Page<SprintAppRelPO> page = new Page<>();
        page.setSize(size);
        page.setPages(pageNum);
        Page<SprintAppRelPO> pageRtn = sprintAppRelMapper.selectPage(page, conds);
        return ResponseResult.ok(pageRtn);
    }

    private String checkParam(Integer size, Integer pageNum, Long sprintId) {
        List<String> ls = new ArrayList<>();
        if(size==null){
            ls.add("size");
        }
        if(pageNum==null){
            ls.add("pageNum");
        }
        if(sprintId==null){
            ls.add("sprintId");
        }
        if(CollectionUtils.isEmpty(ls)){
            return null;
        }
        return String.join("、", ls);
    }

    @ApiOperation("删除冲刺")
    @RequestMapping(value = "sprint", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseResult deleteApp(@RequestParam(value = "ids", required = true) List<String> ids){
        if(CollectionUtils.isEmpty(ids)){
            return ResponseResult.fail("入参sprintcode不可为空");
        }
        QueryWrapper<SprintPO> conds = new QueryWrapper<>();
        conds.lambda().and( wq -> {
            for(String id : ids){
                wq.or().eq(SprintPO::getId, id);
            }
        });
        sprintMapper.delete(conds);
        QueryWrapper<SprintAppRelPO> relConds = new QueryWrapper<>();
        relConds.lambda().and( wq -> {
            for(String id : ids){
                wq.or().eq(SprintAppRelPO::getSprintCode, id);
            }
        });
        sprintAppRelMapper.delete(relConds);
        return ResponseResult.ok();
    }

    @ApiOperation("更新冲刺")
    @RequestMapping(value = "sprint", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseResult updateApp(@RequestBody SprintPO sprintPO){
        if(sprintPO == null || sprintPO.getId() == null){
            return ResponseResult.fail("入参不可为空");
        }
        List<SprintAppRelPO> rels = sprintPO.getRelPOS();
        sprintMapper.updateById(sprintPO);
        if(CollectionUtils.isNotEmpty(rels)){
            QueryWrapper<SprintAppRelPO> conds = new QueryWrapper<>();
            conds.lambda().eq(SprintAppRelPO::getSprintId, sprintPO.getId());
            sprintAppRelMapper.delete(conds);
            for(SprintAppRelPO rel : rels){
                sprintAppRelMapper.insert(SprintAppRelPO.createRelPO(rel, sprintPO));
            }
        }
        return ResponseResult.ok();
    }

}
