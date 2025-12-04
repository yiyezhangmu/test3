package com.coolcollege.intelligent.mapper.device;

import com.coolcollege.intelligent.dao.device.EnterpriseDeviceFetchStreamLogMapper;
import com.coolcollege.intelligent.model.device.EnterpriseDeviceFetchStreamLogDO;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Objects;

@Repository
public class EnterpriseDeviceFetchStreamLogDAO {

    @Resource
    private EnterpriseDeviceFetchStreamLogMapper enterpriseDeviceFetchStreamLogMapper;

    public void insertLog(EnterpriseDeviceFetchStreamLogDO fetchStreamLog) {
        if(Objects.isNull(fetchStreamLog)){
            return;
        }
        enterpriseDeviceFetchStreamLogMapper.insertLog(fetchStreamLog);
    }

}
