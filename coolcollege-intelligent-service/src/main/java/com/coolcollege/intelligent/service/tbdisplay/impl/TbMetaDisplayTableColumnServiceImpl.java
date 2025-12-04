package com.coolcollege.intelligent.service.tbdisplay.impl;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.service.tbdisplay.TbMetaDisplayTableColumnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author byd
 * @date 2021-03-02 19:50
 */
@Service
@Slf4j
public class TbMetaDisplayTableColumnServiceImpl implements TbMetaDisplayTableColumnService {

    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;

    @Override
    public TbMetaDisplayTableColumnDO getByMetaColumnId(String enterpriseId, Long metaColumnId) {
        TbMetaDisplayTableColumnDO tbMetaDisplayTableColumnDO = tbMetaDisplayTableColumnMapper.getById(enterpriseId, metaColumnId);
        if(tbMetaDisplayTableColumnDO == null){
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查项不存在【"+metaColumnId+"】");
        }
        return tbMetaDisplayTableColumnDO;
    }
}
