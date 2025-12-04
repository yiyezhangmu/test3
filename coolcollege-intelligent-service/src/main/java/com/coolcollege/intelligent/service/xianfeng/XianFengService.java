package com.coolcollege.intelligent.service.xianfeng;

import com.coolcollege.intelligent.model.system.dto.UserDTO;

import java.util.List;

public interface XianFengService {
    List<UserDTO> investmentManager(String enterpriseId,Integer pageNum ,Integer pageSize);
}
