package com.coolcollege.intelligent.service.achievement.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.achievement.AchievementStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.RedisOperator;
import com.coolcollege.intelligent.dao.achievement.AchievementFormworkMappingMapper;
import com.coolcollege.intelligent.dao.achievement.AchievementTypeMapper;
import com.coolcollege.intelligent.model.achievement.entity.AchievementTypeDO;
import com.coolcollege.intelligent.model.achievement.request.AchievementRequest;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeReqVO;
import com.coolcollege.intelligent.model.achievement.vo.AchievementTypeResVO;
import com.coolcollege.intelligent.model.enums.AchievementErrorEnum;
import com.coolcollege.intelligent.model.enums.AchievementKeyPrefixEnum;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.achievement.AchievementTypeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 业绩类型服务
 * @Author: mao
 * @CreateDate: 2021/5/21 11:12
 */
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AchievementTypeServiceImpl implements AchievementTypeService {

    private final AchievementTypeMapper achievementTypeMapper;

    private final AchievementFormworkMappingMapper achievementFormworkMappingMapper;

    private static final Integer MAX_ACHIEVEMENT_TYPE_NAME=64;

    @Override
    public AchievementTypeResVO insertAchievementType(String enterpriseId, AchievementTypeReqVO req, CurrentUser user) {
        checkParam(enterpriseId, req, true);
        int count = achievementTypeMapper.countByName(enterpriseId,req.getName(),null);
        if (count > 0) {
            throw new ServiceException(ErrorCodeEnum.ACH_TYPE_NAME_REPEAT);
        }
        AchievementTypeDO achievementTypeDO = new AchievementTypeDO();
        achievementTypeDO.setId(req.getId());
        achievementTypeDO.setName(req.getName());
        achievementTypeDO.setCreateUserId(user.getUserId());
        achievementTypeDO.setCreateUserName(user.getName());
        achievementTypeDO.setUpdateUserId(user.getUserId());
        achievementTypeDO.setUpdateUserName(user.getName());
        achievementTypeMapper.insertAchievementType(enterpriseId, achievementTypeDO);
        AchievementTypeResVO typeResVO = new AchievementTypeResVO();
        setAchievementTypeResVO(achievementTypeDO, typeResVO);
        return typeResVO;
    }

    @Override
    public List<AchievementTypeDO> listAllTypes(String enterpriseId) {
        List<AchievementTypeDO> list = achievementTypeMapper.listAllTypes(enterpriseId);;
        return list;
    }

    @Override
    public Map<Long, String> getMapType(String enterpriseId) {
        List<AchievementTypeDO> list = listAllTypes(enterpriseId);
        return ListUtils.emptyIfNull(list)
                .stream()
                .collect(Collectors.toMap(AchievementTypeDO::getId, AchievementTypeDO::getName, (a, b) -> a));
    }

    @Override
    public PageInfo<AchievementTypeResVO> list(String eid, AchievementRequest request) {
        PageHelper.startPage(request.getPageNum(),request.getPageSize(),true);
        List<AchievementTypeDO> list = achievementTypeMapper.listNotDeletedTypes(eid);
        if(CollectionUtils.isEmpty(list)){
            return new PageInfo<>();
        }
        List<AchievementTypeResVO> achievementTypeResVOS = list.stream().map(e ->{
            AchievementTypeResVO vo = new AchievementTypeResVO();
            vo.setId(e.getId());
            vo.setName(e.getName());
            vo.setEditTime(e.getEditTime());
            vo.setUpdateUserName(e.getUpdateUserName());
            return vo;
        }).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(list);
        pageInfo.setList(achievementTypeResVOS);
        return pageInfo;
    }

    @Override
    public List<AchievementTypeResVO> listAchievementTypes(String enterpriseId) {
        List<AchievementTypeDO> list = achievementTypeMapper.listNotDeletedTypes(enterpriseId);
        return ListUtils.emptyIfNull(list).stream().map(t -> {
            AchievementTypeResVO typeResVO = new AchievementTypeResVO();
            setAchievementTypeResVO(t, typeResVO);
            return typeResVO;
        }).collect(Collectors.toList());
    }

    @Override
    public AchievementTypeResVO getLatEdit(String enterpriseId) {
        AchievementTypeResVO typeResVO = new AchievementTypeResVO();;
        AchievementTypeDO type = achievementTypeMapper.getLastEdit(enterpriseId);
        if (Objects.nonNull(type)) {
            setAchievementTypeResVO(type, typeResVO);
        }
        return typeResVO;
    }

    @Override
    public void deleteType(String enterpriseId, AchievementTypeReqVO reqVO) {
        achievementTypeMapper.deleteTypeById(enterpriseId, reqVO.getId());
        //删除关系
        achievementFormworkMappingMapper.updateByTypeId(enterpriseId,reqVO.getId(), AchievementStatusEnum.DELETE.getCode());
    }

    @Override
    public AchievementTypeResVO updateType(String enterpriseId, AchievementTypeReqVO req, CurrentUser user) {
        checkParam(enterpriseId, req, false);
        int count = achievementTypeMapper.countByName(enterpriseId,req.getName(), req.getId());
        if (count > 0) {
            throw new ServiceException(ErrorCodeEnum.ACH_TYPE_NAME_REPEAT);
        }
        AchievementTypeDO achievementTypeDO = new AchievementTypeDO();
        achievementTypeDO.setId(req.getId());
        achievementTypeDO.setName(req.getName());
        achievementTypeDO.setUpdateUserId(user.getUserId());
        achievementTypeDO.setUpdateUserName(user.getName());
        achievementTypeDO.setEditTime(new Date());
        achievementTypeMapper.updateType(enterpriseId, achievementTypeDO);
        AchievementTypeResVO typeResVO = new AchievementTypeResVO();
        setAchievementTypeResVO(achievementTypeDO, typeResVO);
        return typeResVO;
    }

    /**
     * 参数校验
     *
     * @param enterpriseId
     * @param req
     * @param add
     * @return boolean
     * @author mao
     * @date 2021/6/2 15:43
     */
    public boolean checkParam(String enterpriseId, AchievementTypeReqVO req, boolean add) {
        if (StringUtils.isEmpty(req.getName())) {
            throw new ServiceException(AchievementErrorEnum.TYPE_ADD_NAME.code,
                AchievementErrorEnum.TYPE_ADD_NAME.message);
        }
        if (req.getName().length()>MAX_ACHIEVEMENT_TYPE_NAME) {
            throw new ServiceException(ErrorCodeEnum.ACH_TYPE_NAME_LENGTH);
        }
        return true;
    }

    private AchievementTypeResVO setAchievementTypeResVO(AchievementTypeDO achievementTypeDO,
        AchievementTypeResVO typeResVO) {
        typeResVO.setId(achievementTypeDO.getId());
        typeResVO.setName(achievementTypeDO.getName());
        typeResVO.setEditTime(achievementTypeDO.getEditTime());
        typeResVO.setUpdateUserName(achievementTypeDO.getUpdateUserName());
        return typeResVO;
    }

}
