package com.coolcollege.intelligent.service.achievement.impl;

import cn.hutool.core.util.StrUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.achievement.AchievementStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.achievement.AchievementFormWorkMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementFormworkMappingMapper;
import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkDTO;
import com.coolcollege.intelligent.model.achievement.dto.AchievementFormworkMappingDTO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkDO;
import com.coolcollege.intelligent.model.achievement.entity.AchievementFormworkMappingDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementFormworkVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.achievement.AchievementFormworkService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 业绩模板
 *
 * @author chenyupeng
 * @since 2021/10/25
 */
@Service
public class AchievementFormworkServiceImpl implements AchievementFormworkService {

    @Resource
    AchievementFormWorkMapper achievementFormWorkMapper;

    @Resource
    AchievementFormworkMappingMapper achievementFormworkMappingMapper;
    private final int MAX_FORMWORK_NUM=50;
    private final int MAX_FORMWORK_NUM_SENYU=300;

    @Override
    public void saveFormwork(String eid, AchievementFormworkDTO dto, CurrentUser user) {

        if(CollectionUtils.isEmpty(dto.getAchievementTypeIdList())){
            throw new ServiceException(ErrorCodeEnum.ACH_PARAM_FORMWORK_ADD_ERROR);
        }
        int count = achievementFormWorkMapper.countByName(eid,dto.getName(),null);
        if(count > 0){
            throw new ServiceException(ErrorCodeEnum.ACH_FORMWORK_NAME_REPEAT);
        }
        int maxNum = MAX_FORMWORK_NUM;
        if(Constants.SENYU_ENTERPRISE_ID.equals(eid)){
            maxNum = MAX_FORMWORK_NUM_SENYU;
        }
        if(dto.getAchievementTypeIdList().size() > maxNum){
            throw new ServiceException(ErrorCodeEnum.ACH_FORMWORK_TYPE_MAX,maxNum);
        }
        AchievementFormworkDO achievementFormworkDO = transAchievementFormworkDTO(dto,user);
        achievementFormWorkMapper.save(eid,achievementFormworkDO);
        List<AchievementFormworkMappingDO> mappingList= new ArrayList<>();
        for (Long typeId : dto.getAchievementTypeIdList()) {
            AchievementFormworkMappingDO tempDo = new AchievementFormworkMappingDO();
            tempDo.setFormworkId(achievementFormworkDO.getId());
            tempDo.setTypeId(typeId);
            tempDo.setStatus(AchievementStatusEnum.NORMAL.getCode());
            mappingList.add(tempDo);
        }
        achievementFormworkMappingMapper.batchSave(eid,mappingList);

    }

    public AchievementFormworkDO transAchievementFormworkDTO(AchievementFormworkDTO trans,CurrentUser user){
        AchievementFormworkDO entity = new AchievementFormworkDO();
        entity.setName(trans.getName());
        entity.setType(trans.getType());
        entity.setCreateId(user.getUserId());
        entity.setCreateName(user.getName());
        entity.setUpdateId(user.getUserId());
        entity.setUpdateName(user.getName());
        return entity;
    }

    @Override
    public void updateFormwork(String eid, AchievementFormworkDTO dto, CurrentUser user) {
        int count = achievementFormWorkMapper.countByName(eid,dto.getName(),dto.getId());
        if(count > 0){
            throw new ServiceException(ErrorCodeEnum.ACH_FORMWORK_NAME_REPEAT);
        }
        AchievementFormworkDO entity = new AchievementFormworkDO();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setStatus(dto.getStatus());
        entity.setUpdateId(user.getUserId());
        entity.setUpdateName(user.getName());
        achievementFormWorkMapper.update(eid,entity);

        if(CollectionUtils.isEmpty(dto.getAchievementTypeIdList())){
            return;
        }
        int maxNum = MAX_FORMWORK_NUM;
        if(Constants.SENYU_ENTERPRISE_ID.equals(eid)){
            maxNum = MAX_FORMWORK_NUM_SENYU;
        }
        List<AchievementFormworkMappingDTO> achievementFormworkMappingDTOS = achievementFormworkMappingMapper.getListByFormWorkId(eid, dto.getId(),null);
        if(achievementFormworkMappingDTOS != null && achievementFormworkMappingDTOS.size() + dto.getAchievementTypeIdList().size() > maxNum){
            throw new ServiceException(ErrorCodeEnum.ACH_FORMWORK_TYPE_MAX,maxNum);
        }
        Map<Long, AchievementFormworkMappingDTO> mappingDTOMap = ListUtils.emptyIfNull(achievementFormworkMappingDTOS).stream().collect(Collectors.toMap(AchievementFormworkMappingDTO::getTypeId, data -> data, (a, b) -> b));
        List<AchievementFormworkMappingDO> mappingList= new ArrayList<>();
        List<Long> oldList = new ArrayList<>();
        for (Long typeId : dto.getAchievementTypeIdList()) {
            if(mappingDTOMap.get(typeId) != null){
                oldList.add(typeId);
            }else {
                AchievementFormworkMappingDO tempDo = new AchievementFormworkMappingDO();
                tempDo.setFormworkId(dto.getId());
                tempDo.setTypeId(typeId);
                tempDo.setStatus(AchievementStatusEnum.NORMAL.getCode());
                mappingList.add(tempDo);
            }

        }
        if(CollectionUtils.isNotEmpty(oldList)){
            achievementFormworkMappingMapper.updateByFormworkIdAndTypeIds(eid, dto.getId(), oldList,AchievementStatusEnum.NORMAL.getCode());
        }
        if(CollectionUtils.isNotEmpty(mappingList)){
            achievementFormworkMappingMapper.batchSave(eid,mappingList);
        }
    }

    @Override
    public void updateMappingStatus(String eid, AchievementFormworkMappingDTO dto) {
        achievementFormworkMappingMapper.updateByFormworkIdAndTypeId(eid,dto.getFormworkId(),dto.getTypeId(),dto.getStatus());
    }

    @Override
    public List<AchievementFormworkVO> listAllFormwork(String eid, String statusStr) {

        List<AchievementFormworkDO> achievementFormworkDOS = achievementFormWorkMapper.listAll(eid, StrUtil.splitTrim(statusStr, ","));
        if(CollectionUtils.isEmpty(achievementFormworkDOS)){
            return new ArrayList<>();
        }
        return achievementFormworkDOS.stream().map(this::getAchievementFormworkVO).collect(Collectors.toList());
    }

    @Override
    public PageInfo<AchievementFormworkVO> listFormwork(String eid, AchievementRequest request) {
        List<String> notDeleteList = new ArrayList<>();
        List<Integer> statusList = request.getStatusList();
        if(CollectionUtils.isEmpty(statusList)){
            notDeleteList.add(String.valueOf(AchievementStatusEnum.FREEZE.getCode()));
            notDeleteList.add(String.valueOf(AchievementStatusEnum.NORMAL.getCode()));
        }else {
             notDeleteList = statusList.stream()
                    .map(data -> {
                        AchievementStatusEnum byCode = AchievementStatusEnum.getByCode(data);
                        if (byCode != null) {
                            return byCode.getCode().toString();
                        }
                        return null;
                    }).filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        PageHelper.startPage(request.getPageNum(), request.getPageSize(), true);
        List<AchievementFormworkDO> achievementFormworkDOList = achievementFormWorkMapper.listAll(eid,notDeleteList);
        if(CollectionUtils.isEmpty(achievementFormworkDOList)){
            return new PageInfo<>();
        }
        List<AchievementFormworkVO> result = achievementFormworkDOList.stream().map(this::getAchievementFormworkVO).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(achievementFormworkDOList);
        pageInfo.setList(result);
        return pageInfo;
    }

    private AchievementFormworkVO getAchievementFormworkVO(AchievementFormworkDO e) {
        AchievementFormworkVO vo = new AchievementFormworkVO();
        vo.setId(e.getId());
        vo.setName(e.getName());
        vo.setType(e.getType());
        vo.setStatus(e.getStatus());
        vo.setUpdateName(e.getUpdateName());
        vo.setEditTime(e.getEditTime());
        return vo;
    }

    @Override
    public AchievementFormworkVO getFormwork(String eid, Long id, String statusStr) {
        AchievementFormworkDO achievementFormworkDO = achievementFormWorkMapper.get(eid,id);

        List<AchievementFormworkMappingDTO> list = achievementFormworkMappingMapper.getListByFormWorkId(eid,id,StrUtil.splitTrim(statusStr, ","));
        AchievementFormworkVO vo = getAchievementFormworkVO(achievementFormworkDO);
        vo.setTypeList(list);
        return vo;
    }
}
