package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author chenyupeng
 * @since 2022/1/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseInitDeptOrderDTO {

    private String appType;

    private String corpId;

    private String eid;

    private String dbName;

    private List<String> deptIds;
}
