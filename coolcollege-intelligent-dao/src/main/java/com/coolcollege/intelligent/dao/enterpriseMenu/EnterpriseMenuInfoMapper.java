package com.coolcollege.intelligent.dao.enterpriseMenu;

import com.coolcollege.intelligent.model.enterpriseMenu.EnterpriseMenuInfoDO;
import org.apache.ibatis.annotations.Param;

public interface EnterpriseMenuInfoMapper {

    void create(@Param("menuInfoDO") EnterpriseMenuInfoDO menuInfoDO);

    void updateById(@Param("menuInfoDO") EnterpriseMenuInfoDO menuInfoDO);

    EnterpriseMenuInfoDO getByEnterpriseId(@Param("enterpriseId") String enterpriseId);
}
