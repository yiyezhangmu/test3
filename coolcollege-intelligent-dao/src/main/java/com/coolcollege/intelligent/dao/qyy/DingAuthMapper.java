package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.CardDingAuthDTO;
import org.apache.ibatis.annotations.Param;

public interface DingAuthMapper {

    CardDingAuthDTO findDingAuth(@Param("cardDingAuthDTO") CardDingAuthDTO cardDingAuthDTO);

    void insetDingAuth(@Param("cardDingAuthDTO") CardDingAuthDTO cardDingAuthDTO);
}
