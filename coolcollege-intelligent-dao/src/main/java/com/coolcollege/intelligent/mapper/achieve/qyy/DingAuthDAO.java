package com.coolcollege.intelligent.mapper.achieve.qyy;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.dao.qyy.DingAuthMapper;
import com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy.CardDingAuthDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
@Slf4j
public class DingAuthDAO {

    @Resource
    DingAuthMapper dingAuthMapper;

    public CardDingAuthDTO judgeDingAuth(String enterpriseId, CardDingAuthDTO cardDingAuthDTO) {
        CardDingAuthDTO findResponse = dingAuthMapper.findDingAuth(cardDingAuthDTO);
        if (Objects.nonNull(findResponse)){
            cardDingAuthDTO.setFlag(Constants.ONE_STR);
            return cardDingAuthDTO;
        }else {
            dingAuthMapper.insetDingAuth(cardDingAuthDTO);
            cardDingAuthDTO.setFlag(Constants.ZERO_STR);
            return cardDingAuthDTO;
        }
    }
}
