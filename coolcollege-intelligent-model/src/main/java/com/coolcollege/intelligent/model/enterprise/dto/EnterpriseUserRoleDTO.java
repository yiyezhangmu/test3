package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
public class EnterpriseUserRoleDTO {

    private Long id;

    private Long roleId;

    private String userId;

}
