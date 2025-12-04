package com.coolcollege.intelligent.service.login;

import com.coolcollege.intelligent.model.login.EnterpriseLoginCountDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/10/27
 */
public interface LoginRecordService {

    /**
     * 统计所有企业的登录数据
     * @return
     */
    List<EnterpriseLoginCountDTO> statisticsLoginRecord();
}
