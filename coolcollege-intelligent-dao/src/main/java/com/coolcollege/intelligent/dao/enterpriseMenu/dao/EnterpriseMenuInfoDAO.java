package com.coolcollege.intelligent.dao.enterpriseMenu.dao;

import com.coolcollege.intelligent.dao.enterpriseMenu.EnterpriseMenuInfoMapper;
import com.coolcollege.intelligent.model.enterpriseMenu.EnterpriseMenuInfoDO;
import com.coolcollege.intelligent.model.enterpriseMenu.EnterpriseMenuInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;

@Repository
public class EnterpriseMenuInfoDAO {

    @Resource
    private EnterpriseMenuInfoMapper enterpriseMenuInfoMapper;

    public void createOrUpdateByEnterpriseId(EnterpriseMenuInfoDTO dto) {
        EnterpriseMenuInfoDO menuInfoDO = enterpriseMenuInfoMapper.getByEnterpriseId(dto.getEnterpriseId());
        if (menuInfoDO == null) {
            menuInfoDO = new EnterpriseMenuInfoDO();
            menuInfoDO.setEnterpriseId(dto.getEnterpriseId());
            menuInfoDO.setMenuInfo(dto.getMenuInfo());
            menuInfoDO.setCreateTime(new Date());
            enterpriseMenuInfoMapper.create(menuInfoDO);
            return;
        }
        menuInfoDO.setMenuInfo(dto.getMenuInfo());
        menuInfoDO.setCreateTime(new Date());
        enterpriseMenuInfoMapper.updateById(menuInfoDO);
    }

    public EnterpriseMenuInfoDO getByEnterpriseId(String enterpriseId) {
        if (StringUtils.isBlank(enterpriseId)) {
            return null;
        }
        return enterpriseMenuInfoMapper.getByEnterpriseId(enterpriseId);
    }
}
