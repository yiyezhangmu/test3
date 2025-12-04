package com.coolcollege.intelligent.service.enterprise.impl;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseFollowRecordsMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseFollowRecordsDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseFollowRecordsDTO;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseFollowRecordsRequest;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseFollowRecordsVO;
import com.coolcollege.intelligent.model.system.dto.BossLoginUserDTO;
import com.coolcollege.intelligent.service.enterprise.EnterpriseFollowRecordsService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenyupeng
 * @since 2021/11/24
 */
@Service
public class EnterpriseFollowRecordsServiceImpl implements EnterpriseFollowRecordsService {

    @Resource
    EnterpriseFollowRecordsMapper enterpriseFollowRecordsMapper;

    @Override
    public EnterpriseFollowRecordsDTO saveEnterpriseFollowRecords(EnterpriseFollowRecordsDTO dto, BossLoginUserDTO user) {
        EnterpriseFollowRecordsDO enterpriseFollowRecordsDO = new EnterpriseFollowRecordsDO();
        enterpriseFollowRecordsDO.setRecordDescribe(dto.getRecordDescribe());
        enterpriseFollowRecordsDO.setCluesId(dto.getCluesId());
        enterpriseFollowRecordsDO.setCreateTime(new Date());
        enterpriseFollowRecordsDO.setCreateId(user.getUserId());
        enterpriseFollowRecordsDO.setCreateName(user.getName());
        enterpriseFollowRecordsDO.setUpdateTime(new Date());
        enterpriseFollowRecordsDO.setUpdateId(user.getUserId());
        enterpriseFollowRecordsDO.setUpdateName(user.getName());
        enterpriseFollowRecordsMapper.save(enterpriseFollowRecordsDO);
        dto.setId(enterpriseFollowRecordsDO.getId());
        return dto;
    }

    @Override
    public void updateEnterpriseFollowRecords(EnterpriseFollowRecordsDTO dto, BossLoginUserDTO user) {
        EnterpriseFollowRecordsDO enterpriseFollowRecordsDO = new EnterpriseFollowRecordsDO();
        enterpriseFollowRecordsDO.setRecordDescribe(dto.getRecordDescribe());
        enterpriseFollowRecordsDO.setId(dto.getId());
        enterpriseFollowRecordsDO.setUpdateTime(new Date());
        enterpriseFollowRecordsDO.setUpdateId(user.getUserId());
        enterpriseFollowRecordsDO.setUpdateName(user.getName());
        enterpriseFollowRecordsMapper.update(enterpriseFollowRecordsDO);
    }

    @Override
    public void deleteEnterpriseFollowRecords(Long id) {
        enterpriseFollowRecordsMapper.deleteById(id);
    }

    @Override
    public PageInfo<EnterpriseFollowRecordsVO> listEnterpriseFollowRecords(EnterpriseFollowRecordsRequest request) {
        PageHelper.startPage(request.getPageNum(),request.getPageSize());
        List<EnterpriseFollowRecordsDTO> enterpriseFollowRecordsDTOS =  enterpriseFollowRecordsMapper.list(request.getCluesId());
        PageInfo<EnterpriseFollowRecordsVO> pageInfo = new PageInfo(enterpriseFollowRecordsDTOS);
        List<EnterpriseFollowRecordsVO> resultList = ListUtils.emptyIfNull(enterpriseFollowRecordsDTOS).stream()
                .map(e -> {
                    EnterpriseFollowRecordsVO vo = new EnterpriseFollowRecordsVO();
                    vo.setId(e.getId());
                    vo.setCluesId(e.getCluesId());
                    vo.setCreateId(e.getCreateId());
                    vo.setCreateName(e.getCreateName());
                    vo.setCreateTime(e.getCreateTime());
                    vo.setUpdateId(e.getUpdateId());
                    vo.setUpdateName(e.getUpdateName());
                    vo.setUpdateTime(e.getUpdateTime());
                    vo.setRecordDescribe(e.getRecordDescribe());
                    return vo;
                }).collect(Collectors.toList());
        pageInfo.setList(resultList);
        return pageInfo;
    }
}
