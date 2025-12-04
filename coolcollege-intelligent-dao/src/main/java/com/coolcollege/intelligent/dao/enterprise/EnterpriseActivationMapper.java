package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseActivationPageDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseActivationVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhangchenbiao
 * @date 2025-07-28 05:26
 */
public interface EnterpriseActivationMapper {

    /**
     * 获取企业授权列表
     * @return
     */
    Page<EnterpriseActivationVO> getEnterpriseActivationPage(@Param("request") EnterpriseActivationPageDTO param);

}