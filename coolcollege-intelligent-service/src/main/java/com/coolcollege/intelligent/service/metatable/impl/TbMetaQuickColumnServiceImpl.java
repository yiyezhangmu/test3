package com.coolcollege.intelligent.service.metatable.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnStatusEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnResultMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaQuickColumnReasonDao;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbMetaQuickColumnAppealDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.mapper.metatable.TbMetaColumnCategoryDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnResultDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.mapper.store.StoreSceneDAO;
import com.coolcollege.intelligent.model.ai.AIConfigDTO;
import com.coolcollege.intelligent.model.ai.entity.AiModelLibraryDO;
import com.coolcollege.intelligent.model.ai.vo.AIModelVO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.enums.ExportServiceEnum;
import com.coolcollege.intelligent.model.enums.FormPickerEnum;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnReasonDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnResultDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaQuickColumnResultDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnAppealDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.request.TbMetaQuickColumnExportRequest;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnResultVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnVO;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.patrolstore.vo.QuickTableColumnVO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.safetycheck.TbMetaQuickColumnAppealDO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.export.ExportUtil;
import com.coolcollege.intelligent.service.metatable.TbMetaQuickColumnService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import org.apache.commons.collections.ArrayStack;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.service.metatable.impl.TbMetaTableServiceImpl.fillExtendField;

/**
 * @author zhangchenbiao
 * @FileName: TbMetaQuickColumnServiceImpl
 * @Description: 快速检查项service
 * @date 2022-04-06 14:57
 */
@Service
public class TbMetaQuickColumnServiceImpl implements TbMetaQuickColumnService {

    @Resource
    private TbMetaQuickColumnDAO tbMetaQuickColumnDAO;

    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private TaskSopService taskSopService;

    @Resource
    private TbMetaQuickColumnResultDAO tbMetaQuickColumnResultDAO;

    @Resource
    private TbMetaQuickColumnReasonDao metaQuickColumnReasonDao;

    @Resource
    private TbMetaQuickColumnAppealDao metaQuickColumnAppealDao;

    @Autowired
    private StoreSceneMapper storeSceneMapper;

    @Resource
    private ExportUtil exportUtil;

    @Resource
    private TbMetaColumnCategoryDAO tbMetaColumnCategoryDAO;
    @Resource
    private StoreSceneDAO storeSceneDAO;
    @Resource
    private UserPersonInfoService userPersonInfoService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private TbMetaQuickColumnResultMapper tbMetaQuickColumnResultMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;
    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Resource
    private AiModelLibraryService aiModelLibraryService;

    @Override
    public Boolean updateStatus(String enterpriseId, Long id, MetaColumnStatusEnum statusEnum) {
        return tbMetaQuickColumnDAO.updateStatus(enterpriseId, id, statusEnum);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TbMetaQuickColumnVO createQuickTableColumn(String enterpriseId, String userId, QuickTableColumnRequest quickTableColumnRequest) {
        TbMetaQuickColumnDO entity = new TbMetaQuickColumnDO();
        MetaColumnTypeEnum columnType = MetaColumnTypeEnum.getColumnType(quickTableColumnRequest.getColumnType());
        if(Objects.isNull(columnType)){
            throw new ServiceException(ErrorCodeEnum.COLUMN_TYPE_ERROR);
        }
        if(!columnType.equals(MetaColumnTypeEnum.COLLECT_COLUMN) && CollectionUtils.isEmpty(quickTableColumnRequest.getColumnResultList())){
            throw new ServiceException(ErrorCodeEnum.COLUMN_RESULT_ISNULL);
        }
        //分类为空的时候归为其他的
        if(Objects.isNull(quickTableColumnRequest.getCategoryId())){
            Long otherCategoryId = tbMetaColumnCategoryDAO.getOtherCategoryId(enterpriseId);
            entity.setCategoryId(otherCategoryId);
            quickTableColumnRequest.setCategoryId(otherCategoryId);
        }
        //重名判断
        Integer sameNameCount = tbMetaQuickColumnDAO.getSameNameCount(enterpriseId, quickTableColumnRequest.getColumnName(), userId,  quickTableColumnRequest.getCategoryId(), columnType.getCode(), null);
        if(sameNameCount > Constants.ZERO){
            throw new ServiceException(ErrorCodeEnum.COLUMN_REPEAT);
        }
        //默认分类
        BigDecimal awardMoney = quickTableColumnRequest.getAwardMoney() == null? new BigDecimal(Constants.ZERO_STR) : quickTableColumnRequest.getAwardMoney();
        entity.setAwardMoney(awardMoney);
        entity.setColumnName(quickTableColumnRequest.getColumnName());
        entity.setDescription(quickTableColumnRequest.getDescription());
        BigDecimal punishMoney = quickTableColumnRequest.getPunishMoney() == null? new BigDecimal(Constants.ZERO_STR) : quickTableColumnRequest.getPunishMoney();
        entity.setPunishMoney(punishMoney);
        entity.setStandardPic(quickTableColumnRequest.getStandardPic());

        entity.setSopId(quickTableColumnRequest.getSopId() == null ? 0:quickTableColumnRequest.getSopId());
        if (quickTableColumnRequest.getCoolCourse() != null) {
            entity.setCoolCourse(JSON.toJSONString(quickTableColumnRequest.getCoolCourse()));
        }
        if (quickTableColumnRequest.getFreeCourse() != null) {
            entity.setFreeCourse(JSON.toJSONString(quickTableColumnRequest.getFreeCourse()));
        }
        entity.setCreateUser(userId);
        String questionHandlerName = null;
        if(FormPickerEnum.POSITION.getCode().equals(quickTableColumnRequest.getQuestionHandlerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionHandlerId())){
                List<Long> positionIdList = Arrays.asList(quickTableColumnRequest.getQuestionHandlerId().split(",")).stream().map(data -> Long.parseLong(data)).collect(Collectors.toList());
                List<SysRoleDO> positionDTOList = roleMapper.getRoleList(enterpriseId, positionIdList);
                if(positionDTOList != null){
                    questionHandlerName = positionDTOList.stream().map(data->data.getRoleName()).collect(Collectors.joining(","));
                }
                entity.setQuestionHandlerType(quickTableColumnRequest.getQuestionHandlerType());
                entity.setQuestionHandlerId(quickTableColumnRequest.getQuestionHandlerId());
            }
        }
        if(FormPickerEnum.PERSON.getCode().equals(quickTableColumnRequest.getQuestionHandlerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionHandlerId())){
                List<String> userIds = Arrays.asList(quickTableColumnRequest.getQuestionHandlerId().split(","));
                List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId,userIds);
                if(userList != null){
                    questionHandlerName = userList.stream().map(data -> data.getName()).collect(Collectors.joining(","));
                }
                entity.setQuestionHandlerType(quickTableColumnRequest.getQuestionHandlerType());
                entity.setQuestionHandlerId(quickTableColumnRequest.getQuestionHandlerId());
            }
        }
        entity.setQuestionHandlerName(questionHandlerName);

        String getQuestionRecheckerName = null;
        if(FormPickerEnum.POSITION.getCode().equals(quickTableColumnRequest.getQuestionRecheckerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionRecheckerId())){
                List<Long> positionIdList = Arrays.asList(quickTableColumnRequest.getQuestionRecheckerId().split(",")).stream().map(data -> Long.parseLong(data)).collect(Collectors.toList());
                List<SysRoleDO> positionDTOList = roleMapper.getRoleList(enterpriseId, positionIdList);
                if(positionDTOList != null){
                    getQuestionRecheckerName = positionDTOList.stream().map(data->data.getRoleName()).collect(Collectors.joining(","));
                }
                entity.setQuestionRecheckerId(quickTableColumnRequest.getQuestionRecheckerId());
                entity.setQuestionRecheckerType(quickTableColumnRequest.getQuestionRecheckerType());
            }
        }
        if(FormPickerEnum.PERSON.getCode().equals(quickTableColumnRequest.getQuestionRecheckerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionRecheckerId())){
                List<String> userIds = Arrays.asList(quickTableColumnRequest.getQuestionRecheckerId().split(","));
                List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId,userIds);
                if(userList != null){
                    getQuestionRecheckerName = userList.stream().map(EnterpriseUserDO::getName).collect(Collectors.joining(","));
                }
                entity.setQuestionRecheckerId(quickTableColumnRequest.getQuestionRecheckerId());
                entity.setQuestionRecheckerType(quickTableColumnRequest.getQuestionRecheckerType());
            }
        }
        entity.setCreateUserApprove(quickTableColumnRequest.getCreateUserApprove());
        entity.setQuestionCcId(quickTableColumnRequest.getQuestionCcId() == null ? "" : quickTableColumnRequest.getQuestionCcId());
        entity.setQuestionCcType(quickTableColumnRequest.getQuestionCcType() == null ? "" : quickTableColumnRequest.getQuestionCcType());
        entity.setQuestionCcName("");
        entity.setQuestionRecheckerName(getQuestionRecheckerName);
        String userName = enterpriseUserDao.selectNameByUserId(enterpriseId,userId);
        entity.setCreateUserName(userName);
        entity.setEditUserId(userId);
        entity.setEditUserName(userName);
        entity.setStoreSceneId(quickTableColumnRequest.getStoreSceneId());
        entity.setQuestionApproveUser(quickTableColumnRequest.getQuestionApproveUser());
        entity.setCategoryId(quickTableColumnRequest.getCategoryId());
        entity.setUserDefinedScore(quickTableColumnRequest.getUserDefinedScore());
        entity.setConfigType(quickTableColumnRequest.getConfigType());
        entity.setMinScore(quickTableColumnRequest.getMinScore());
        entity.setMaxScore(quickTableColumnRequest.getMaxScore());
        entity.setColumnType(quickTableColumnRequest.getColumnType());
        entity.setCreateTime(new Date());
        entity.setUsePersonInfo(quickTableColumnRequest.getUsePersonInfo());
        entity.setUseRange(quickTableColumnRequest.getUseRange());
        entity.setUseUserids(userPersonInfoService.getUserIds(enterpriseId, quickTableColumnRequest.getUsePersonInfo(),
                quickTableColumnRequest.getUseRange(), userId));
        entity.setIsAiCheck(quickTableColumnRequest.getIsAiCheck());
        entity.setAiCheckStdDesc(quickTableColumnRequest.getAiCheckStdDesc());
        if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getCommonEditUserIdList())){
            entity.setCommonEditUserids(Constants.COMMA + StringUtils.join(quickTableColumnRequest.getCommonEditUserIdList(), Constants.COMMA)
            + Constants.COMMA);
        }
        //判断是否采集项,判断采集项是否需要拍照
        if(MetaColumnTypeEnum.COLLECT_COLUMN.equals(columnType)){
            entity.setMustPic(quickTableColumnRequest.getMustPic()==null?Constants.ZERO:quickTableColumnRequest.getMustPic());
        }
        // 扩展信息
        entity.setExtendInfo(fillExtendField(quickTableColumnRequest));

        tbMetaQuickColumnMapper.insertSelective(enterpriseId,entity);
        if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionCcId())){
            JSONArray jsonArray = JSONUtil.parseArray(quickTableColumnRequest.getQuestionCcId());
            List<PersonPositionDTO> ccIdList = JSONUtil.toList(jsonArray, PersonPositionDTO.class);
            entity.setCcPeopleList(ccIdList);
        }
        TbMetaQuickColumnVO vo = new TbMetaQuickColumnVO();
        BeanUtils.copyProperties(entity, vo);
        if(entity.getSopId() != null){
            TaskSopVO taskSopVO = taskSopService.getSopById(enterpriseId, entity.getSopId());
            vo.setTaskSopVO(taskSopVO);
        }
        if (StringUtils.isNotBlank(entity.getCoolCourse())) {
            CoolCourseVO coolCourseVO =JSON.parseObject(entity.getCoolCourse(), CoolCourseVO.class);
            vo.setCoolCourseVO(coolCourseVO);
        }
        if (StringUtils.isNotBlank(entity.getFreeCourse())) {
            CoolCourseVO freeCourseVO =JSON.parseObject(entity.getFreeCourse(), CoolCourseVO.class);
            vo.setFreeCourseVO(freeCourseVO);
        }
        if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getColumnResultList())){
            List<TbMetaQuickColumnResultDO> columnResultList = new ArrayStack();
            for (TbMetaQuickColumnResultDTO tbMetaColumnResult : quickTableColumnRequest.getColumnResultList()) {
                columnResultList.add(TbMetaQuickColumnResultDO.builder().metaQuickColumnId(entity.getId()).resultName(tbMetaColumnResult.getResultName())
                        .maxScore(tbMetaColumnResult.getMaxScore()).minScore(tbMetaColumnResult.getMinScore()).score(tbMetaColumnResult.getScore()).defaultMoney(tbMetaColumnResult.getMoney()).mappingResult(tbMetaColumnResult.getMappingResult())
                        .mustPic(tbMetaColumnResult.getMustPic()).orderNum(tbMetaColumnResult.getOrderNum()).description(tbMetaColumnResult.getDescription()).scoreIsDouble(tbMetaColumnResult.getScoreIsDouble())
                        .awardIsDouble(tbMetaColumnResult.getAwardIsDouble()).createUserId(userId).createTime(new Date()).extendInfo(tbMetaColumnResult.convertToExtendInfo()).build());
            }
            tbMetaQuickColumnResultDAO.addQuickColumnResult(enterpriseId, columnResultList);
        }
        if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getColumnReasonList())){
            List<TbMetaQuickColumnReasonDO> reasonDOList = new ArrayList<>();
            quickTableColumnRequest.getColumnReasonList().forEach(columnResult -> {
                TbMetaQuickColumnReasonDO reasonDO = new TbMetaQuickColumnReasonDO();
                reasonDO.setQuickColumnId(entity.getId());
                reasonDO.setReasonName(columnResult.getReasonName());
                reasonDO.setCreateUserId(userId);
                reasonDO.setMappingResult(columnResult.getMappingResult());
                reasonDOList.add(reasonDO);
            });
            if(CollectionUtils.isNotEmpty(reasonDOList)){
                metaQuickColumnReasonDao.batchInsert(enterpriseId, reasonDOList);
            }
        }

        if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getColumnAppealList())){
            List<TbMetaQuickColumnAppealDO> appealDOList = new ArrayList<>();
            quickTableColumnRequest.getColumnAppealList().forEach(appealRequest -> {
                TbMetaQuickColumnAppealDO appealDO = new TbMetaQuickColumnAppealDO();
                appealDO.setMetaQuickColumnId(entity.getId());
                appealDO.setAppealName(appealRequest.getAppealName());
                appealDO.setCreateUserId(userId);
                appealDOList.add(appealDO);
            });
            if(CollectionUtils.isNotEmpty(appealDOList)){
                metaQuickColumnAppealDao.batchInsert(enterpriseId, appealDOList);
            }
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateQuickTableColumn(String enterpriseId, String userId, QuickTableColumnRequest quickTableColumnRequest) {
        MetaColumnTypeEnum columnType = MetaColumnTypeEnum.getColumnType(quickTableColumnRequest.getColumnType());
        if(Objects.isNull(columnType)){
            throw new ServiceException(ErrorCodeEnum.COLUMN_TYPE_ERROR);
        }
        if(!columnType.equals(MetaColumnTypeEnum.COLLECT_COLUMN) && CollectionUtils.isEmpty(quickTableColumnRequest.getColumnResultList())){
            throw new ServiceException(ErrorCodeEnum.COLUMN_RESULT_ISNULL);
        }
        //重名判断
        Integer sameNameCount = tbMetaQuickColumnDAO.getSameNameCount(enterpriseId, quickTableColumnRequest.getColumnName(), userId, quickTableColumnRequest.getCategoryId(), columnType.getCode(), quickTableColumnRequest.getId());
        if(sameNameCount > Constants.ZERO){
            throw new ServiceException(ErrorCodeEnum.COLUMN_REPEAT);
        }
        TbMetaQuickColumnDO entity = new TbMetaQuickColumnDO();
        entity.setId(quickTableColumnRequest.getId());
        entity.setColumnName(quickTableColumnRequest.getColumnName());
        entity.setDescription(quickTableColumnRequest.getDescription());
        entity.setStandardPic(quickTableColumnRequest.getStandardPic());
        entity.setSopId(quickTableColumnRequest.getSopId()==null? 0 : quickTableColumnRequest.getSopId());
        entity.setCategoryId(quickTableColumnRequest.getCategoryId());
        entity.setUserDefinedScore(quickTableColumnRequest.getUserDefinedScore());
        entity.setConfigType(quickTableColumnRequest.getConfigType());
        entity.setMinScore(quickTableColumnRequest.getMinScore());
        entity.setMaxScore(quickTableColumnRequest.getMaxScore());
        entity.setColumnType(columnType.getCode());
        if(MetaColumnTypeEnum.COLLECT_COLUMN.equals(columnType)){
            entity.setMustPic(quickTableColumnRequest.getMustPic());
        }
        //设置酷学院课程以及免费课程
        if (quickTableColumnRequest.getCoolCourse() != null) {
            entity.setCoolCourse(JSON.toJSONString(quickTableColumnRequest.getCoolCourse()));
        }
        if (quickTableColumnRequest.getFreeCourse() != null) {
            entity.setFreeCourse(JSON.toJSONString(quickTableColumnRequest.getFreeCourse()));
        }
        entity.setAwardMoney(quickTableColumnRequest.getAwardMoney());
        entity.setPunishMoney(quickTableColumnRequest.getPunishMoney());
        String questionHandlerName = null;
        if(FormPickerEnum.POSITION.getCode().equals(quickTableColumnRequest.getQuestionHandlerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionHandlerId())){
                List<Long> positionIdList = Arrays.asList(quickTableColumnRequest.getQuestionHandlerId().split(",")).stream().map(data -> Long.parseLong(data)).collect(Collectors.toList());
                List<SysRoleDO> positionDTOList = roleMapper.getRoleList(enterpriseId, positionIdList);
                if(positionDTOList != null){
                    questionHandlerName = positionDTOList.stream().map(data->data.getRoleName()).collect(Collectors.joining(","));
                }
                entity.setQuestionHandlerType(quickTableColumnRequest.getQuestionHandlerType());
                entity.setQuestionHandlerId(quickTableColumnRequest.getQuestionHandlerId());
            }
        }
        if(FormPickerEnum.PERSON.getCode().equals(quickTableColumnRequest.getQuestionHandlerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionHandlerId())){
                List<String> userIds = Arrays.asList(quickTableColumnRequest.getQuestionHandlerId().split(","));
                List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId,userIds);
                if(userList != null){
                    questionHandlerName = userList.stream().map(data -> data.getName()).collect(Collectors.joining(","));
                }
                entity.setQuestionHandlerType(quickTableColumnRequest.getQuestionHandlerType());
                entity.setQuestionHandlerId(quickTableColumnRequest.getQuestionHandlerId());
            }
        }
        if (StringUtils.isBlank(quickTableColumnRequest.getQuestionHandlerType())){
            entity.setQuestionHandlerType("");
            entity.setQuestionHandlerId("");
        }
        entity.setQuestionHandlerName(questionHandlerName);

        String getQuestionRecheckerName = null;
        if(FormPickerEnum.POSITION.getCode().equals(quickTableColumnRequest.getQuestionRecheckerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionRecheckerId())){
                List<Long> positionIdList = Arrays.asList(quickTableColumnRequest.getQuestionRecheckerId().split(",")).stream().map(data -> Long.parseLong(data)).collect(Collectors.toList());
                List<SysRoleDO> positionDTOList = roleMapper.getRoleList(enterpriseId, positionIdList);
                if(positionDTOList != null){
                    getQuestionRecheckerName = positionDTOList.stream().map(data->data.getRoleName()).collect(Collectors.joining(","));
                }
                entity.setQuestionRecheckerId(quickTableColumnRequest.getQuestionRecheckerId());
                entity.setQuestionRecheckerType(quickTableColumnRequest.getQuestionRecheckerType());
            }
        }
        if(FormPickerEnum.PERSON.getCode().equals(quickTableColumnRequest.getQuestionRecheckerType())){
            if(StringUtils.isNotBlank(quickTableColumnRequest.getQuestionRecheckerId())){
                List<String> userIds = Arrays.asList(quickTableColumnRequest.getQuestionRecheckerId().split(","));
                List<EnterpriseUserDO> userList = enterpriseUserDao.selectByUserIds(enterpriseId,userIds);
                if(userList != null){
                    getQuestionRecheckerName = userList.stream().map(EnterpriseUserDO::getName).collect(Collectors.joining(","));
                }
                entity.setQuestionRecheckerId(quickTableColumnRequest.getQuestionRecheckerId());
                entity.setQuestionRecheckerType(quickTableColumnRequest.getQuestionRecheckerType());
            }
        }
        if (StringUtils.isEmpty(quickTableColumnRequest.getQuestionRecheckerType())){
            entity.setQuestionRecheckerId("");
            entity.setQuestionRecheckerType("");
            getQuestionRecheckerName="";
        }
        entity.setCreateUserApprove(quickTableColumnRequest.getCreateUserApprove());
        entity.setQuestionRecheckerName(getQuestionRecheckerName);
        //设置抄送人姓名
        entity.setQuestionCcId(quickTableColumnRequest.getQuestionCcId() == null ? "" : quickTableColumnRequest.getQuestionCcId());
        entity.setQuestionCcType(quickTableColumnRequest.getQuestionCcType() == null ? "" : quickTableColumnRequest.getQuestionCcType());
        entity.setQuestionCcName("");
        String userName = enterpriseUserDao.selectNameByUserId(enterpriseId,userId);
        entity.setEditUserId(userId);
        entity.setEditUserName(userName);
        entity.setStoreSceneId(quickTableColumnRequest.getStoreSceneId());
        entity.setQuestionApproveUser(quickTableColumnRequest.getQuestionApproveUser());
        //分类为空的时候归为其他的
        if(Objects.isNull(quickTableColumnRequest.getCategoryId())){
            entity.setCategoryId(tbMetaColumnCategoryDAO.getOtherCategoryId(enterpriseId));
        }
        entity.setUsePersonInfo(quickTableColumnRequest.getUsePersonInfo());
        entity.setUseRange(quickTableColumnRequest.getUseRange());
        entity.setIsAiCheck(quickTableColumnRequest.getIsAiCheck());
        entity.setAiCheckStdDesc(quickTableColumnRequest.getAiCheckStdDesc());
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        TbMetaQuickColumnDO oldQuickColumnDO = tbMetaQuickColumnMapper.selectByPrimaryKey(enterpriseId, entity.getId());
        //只有管理员和创建人可以修改使用人和共同编辑人信息
        if(isAdmin || userId.equals(oldQuickColumnDO.getCreateUser())){
            entity.setUseUserids(userPersonInfoService.getUserIds(enterpriseId, quickTableColumnRequest.getUsePersonInfo(),
                    quickTableColumnRequest.getUseRange(), userId));
            if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getCommonEditUserIdList())){
                entity.setCommonEditUserids(Constants.COMMA + StringUtils.join(quickTableColumnRequest.getCommonEditUserIdList(), Constants.COMMA)
                        + Constants.COMMA);
            }
        }
        // 扩展信息
        entity.setExtendInfo(fillExtendField(quickTableColumnRequest));

        tbMetaQuickColumnMapper.updateByPrimaryKeySelective(enterpriseId,entity);
        //修改结果项
        List<TbMetaQuickColumnResultDO> columnResultList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getColumnResultList())){
            for (TbMetaQuickColumnResultDTO tbMetaColumnResult : quickTableColumnRequest.getColumnResultList()) {
                columnResultList.add(TbMetaQuickColumnResultDO.builder().id(tbMetaColumnResult.getId()).metaQuickColumnId(entity.getId()).resultName(tbMetaColumnResult.getResultName())
                        .maxScore(tbMetaColumnResult.getMaxScore()).minScore(tbMetaColumnResult.getMinScore()).score(tbMetaColumnResult.getScore()).defaultMoney(tbMetaColumnResult.getMoney()).mappingResult(tbMetaColumnResult.getMappingResult())
                        .mustPic(tbMetaColumnResult.getMustPic()).orderNum(tbMetaColumnResult.getOrderNum()).description(tbMetaColumnResult.getDescription()).scoreIsDouble(tbMetaColumnResult.getScoreIsDouble())
                        .awardIsDouble(tbMetaColumnResult.getAwardIsDouble()).createUserId(userId).createTime(new Date()).updateTime(new Date()).updateUserId(userId).extendInfo(tbMetaColumnResult.convertToExtendInfo()).build());
            }
        }
        tbMetaQuickColumnResultDAO.updateQuickColumnResult(enterpriseId, quickTableColumnRequest.getId(), columnResultList);
        //修改不合格原因
        this.updateQuickColumnReason(enterpriseId, quickTableColumnRequest.getId(), quickTableColumnRequest, userId);
        //申诉快捷項
        this.updateQuickColumnAppeal(enterpriseId, quickTableColumnRequest.getId(), quickTableColumnRequest, userId);
        return Boolean.TRUE;
    }

    @Override
    public QuickTableColumnVO getQuickTableColumnDetail(String enterpriseId, Long id, String userId) {
        TbMetaQuickColumnDO patrolMetaQuickColumnDO = tbMetaQuickColumnMapper.selectByPrimaryKey(enterpriseId, id);
        if(patrolMetaQuickColumnDO != null){
            QuickTableColumnVO quickTableColumnVO = new QuickTableColumnVO();
            quickTableColumnVO.setCreateUser(patrolMetaQuickColumnDO.getCreateUser());
            quickTableColumnVO.setAwardMoney(patrolMetaQuickColumnDO.getAwardMoney());
            quickTableColumnVO.setColumnName(patrolMetaQuickColumnDO.getColumnName());
            quickTableColumnVO.setCreateTime(patrolMetaQuickColumnDO.getCreateTime());
            quickTableColumnVO.setEditTime(patrolMetaQuickColumnDO.getEditTime());
            quickTableColumnVO.setDescription(patrolMetaQuickColumnDO.getDescription());
            quickTableColumnVO.setPunishMoney(patrolMetaQuickColumnDO.getPunishMoney());
            quickTableColumnVO.setId(patrolMetaQuickColumnDO.getId());
            quickTableColumnVO.setStandardPic(patrolMetaQuickColumnDO.getStandardPic());
            quickTableColumnVO.setCreateUserName(patrolMetaQuickColumnDO.getCreateUserName());
            quickTableColumnVO.setQuestionHandlerId(patrolMetaQuickColumnDO.getQuestionHandlerId());
            quickTableColumnVO.setQuestionHandlerType(patrolMetaQuickColumnDO.getQuestionHandlerType());
            quickTableColumnVO.setQuestionRecheckerId(patrolMetaQuickColumnDO.getQuestionRecheckerId());
            quickTableColumnVO.setQuestionRecheckerType(patrolMetaQuickColumnDO.getQuestionRecheckerType());
            quickTableColumnVO.setCreateUserApprove(patrolMetaQuickColumnDO.getCreateUserApprove());
            quickTableColumnVO.setQuestionCcId(patrolMetaQuickColumnDO.getQuestionCcId());
            quickTableColumnVO.setQuestionCcType(patrolMetaQuickColumnDO.getQuestionCcType());
            quickTableColumnVO.setStoreSceneId(patrolMetaQuickColumnDO.getStoreSceneId());
            quickTableColumnVO.setCategoryId(patrolMetaQuickColumnDO.getCategoryId());
            quickTableColumnVO.setUserDefinedScore(patrolMetaQuickColumnDO.getUserDefinedScore());
            quickTableColumnVO.setConfigType(patrolMetaQuickColumnDO.getConfigType());
            quickTableColumnVO.setMinScore(patrolMetaQuickColumnDO.getMinScore());
            quickTableColumnVO.setMaxScore(patrolMetaQuickColumnDO.getMaxScore());
            quickTableColumnVO.setColumnType(patrolMetaQuickColumnDO.getColumnType());
            quickTableColumnVO.setStoreSceneIsDelete(true);
            quickTableColumnVO.setMustPic(patrolMetaQuickColumnDO.getMustPic());
            quickTableColumnVO.setIsAiCheck(patrolMetaQuickColumnDO.getIsAiCheck());
            quickTableColumnVO.setAiCheckStdDesc(patrolMetaQuickColumnDO.getAiCheckStdDesc());
            if (patrolMetaQuickColumnDO.getStoreSceneId()!=null){
                StoreSceneDo storeSceneById = storeSceneMapper.getStoreSceneById(enterpriseId, patrolMetaQuickColumnDO.getStoreSceneId());
                if (storeSceneById!=null){
                    quickTableColumnVO.setStoreSceneIsDelete(false);
                }
            }
            if(StringUtils.isNotBlank(patrolMetaQuickColumnDO.getQuestionCcId())){
                JSONArray jsonArray = JSONUtil.parseArray(patrolMetaQuickColumnDO.getQuestionCcId());
                List<PersonPositionDTO> ccIdList = JSONUtil.toList(jsonArray, PersonPositionDTO.class);
                //设置抄送人列表
                quickTableColumnVO.setCcPeopleList(ccIdList);
            }
            quickTableColumnVO.setQuestionApproveUser(patrolMetaQuickColumnDO.getQuestionApproveUser());
            quickTableColumnVO.setQuestionCcName(patrolMetaQuickColumnDO.getQuestionCcName());
            quickTableColumnVO.setUpdateUserName(patrolMetaQuickColumnDO.getEditUserName());
            String questionHandlerName =patrolMetaQuickColumnDO.getQuestionHandlerName();
            quickTableColumnVO.setQuestionHandlerName(questionHandlerName);
            String recheckerName =patrolMetaQuickColumnDO.getQuestionRecheckerName();
            quickTableColumnVO.setQuestionRecheckerName(recheckerName);
            if(null != patrolMetaQuickColumnDO.getSopId()){
                // 快捷配置项详情返回sop文档id
                TaskSopVO taskSopVO = taskSopService.getSopById(enterpriseId, patrolMetaQuickColumnDO.getSopId());
                quickTableColumnVO.setTaskSopVO(taskSopVO);
            }
            if (StringUtils.isNotBlank(patrolMetaQuickColumnDO.getCoolCourse())) {
                quickTableColumnVO.setCoolCourse(JSON.parseObject(patrolMetaQuickColumnDO.getCoolCourse(), CoolCourseVO.class));
            }
            if (StringUtils.isNotBlank(patrolMetaQuickColumnDO.getFreeCourse())) {
                quickTableColumnVO.setFreeCourse(JSON.parseObject(patrolMetaQuickColumnDO.getFreeCourse(), CoolCourseVO.class));
            }
            //结果项
            List<TbMetaQuickColumnResultVO> list = tbMetaQuickColumnResultDAO.getColumnResultList(enterpriseId, id);
            quickTableColumnVO.setColumnResultList(list);
            //不合格原因
            List<TbQuickColumnReasonDTO> tbMetaColumnReasonDOList = metaQuickColumnReasonDao.getListByColumnId(enterpriseId, id);
            quickTableColumnVO.setColumnReasonList(tbMetaColumnReasonDOList);
            quickTableColumnVO.setCategoryName(tbMetaColumnCategoryDAO.getCategoryName(enterpriseId, patrolMetaQuickColumnDO.getCategoryId()));
            quickTableColumnVO.setStoreSceneName(storeSceneDAO.getStoreSceneName(enterpriseId, patrolMetaQuickColumnDO.getStoreSceneId()));
            quickTableColumnVO.setUsePersonInfo(patrolMetaQuickColumnDO.getUsePersonInfo());
            quickTableColumnVO.setUseRange(patrolMetaQuickColumnDO.getUseRange());
            quickTableColumnVO.setCommonEditUserids(patrolMetaQuickColumnDO.getCommonEditUserids());
            quickTableColumnVO.setEditFlag(false);
            if(userId.equals(patrolMetaQuickColumnDO.getCreateUser())){
                quickTableColumnVO.setEditFlag(true);
            }
            boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
            if(isAdmin){
                quickTableColumnVO.setEditFlag(true);
            }
            //申诉原因
            List<TbQuickColumnAppealDTO> quickColumnAppealDTOList = metaQuickColumnAppealDao.selectListByColumnId(enterpriseId, id);
            quickTableColumnVO.setColumnAppealList(quickColumnAppealDTOList);
            //共同编辑人列表
            List<PersonDTO> personDTOList = new ArrayList<>();
            if(StringUtils.isNotBlank(patrolMetaQuickColumnDO.getCommonEditUserids())){
                List<String> commonEditUserIdList = StrUtil.splitTrim(patrolMetaQuickColumnDO.getCommonEditUserids(), ",");
                List<String> queryUserIds = new ArrayList<>(commonEditUserIdList);
                if(CollectionUtils.isNotEmpty(queryUserIds) && queryUserIds.size() > Constants.ONE_HUNDRED){
                    queryUserIds = queryUserIds.subList(0, Constants.ONE_HUNDRED);
                }
                Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, queryUserIds);
                commonEditUserIdList.forEach(editUserId -> {
                    PersonDTO personDTO = new PersonDTO();
                    personDTO.setUserId(editUserId);
                    personDTO.setUserName(userMap.get(editUserId));
                    personDTOList.add(personDTO);
                });
                if(commonEditUserIdList.contains(userId)){
                    quickTableColumnVO.setEditFlag(true);
                }
            }
            quickTableColumnVO.setCommonEditUserList(personDTOList);
            JSONObject extendInfo = JSONObject.parseObject(patrolMetaQuickColumnDO.getExtendInfo());
            if (Objects.nonNull(extendInfo)) {
                quickTableColumnVO.setDescRequired(extendInfo.getBoolean(Constants.TableColumn.DESC_REQUIRED));
                quickTableColumnVO.setAutoQuestionTaskValidity(extendInfo.getInteger(Constants.TableColumn.AUTO_QUESTION_TASK_VALIDITY));
                quickTableColumnVO.setIsSetAutoQuestionTaskValidity(extendInfo.getBoolean(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY));
                quickTableColumnVO.setMinCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MIN_CHECK_PIC_NUM));
                quickTableColumnVO.setMaxCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MAX_CHECK_PIC_NUM));

            }
            quickTableColumnVO.setIsAiCheck(patrolMetaQuickColumnDO.getIsAiCheck());
            quickTableColumnVO.setAiModel(patrolMetaQuickColumnDO.getAiModel());
            AiModelLibraryDO aiModelLibraryDO = aiModelLibraryService.getModelByCode(patrolMetaQuickColumnDO.getAiModel());
            quickTableColumnVO.setAiModelName(Objects.nonNull(aiModelLibraryDO) ? aiModelLibraryDO.getName() : null);
            return quickTableColumnVO;
        }
        return null;
    }

    @Override
    public Long copyQuickTableColumn(String enterpriseId, Long id, CurrentUser user) {
        TbMetaQuickColumnDO quickColumnDO = tbMetaQuickColumnMapper.selectByPrimaryKey(enterpriseId, id);
        if(quickColumnDO == null){
            throw new ServiceException(ErrorCodeEnum.META_COLUMN_NOT_EXIST);
        }
        quickColumnDO.setId(null);
        quickColumnDO.setCreateTime(new Date());
        quickColumnDO.setEditTime(new Date());
        quickColumnDO.setCreateUser(user.getUserId());
        quickColumnDO.setCreateUserName(user.getName());
        quickColumnDO.setEditUserId(user.getUserId());
        quickColumnDO.setEditUserName(user.getName());
        quickColumnDO.setColumnName(quickColumnDO.getColumnName() +  UUIDUtils.get8UUID());
        tbMetaQuickColumnMapper.insertSelective(enterpriseId, quickColumnDO);
        List<TbMetaQuickColumnResultDO> columnResultList = tbMetaQuickColumnResultDAO.getColumnResultListById(enterpriseId, id);
        if(CollectionUtils.isNotEmpty(columnResultList)) {
            for(TbMetaQuickColumnResultDO resultDO : columnResultList){
                resultDO.setMetaQuickColumnId(quickColumnDO.getId());
                resultDO.setCreateUserId(user.getUserId());
            }
            tbMetaQuickColumnResultDAO.addQuickColumnResult(enterpriseId, columnResultList);
        }
        List<TbQuickColumnReasonDTO> tbMetaColumnReasonDOList = metaQuickColumnReasonDao.getListByColumnId(enterpriseId, id);
        if (CollectionUtils.isNotEmpty(tbMetaColumnReasonDOList)) {
            List<TbMetaQuickColumnReasonDO> reasonDOList = new ArrayList<>();
            tbMetaColumnReasonDOList.forEach(columnResult -> {
                TbMetaQuickColumnReasonDO reasonDO = new TbMetaQuickColumnReasonDO();
                reasonDO.setQuickColumnId(quickColumnDO.getId());
                reasonDO.setReasonName(columnResult.getReasonName());
                reasonDO.setCreateUserId(user.getUserId());
                reasonDO.setMappingResult(columnResult.getMappingResult());
                reasonDOList.add(reasonDO);
            });
            metaQuickColumnReasonDao.batchInsert(enterpriseId, reasonDOList);
        }

        List<TbQuickColumnAppealDTO> appealDTOList = metaQuickColumnAppealDao.selectListByColumnId(enterpriseId, id);
        if (CollectionUtils.isNotEmpty(appealDTOList)) {
            List<TbMetaQuickColumnAppealDO> appealDOList = new ArrayList<>();
            appealDTOList.forEach(appealDTO -> {
                TbMetaQuickColumnAppealDO appealDO = new TbMetaQuickColumnAppealDO();
                appealDO.setMetaQuickColumnId(quickColumnDO.getId());
                appealDO.setAppealName(appealDTO.getAppealName());
                appealDO.setCreateUserId(user.getUserId());
                appealDOList.add(appealDO);
            });
            metaQuickColumnAppealDao.batchInsert(enterpriseId, appealDOList);
        }
        return quickColumnDO.getId();
    }

    @Override
    public ImportTaskDO exportQuickColumn(String eid, TbMetaQuickColumnExportRequest request, CurrentUser user) {
        request.setExportServiceEnum(ExportServiceEnum.EXPORT_QUICK_COLUMN_NEW);
        return exportUtil.exportFile(eid, request, UserHolder.getUser().getDbName());
    }

    @Override
    public Boolean batchUpdateStatus(String enterpriseId, List<Long> ids, MetaColumnStatusEnum statusEnum) {
        return tbMetaQuickColumnDAO.batchUpdateStatus(enterpriseId, ids, statusEnum);
    }

    @Override
    public Boolean configQuickColumnAuth(String enterpriseId, QuickTableColumnRequest quickTableColumnRequest, CurrentUser user) {
        TbMetaQuickColumnDO entity = new TbMetaQuickColumnDO();
        entity.setUsePersonInfo(quickTableColumnRequest.getUsePersonInfo());
        entity.setUseRange(quickTableColumnRequest.getUseRange());
        entity.setUseUserids(userPersonInfoService.getUserIds(enterpriseId, quickTableColumnRequest.getUsePersonInfo(),
                quickTableColumnRequest.getUseRange(), user.getUserId()));
        if(CollectionUtils.isNotEmpty(quickTableColumnRequest.getCommonEditUserIdList())){
            entity.setCommonEditUserids(Constants.COMMA + StringUtils.join(quickTableColumnRequest.getCommonEditUserIdList(), Constants.COMMA)
                    + Constants.COMMA);
        }
        entity.setEditUserId(user.getUserId());
        entity.setEditUserName(user.getName());
        return tbMetaQuickColumnDAO.batchUpdateQuickColumnAUth(enterpriseId,quickTableColumnRequest.getColumnIdList(),entity);
    }

    @Override
    public void updateQuickColumnUseUser(String enterpriseId) {
        boolean isContinue = true;
        int pageNum = 0, pageSize = 50;
        while (isContinue){
            PageHelper.startPage(pageNum++, pageSize, false);
            List<TbMetaQuickColumnDO> quickColumnList = tbMetaQuickColumnDAO.getQuickColumnList(enterpriseId);
            if(CollectionUtils.isEmpty(quickColumnList)){
                break;
            }
            if(quickColumnList.size() < pageSize){
                isContinue = false;
            }

            List<String> createUserIds = ListUtils.emptyIfNull(quickColumnList).stream().map(TbMetaQuickColumnDO::getCreateUser).collect(Collectors.toList());
            List<EnterpriseUserDO> createUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, createUserIds);
            Map<String, EnterpriseUserDO> createUserMap = ListUtils.emptyIfNull(createUserDOList).stream().collect(Collectors.toMap(EnterpriseUserDO::getUserId, Function.identity()));
            for (TbMetaQuickColumnDO tbMetaQuickColumnDO : quickColumnList) {
                EnterpriseUserDO enterpriseUserDO = createUserMap.get(tbMetaQuickColumnDO.getCreateUser());
                if(enterpriseUserDO != null && enterpriseUserDO.getActive()){
                    List<String> useUserIds = userPersonInfoService.getUserIdList(enterpriseId, tbMetaQuickColumnDO.getUsePersonInfo(), tbMetaQuickColumnDO.getUseRange(), tbMetaQuickColumnDO.getCreateUser());
                    if(CollectionUtils.isNotEmpty(useUserIds)){
                        tbMetaQuickColumnDO.setUseUserids(Constants.COMMA + StringUtils.join(useUserIds, Constants.COMMA) + Constants.COMMA);
                    }
                }
            }
            tbMetaQuickColumnDAO.batchUpdateUseUserIds(enterpriseId, quickColumnList);
        }
    }

    @Override
    public List<AIModelVO> getEnterpriseAIModelList(String enterpriseId) {
        DataSourceHelper.reset();
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        AIConfigDTO aiConfigDTO = JSONObject.parseObject(enterpriseSettingDO.getExtendField(), AIConfigDTO.class);
        if (Objects.nonNull(aiConfigDTO) && CollectionUtils.isNotEmpty(aiConfigDTO.getEnableAiModel())) {
            Set<String> aiModelCodes = CollStreamUtil.toSet(aiConfigDTO.getEnableAiModel(), AIConfigDTO.EnableAIModel::getAiModel);
            Map<String, AiModelLibraryDO> aiModelMap = aiModelLibraryService.getModelMapByCodes(new ArrayList<>(aiModelCodes));
            return aiConfigDTO.getEnableAiModel().stream()
                    .filter(v -> Boolean.TRUE.equals(v.getEnable()))
                    .map(v -> {
                        AiModelLibraryDO aiModel = aiModelMap.get(v.getAiModel());
                        return Objects.nonNull(aiModel) ? AIModelVO.convert(aiModel) : null;
                    })
                    .filter(Objects::nonNull)
                    .sorted((o1, o2) -> {
                        AiModelLibraryDO m1 = aiModelMap.get(o1.getCode());
                        AiModelLibraryDO m2 = aiModelMap.get(o2.getCode());

                        // 先按平台排序
                        int platformCompare = m1.getPlatformCode().compareTo(m2.getPlatformCode());
                        if (platformCompare != 0) {
                            return platformCompare;
                        }
                        // platformCode 相同时，按name字典序升序
                        return m1.getName().compareTo(m2.getName());
                    })
                    .sorted(Comparator.comparing(o -> aiModelMap.get(o.getCode()).getPlatformCode()))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    /**
     * 修改结果项以及原因
     * @param enterpriseId
     * @param metaQuickColumnId
     * @param quickTableColumnRequest
     * @param userId
     */
    private void updateQuickColumnReason(String enterpriseId, Long metaQuickColumnId, QuickTableColumnRequest quickTableColumnRequest, String userId) {
        if (CollectionUtils.isEmpty(quickTableColumnRequest.getColumnReasonList())) {
            //删除不合格原因
            metaQuickColumnReasonDao.deleteByQuickColumnId(enterpriseId, metaQuickColumnId);
            return;
        }
        //不合格原因
        List<Long> existIdList = metaQuickColumnReasonDao.getIdListByColumnId(enterpriseId, metaQuickColumnId);
        quickTableColumnRequest.getColumnReasonList().forEach(columnReason -> {
            TbMetaQuickColumnReasonDO reasonDO = new TbMetaQuickColumnReasonDO();
            reasonDO.setQuickColumnId(metaQuickColumnId);
            reasonDO.setReasonName(columnReason.getReasonName());
            reasonDO.setId(columnReason.getId());
            if (columnReason.getId() == null) {
                reasonDO.setCreateUserId(userId);
                reasonDO.setMappingResult(columnReason.getMappingResult());
                metaQuickColumnReasonDao.batchInsert(enterpriseId, Collections.singletonList(reasonDO));
            } else {
                metaQuickColumnReasonDao.updateByPrimaryKeySelective(enterpriseId, reasonDO);
                existIdList.remove(columnReason.getId());
            }
        });
        if (CollectionUtils.isNotEmpty(existIdList)) {
            metaQuickColumnReasonDao.logicallyDeleteByIds(enterpriseId, existIdList);
        }

    }


    /**
     * 申诉原图
     * @param enterpriseId
     * @param metaQuickColumnId
     * @param quickTableColumnRequest
     * @param userId
     */
    private void updateQuickColumnAppeal(String enterpriseId, Long metaQuickColumnId, QuickTableColumnRequest quickTableColumnRequest, String userId) {
        if (CollectionUtils.isEmpty(quickTableColumnRequest.getColumnAppealList())) {
            //删除不合格原因
            metaQuickColumnAppealDao.deleteByQuickColumnId(enterpriseId, metaQuickColumnId);
            return;
        }
        //不合格原因
        List<Long> existIdList = metaQuickColumnAppealDao.getIdListByColumnId(enterpriseId, metaQuickColumnId);
        quickTableColumnRequest.getColumnAppealList().forEach(columnAppealRequest -> {
            TbMetaQuickColumnAppealDO appealDO = new TbMetaQuickColumnAppealDO();
            appealDO.setMetaQuickColumnId(metaQuickColumnId);
            appealDO.setAppealName(columnAppealRequest.getAppealName());
            appealDO.setCreateUserId(userId);
            appealDO.setId(columnAppealRequest.getId());
            if (columnAppealRequest.getId() == null) {
                appealDO.setCreateUserId(userId);
                metaQuickColumnAppealDao.batchInsert(enterpriseId, Collections.singletonList(appealDO));
            } else {
                metaQuickColumnAppealDao.updateByPrimaryKeySelective(appealDO, enterpriseId);
                existIdList.remove(appealDO.getId());
            }
        });
        if (CollectionUtils.isNotEmpty(existIdList)) {
            metaQuickColumnAppealDao.logicallyDeleteByIds(enterpriseId, existIdList);
        }
    }
}
