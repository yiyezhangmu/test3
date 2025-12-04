package com.coolcollege.intelligent.model.system.dto;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Data
public class AppMenuDTO {
    private Long id;
    private Long parentId;
    private String menuKey;
    private String menuName;
    private Boolean checked;
    private String path;
    private String  icon;
    private List<AppMenuDTO> children;
    private String env;
    private String label;
}

