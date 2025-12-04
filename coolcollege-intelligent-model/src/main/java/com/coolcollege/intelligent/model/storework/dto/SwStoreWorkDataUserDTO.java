package com.coolcollege.intelligent.model.storework.dto;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/3/24 10:58
 * @Version 1.0
 */
@Data
public class SwStoreWorkDataUserDTO {

    private Long id;

    private String tableName;

    private Date beginTime;

    private Date endTime;

}
