package com.coolcollege.intelligent.service.supervison.impl;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.metatable.TbMetaDefTableColumnMapper;
import com.coolcollege.intelligent.dao.supervision.dao.SupervisionDefDataColumnDao;
import com.coolcollege.intelligent.model.enums.SupervisionSubTaskStatusEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaDefTableColumnDO;
import com.coolcollege.intelligent.model.supervision.SupervisionDefDataColumnDO;
import com.coolcollege.intelligent.model.supervision.dto.SupervisionDefDataColumnDTO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionDataColumnVO;
import com.coolcollege.intelligent.model.supervision.vo.SupervisionStoreTaskDetailVO;
import com.coolcollege.intelligent.service.supervison.SupervisionDefDataColumnService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2023/2/28 15:36
 * @Version 1.0
 */
@Service
public class SupervisionDefDataColumnServiceImpl implements SupervisionDefDataColumnService {
    @Resource
    SupervisionDefDataColumnDao supervisionDefDataColumnDao;
    @Resource
    TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;

    @Override
    public List<SupervisionDefDataColumnDTO> getDataColumnListByTaskIdAndType(String enterpriseId, List<Long> taskIds, String type) {
        if (CollectionUtils.isEmpty(taskIds) || type ==null){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<SupervisionDefDataColumnDTO> dataColumnListByTaskIdAndType = supervisionDefDataColumnDao.getDataColumnListByTaskIdAndType(enterpriseId, taskIds, type);
        return dataColumnListByTaskIdAndType;
    }

    @Override
    public SupervisionDataColumnVO getSupervisionDataColumn(String enterpriseId, Long taskId,String formId, String type) {
        if (StringUtils.isEmpty(formId)){
            throw  new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        SupervisionDataColumnVO supervisionDataColumnVO = new SupervisionDataColumnVO();
        List<SupervisionDefDataColumnDTO> data = getDataColumnListByTaskIdAndType(enterpriseId, Arrays.asList(taskId), type);
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, Long.valueOf(formId));
        supervisionDataColumnVO.setSupervisionDefDataColumnDTOS(data);
        supervisionDataColumnVO.setTbMetaDefTableColumnDOS(tbMetaDefTableColumnDOS);
        return supervisionDataColumnVO;
    }



}
