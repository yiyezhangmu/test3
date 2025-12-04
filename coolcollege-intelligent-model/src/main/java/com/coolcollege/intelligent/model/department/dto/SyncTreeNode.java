package com.coolcollege.intelligent.model.department.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/12/2 14:17
 */
@Data
public class SyncTreeNode {

    private String id;

    private String pid;

    private String name;

    private List<SyncTreeNode> child;
}
