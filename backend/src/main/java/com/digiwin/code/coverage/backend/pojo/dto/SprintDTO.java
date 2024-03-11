package com.digiwin.code.coverage.backend.pojo.dto;

import com.digiwin.code.coverage.backend.pojo.po.SprintAppRelPO;

import java.util.List;

/**
 * TODO
 *
 * @Author: 夏锐
 * @CreateTime: 2024-03-05  20:51
 */
public class SprintDTO {

    private Long id;
    private String sprintCode;
    private Long isCompare;

    private List<SprintAppRelPO> relPOS;

}
