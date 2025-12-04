package com.coolcollege.intelligent.service.metatable.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaQuickColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaQuickColumnReasonDao;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbMetaQuickColumnAppealDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.mapper.metatable.TbMetaColumnCategoryDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaQuickColumnResultDAO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.mapper.store.StoreSceneDAO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.metatable.TbMetaQuickColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaQuickColumnImportDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnAppealDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbQuickColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaQuickColumnResultVO;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaStaTableColumnDetailVO;
import com.coolcollege.intelligent.model.oaPlugin.vo.OptionDataVO;
import com.coolcollege.intelligent.model.patrolstore.vo.QuickTableColumnListVO;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseConfigService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.service.importexcel.GenerateOssFileService;
import com.coolcollege.intelligent.service.importexcel.ImportBaseService;
import com.coolcollege.intelligent.service.metatable.TbMetaDataColumnService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shuchang.wei
 * @date 2020-12-9
 */
@Slf4j
@Service
public class TbMetaDataColumnServiceImpl implements TbMetaDataColumnService {
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaQuickColumnMapper tbMetaQuickColumnMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;
    @Resource
    private EnterpriseUserService enterpriseUserService;

    @Resource
    private SysRoleMapper roleMapper;

    @Resource
    private TaskSopService taskSopService;

    @Autowired
    private StoreSceneMapper storeSceneMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private ImportTaskMapper importTaskMapper;

    @Autowired
    private GenerateOssFileService generateOssFileService;

    @Autowired
    private EnterpriseConfigService enterpriseConfigService;

    @Autowired
    private TbMetaColumnCategoryDAO tbMetaColumnCategoryDAO;

    @Autowired
    private TbMetaQuickColumnResultDAO tbMetaQuickColumnResultDAO;

    @Autowired
    private TbMetaQuickColumnReasonDao metaQuickColumnReasonDao;

    @Autowired
    private TbMetaQuickColumnAppealDao metaQuickColumnAppealDao;

    @Autowired
    private StoreSceneDAO storeSceneDAO;

    @Autowired
    private RedisUtilPool redis;

    @Autowired
    private SysRoleService sysRoleService;


    @Autowired
    private EnterpriseSettingMapper enterpriseSettingMapper;

    @Autowired
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;
    @Autowired
    private AiModelLibraryService aiModelLibraryService;

    private final String DEFAULT_CATEGORY = "其他";

    public static final String COL_NAME_EMPTY = "检查项名称为空";

    public static final String COL_NAME_TOO_LONG = "检查项名称长度大于128";

    public static final String COL_NAME_REPEAT = "检查项名称重复";

    public final String IMPORT_TITLE = "填写须知：\n" +
            "1.请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败。\n" +
            "2.不要更改第2行数据，不要更改第2行数据,不要更改第2行数据（重要的事情说三遍）；";

    private final String DEFAULT_MONEY="0";


    @Override
    public Integer createStaTableColumn(String enterpriseId, List<TbMetaStaTableColumnDO> tbMetaStaTableColumnList) {
        if(CollectionUtils.isNotEmpty(tbMetaStaTableColumnList)){
            return tbMetaStaTableColumnMapper.insertColumnList(enterpriseId,tbMetaStaTableColumnList);
        }
        return 0;
    }

    @Override
    public Integer deleteStaTableColumn(String enterpriseId, Long checkTableId) {
        return tbMetaStaTableColumnMapper.deleteColumnByMetaTableId(enterpriseId,Arrays.asList(checkTableId));
    }

    @Override
    public List<TbMetaStaTableColumnDO> getTableColumn(String enterpriseId, List<Long> checkTableIdList,Boolean filterFreezeColumn) {
        if(checkTableIdList != null){
            return tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId,checkTableIdList,filterFreezeColumn);
        }
        return null;
    }

    @Override
    public PageInfo getQuickTableColumnList(String enterpriseId, Integer pageSize, Integer pageNum, String columnName, Integer columnType,
                                            Integer tableProperty, Long categoryId, Integer status, Integer orderBy, Boolean create, String userId, Integer isAiCheck) {
        List<Integer> columnTypes = new ArrayList<>();
        if(Objects.isNull(columnType)){
            columnTypes = MetaColumnTypeEnum.getDefaultColumnTypes();
        }
        if(Objects.nonNull(tableProperty)){
            columnTypes = MetaTablePropertyEnum.getTableColumnTypes(tableProperty);
        }
        String createUserId = null;
        //是否只查创建人创建的
        if(create != null && create){
            createUserId = userId;
        }
        //判断是否为管理员
        Boolean adminIs = sysRoleService.checkIsAdmin(enterpriseId, userId);
        String useUserId = null;
        if(adminIs != null && !adminIs){
            useUserId = userId;
        }
        String aiAlgorithms = null;
        if (CollectionUtils.isNotEmpty(columnTypes) && MetaTablePropertyEnum.AI_TABLE.getCode().equals(tableProperty)) {
            DataSourceHelper.reset();
            EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
            if (enterpriseSettingDO != null) {
                aiAlgorithms = enterpriseSettingDO.getAiAlgorithms();
            }
            DataSourceHelper.changeToMy();
            if(StringUtils.isBlank(aiAlgorithms)){
                return new PageInfo();
            }
        }
        PageHelper.startPage(pageNum,pageSize);
        List<String> aiAlgorithmsList = new ArrayList<>();
        if(StringUtils.isNotBlank(aiAlgorithms)){
            aiAlgorithmsList = Arrays.asList(aiAlgorithms.split(Constants.COMMA));
        }

        List<TbMetaQuickColumnDO> tbMetaQuickColumnDOs = tbMetaQuickColumnMapper.selectQuickTableColumnList(enterpriseId,columnName, columnType, columnTypes,
                categoryId, status, orderBy, createUserId, useUserId, aiAlgorithmsList, isAiCheck);
        List<QuickTableColumnListVO> quickTableColumnListVOList = new ArrayList<>();
        if(CollectionUtils.isEmpty(tbMetaQuickColumnDOs)){
            PageInfo pageInfo = new PageInfo(tbMetaQuickColumnDOs);
            pageInfo.setList(quickTableColumnListVOList);
        }
        List<Long> sopIdList = tbMetaQuickColumnDOs.stream().map(TbMetaQuickColumnDO::getSopId).collect(Collectors.toList());
        List<TaskSopVO> taskSopVOList = taskSopService.listByIdList(enterpriseId,sopIdList);
        Map<Long, TaskSopVO> taskSopVOMap = taskSopVOList.stream().collect(Collectors.toMap(TaskSopVO::getId, t -> t));// t->t 表示对象本身
        List<Long> categoryIds = tbMetaQuickColumnDOs.stream().map(TbMetaQuickColumnDO::getCategoryId).collect(Collectors.toList());
        Map<Long, String> categoryNameMap = tbMetaColumnCategoryDAO.getCategoryNameMap(enterpriseId, categoryIds);
        List<Long> ids = tbMetaQuickColumnDOs.stream().map(TbMetaQuickColumnDO::getId).collect(Collectors.toList());
        List<Long> sceneIds = tbMetaQuickColumnDOs.stream().map(o -> o.getStoreSceneId()).collect(Collectors.toList());
        Map<Long, String> storeSceneNameMap = storeSceneDAO.getStoreSceneNameMap(enterpriseId, sceneIds);
        List<String> userIdList = new ArrayList<>();
        //不合格原因
        List<TbQuickColumnReasonDTO> columnReasonList = metaQuickColumnReasonDao.getListByColumnIdList(enterpriseId, ids);
        Map<Long, List<TbQuickColumnReasonDTO>> columnReasonListMap = ListUtils.emptyIfNull(columnReasonList).stream().collect(Collectors.groupingBy(TbQuickColumnReasonDTO::getQuickColumnId));
        Map<Long, List<TbMetaQuickColumnResultVO>> columnResultListMap = tbMetaQuickColumnResultDAO.getColumnResultListMap(enterpriseId, ids);

        List<TbQuickColumnAppealDTO> appealDTOList = metaQuickColumnAppealDao.getListByColumnIdList(enterpriseId, ids);
        Map<Long, List<TbQuickColumnAppealDTO>> appealDTOListMap = ListUtils.emptyIfNull(appealDTOList).stream().collect(Collectors.groupingBy(TbQuickColumnAppealDTO::getMetaQuickColumnId));

        Set<String> aiModelCodes = CollStreamUtil.toSet(tbMetaQuickColumnDOs, TbMetaQuickColumnDO::getAiModel);
        Map<String, String> aiModelMap = aiModelLibraryService.getModelNameMapByCodes(new ArrayList<>(aiModelCodes));
        for(TbMetaQuickColumnDO tbMetaQuickColumnDO : tbMetaQuickColumnDOs){
            QuickTableColumnListVO quickTableColumnListVO = new QuickTableColumnListVO();
            quickTableColumnListVO.setCreateUser(tbMetaQuickColumnDO.getCreateUser());
            quickTableColumnListVO.setCreateTime(tbMetaQuickColumnDO.getCreateTime());
            quickTableColumnListVO.setCategoryName(categoryNameMap.get(tbMetaQuickColumnDO.getCategoryId()));
            quickTableColumnListVO.setColumnName(tbMetaQuickColumnDO.getColumnName());
            quickTableColumnListVO.setDescription(tbMetaQuickColumnDO.getDescription());
            quickTableColumnListVO.setId(tbMetaQuickColumnDO.getId());
            quickTableColumnListVO.setAwardMoney(tbMetaQuickColumnDO.getAwardMoney());
            quickTableColumnListVO.setPunishMoney(tbMetaQuickColumnDO.getPunishMoney());
            quickTableColumnListVO.setStandardPic(tbMetaQuickColumnDO.getStandardPic());
            quickTableColumnListVO.setCreateUserName(tbMetaQuickColumnDO.getCreateUserName());
            quickTableColumnListVO.setQuestionHandlerId(tbMetaQuickColumnDO.getQuestionHandlerId());
            quickTableColumnListVO.setQuestionHandlerType(tbMetaQuickColumnDO.getQuestionHandlerType());
            quickTableColumnListVO.setQuestionRecheckerId(tbMetaQuickColumnDO.getQuestionRecheckerId());
            quickTableColumnListVO.setQuestionRecheckerType(tbMetaQuickColumnDO.getQuestionRecheckerType());
            quickTableColumnListVO.setCreateUserApprove(tbMetaQuickColumnDO.getCreateUserApprove());
            quickTableColumnListVO.setQuestionCcId(tbMetaQuickColumnDO.getQuestionCcId());
            quickTableColumnListVO.setQuestionCcType(tbMetaQuickColumnDO.getQuestionCcType());
            quickTableColumnListVO.setUpdateUserName(tbMetaQuickColumnDO.getEditUserName());
            quickTableColumnListVO.setMustPic(tbMetaQuickColumnDO.getMustPic());
            String questionHandlerName =tbMetaQuickColumnDO.getQuestionHandlerName();
            quickTableColumnListVO.setQuestionHandlerName(questionHandlerName);
            String recheckerName =tbMetaQuickColumnDO.getQuestionRecheckerName();
            quickTableColumnListVO.setQuestionRecheckerName(recheckerName);
            //设置抄送人名称
            quickTableColumnListVO.setQuestionCcName(tbMetaQuickColumnDO.getQuestionCcName());
            quickTableColumnListVO.setSopId(tbMetaQuickColumnDO.getSopId());
            //加载门店场景id
            quickTableColumnListVO.setStoreSceneId(tbMetaQuickColumnDO.getStoreSceneId());
            quickTableColumnListVO.setStoreSceneIsDelete(true);
            quickTableColumnListVO.setQuestionApproveUser(tbMetaQuickColumnDO.getQuestionApproveUser());
            quickTableColumnListVO.setUpdateUserName(tbMetaQuickColumnDO.getEditUserName());
            quickTableColumnListVO.setUpdateUserId(tbMetaQuickColumnDO.getEditUserId());
            quickTableColumnListVO.setCreateUserId(tbMetaQuickColumnDO.getCreateUser());
            quickTableColumnListVO.setUpdateTime(tbMetaQuickColumnDO.getEditTime());
            quickTableColumnListVO.setColumnTypeName(MetaColumnTypeEnum.getColumnTypeName(tbMetaQuickColumnDO.getColumnType()));
            quickTableColumnListVO.setThreshold(tbMetaQuickColumnDO.getThreshold());
            quickTableColumnListVO.setAiType(tbMetaQuickColumnDO.getAiType());
            quickTableColumnListVO.setCategoryId(tbMetaQuickColumnDO.getCategoryId());
            quickTableColumnListVO.setMaxScore(tbMetaQuickColumnDO.getMaxScore());
            quickTableColumnListVO.setMinScore(tbMetaQuickColumnDO.getMinScore());
            quickTableColumnListVO.setStatus(tbMetaQuickColumnDO.getStatus());
            quickTableColumnListVO.setColumnType(tbMetaQuickColumnDO.getColumnType());
            quickTableColumnListVO.setColumnResultList(columnResultListMap.get(tbMetaQuickColumnDO.getId()));
            quickTableColumnListVO.setUserDefinedScore(tbMetaQuickColumnDO.getUserDefinedScore());
            quickTableColumnListVO.setConfigType(tbMetaQuickColumnDO.getConfigType());
            quickTableColumnListVO.setStoreSceneName(storeSceneNameMap.get(tbMetaQuickColumnDO.getStoreSceneId()));
            quickTableColumnListVO.setColumnReasonList(columnReasonListMap.get(tbMetaQuickColumnDO.getId()));
            quickTableColumnListVO.setColumnAppealList(appealDTOListMap.get(tbMetaQuickColumnDO.getId()));
            quickTableColumnListVO.setIsAiCheck(tbMetaQuickColumnDO.getIsAiCheck());
            quickTableColumnListVO.setAiCheckStdDesc(tbMetaQuickColumnDO.getAiCheckStdDesc());
            if (quickTableColumnListVO.getStoreSceneId()!=null){
                StoreSceneDo storeSceneById = storeSceneMapper.getStoreSceneById(enterpriseId, quickTableColumnListVO.getStoreSceneId());
                if (storeSceneById!=null){
                    quickTableColumnListVO.setStoreSceneIsDelete(false);
                }
            }
            // sop文档对象
            if(taskSopVOMap != null){
                quickTableColumnListVO.setTaskSopVO(taskSopVOMap.get(tbMetaQuickColumnDO.getSopId()));
            }
            if (StringUtils.isNoneEmpty(tbMetaQuickColumnDO.getCoolCourse())) {
                // todo： 改这里
                quickTableColumnListVO.setCoolCourseVO(JSON.parseObject(tbMetaQuickColumnDO.getCoolCourse(), CoolCourseVO.class));
            }
            if (StringUtils.isNoneEmpty(tbMetaQuickColumnDO.getFreeCourse())) {
                // todo： 改这里
                quickTableColumnListVO.setFreeCourseVO(JSON.parseObject(tbMetaQuickColumnDO.getFreeCourse(), CoolCourseVO.class));
            }
            if(userId.equals(tbMetaQuickColumnDO.getCreateUser())){
                quickTableColumnListVO.setEditFlag(true);
            }
            if(adminIs){
                quickTableColumnListVO.setEditFlag(true);
            }
            //共同编辑人列表
            List<PersonDTO> personDTOList = new ArrayList<>();
            if(StringUtils.isNotBlank(tbMetaQuickColumnDO.getCommonEditUserids())){
                List<String> commonEditUserIdList = StrUtil.splitTrim(tbMetaQuickColumnDO.getCommonEditUserids(), ",");
                Map<String, String> userMap = enterpriseUserDao.getUserNameMap(enterpriseId, commonEditUserIdList);
                commonEditUserIdList.forEach(editUserId -> {
                    PersonDTO personDTO = new PersonDTO();
                    personDTO.setUserId(editUserId);
                    personDTO.setUserName(userMap.get(editUserId));
                    personDTOList.add(personDTO);
                });
                if(commonEditUserIdList.contains(userId)){
                    quickTableColumnListVO.setEditFlag(true);
                }
            }
            quickTableColumnListVO.setUseRange(tbMetaQuickColumnDO.getUseRange());
            quickTableColumnListVO.setUsePersonInfo(tbMetaQuickColumnDO.getUsePersonInfo());
            quickTableColumnListVO.setCommonEditUserList(personDTOList);
            JSONObject extendInfo = JSONObject.parseObject(tbMetaQuickColumnDO.getExtendInfo());
            if (Objects.nonNull(extendInfo)) {
                quickTableColumnListVO.setDescRequired(extendInfo.getBoolean(Constants.TableColumn.DESC_REQUIRED));
                quickTableColumnListVO.setAutoQuestionTaskValidity(extendInfo.getInteger(Constants.TableColumn.AUTO_QUESTION_TASK_VALIDITY));
                quickTableColumnListVO.setIsSetAutoQuestionTaskValidity(extendInfo.getBoolean(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY));
                quickTableColumnListVO.setMinCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MIN_CHECK_PIC_NUM));
                quickTableColumnListVO.setMaxCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MAX_CHECK_PIC_NUM));
            }
            quickTableColumnListVO.setAiModel(tbMetaQuickColumnDO.getAiModel());
            quickTableColumnListVO.setAiModelName(aiModelMap.get(tbMetaQuickColumnDO.getAiModel()));
            quickTableColumnListVOList.add(quickTableColumnListVO);
            userIdList.add(tbMetaQuickColumnDO.getCreateUser());
            userIdList.add(tbMetaQuickColumnDO.getEditUserId());
        }
        Map<String, String> userIdNameMap = enterpriseUserService.getUserNameMap(enterpriseId, userIdList);
        quickTableColumnListVOList.stream().forEach( quickTableColumnListVO -> {
            String userName = userIdNameMap.get(quickTableColumnListVO.getCreateUser());
            if(StringUtils.isNotBlank(userName)){
                quickTableColumnListVO.setCreateUser(userName);
            }
            String updateUserName = userIdNameMap.get(quickTableColumnListVO.getUpdateUserId());
            if(StringUtils.isNotBlank(updateUserName)){
                quickTableColumnListVO.setUpdateUserName(updateUserName);
            }
            if (StringUtils.isNotBlank(quickTableColumnListVO.getQuestionCcId())) {
                JSONArray jsonArray = JSONUtil.parseArray(quickTableColumnListVO.getQuestionCcId());
                List<PersonPositionDTO> listUserPo = JSONUtil.toList(jsonArray, PersonPositionDTO.class);
                quickTableColumnListVO.setCcPeopleList(listUserPo);
            }
        });
        PageInfo pageInfo = new PageInfo(tbMetaQuickColumnDOs);
        pageInfo.setList(quickTableColumnListVOList);
        return pageInfo;
    }

    @Override
    public List<String> getQuickTableColumnCategory(String enterpriseId) {
        List<String> categoryList = tbMetaQuickColumnMapper.selectAllCategory(enterpriseId);
        return categoryList;
    }

    @Override
    public Boolean deleteQuickTableColumnCategory(String enterpriseId, String userId, List<Long> columnIdList) {
        if( CollectionUtils.isEmpty(columnIdList)){
            return false;
        }
        tbMetaQuickColumnMapper.deleteByIdList(enterpriseId,columnIdList);
        return Boolean.TRUE;
    }

    @Async("importExportThreadPool")
    @Override
    public Boolean importQuickMetaColumn(String enterpriseId, List<Map<String, Object>> dataMapList, String originalFilename
            , CurrentUser user, ImportTaskDO task) {
        boolean lock = lock(enterpriseId, ImportConstants.USER_KEY);
        if (!lock) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(ImportBaseService.EXIST_TASK);
            importTaskMapper.update(enterpriseId, task);
            return false;
        }
        DataSourceHelper.reset();
        EnterpriseConfigDO config = enterpriseConfigService.selectByEnterpriseId(enterpriseId);
        DataSourceHelper.changeToSpecificDataSource(config.getDbName());
        List<TbMetaQuickColumnDO> columnDOList = new ArrayList<>();
        List<TbMetaQuickColumnImportDTO> errorList = new ArrayList<>();
        List<TbMetaQuickColumnDO> allColumnList = tbMetaQuickColumnMapper.selectAllColumnList(enterpriseId);
        List<String> nameList = allColumnList.stream().map(tbMetaQuickColumnDO -> tbMetaQuickColumnDO.getColumnName()).collect(Collectors.toList());
        ListUtils.emptyIfNull(dataMapList).stream()
                .forEach(data->{
                    String columnName = data.get("检查项名称（必填）") == null ? null : String.valueOf(data.get("检查项名称（必填）"));
                    String category = data.get("检查项分类") == null ? null : String.valueOf(data.get("检查项分类"));
                    try {
                        TbMetaQuickColumnDO entity = buildColumn(data, user);
                        if(StringUtils.isBlank(columnName)){
                            TbMetaQuickColumnImportDTO buildImportDto = buildImportDto(entity);
                            buildImportDto.setColumnName(columnName);
                            buildImportDto.setDec(COL_NAME_EMPTY);
                            errorList.add(buildImportDto);
                            return;
                        }
                        if(columnName.length() >  Constants.COLUMN_NAME_MAX_LENGTH){
                            TbMetaQuickColumnImportDTO buildImportDto = buildImportDto(entity);
                            buildImportDto.setColumnName(columnName);
                            buildImportDto.setDec(COL_NAME_TOO_LONG);
                            errorList.add(buildImportDto);
                            return;
                        }
                        if(nameList.contains(columnName)){
                            TbMetaQuickColumnImportDTO buildImportDto = buildImportDto(entity);
                            buildImportDto.setColumnName(columnName);
                            buildImportDto.setDec(COL_NAME_REPEAT);
                            errorList.add(buildImportDto);
                            return;
                        }

                        columnDOList.add(entity);
                    } catch (Exception e) {
                        log.error("importQuickMetaColumn error", e);
                        TbMetaQuickColumnImportDTO error = new TbMetaQuickColumnImportDTO();
                        error.setColumnName(columnName);
                        error.setCategory(category);
                        error.setDec(ImportBaseService.DATA_ERROR);
                        errorList.add(error);
                    }
                });
        if (errorList.size() != 0) {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            String url = generateOssFileService.generateOssExcel(errorList, enterpriseId, IMPORT_TITLE , "出错检查项列表",
                    "", TbMetaQuickColumnImportDTO.class);
            task.setFileUrl(url);
        } else {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        }
        task.setTotalNum(dataMapList.size());
        task.setSuccessNum(task.getTotalNum() - errorList.size());
        importTaskMapper.update(enterpriseId, task);
        if(CollectionUtils.isNotEmpty(columnDOList)){
            tbMetaQuickColumnMapper.batchInsert(enterpriseId,columnDOList);
        }
        return Boolean.TRUE;
    }

    private TbMetaQuickColumnDO buildColumn(Map<String, Object> data, CurrentUser user) {
        TbMetaQuickColumnDO entity = new TbMetaQuickColumnDO();
        if (data.get("检查项分类") != null) {
            String category = String.valueOf(data.get("检查项分类"));
            entity.setCategory(category);
        } else {
            entity.setCategory(DEFAULT_CATEGORY);
        }
        if(data.get("检查项名称（必填）") != null){
            String columnName = String.valueOf(data.get("检查项名称（必填）"));
            entity.setColumnName(columnName);
        }
        if(data.get("检查项描述") != null){
            String description = String.valueOf(data.get("检查项描述"));
            entity.setDescription(description);
        }
        String score = String.valueOf(data.get("分值"));
        String awardMoney = String.valueOf(data.get("奖励金额（正数，如50）"));
        if(StringUtils.isBlank(awardMoney)){
            awardMoney = DEFAULT_MONEY;
        }
        entity.setAwardMoney(new BigDecimal(awardMoney));
        String punishMoney = String.valueOf(data.get("惩罚金额（正数，如50）"));
        if (StringUtils.isBlank(punishMoney)){
            punishMoney = DEFAULT_MONEY;
        }
        entity.setPunishMoney(new BigDecimal(punishMoney));
        entity.setCreateUser(user.getUserId());
        entity.setCreateUserName(user.getName());
        entity.setEditUserId(user.getUserId());
        entity.setEditUserName(user.getName());
        entity.setQuestionCcId("");
        entity.setQuestionCcType("");
        entity.setQuestionCcName("");
        return entity;
    }

    private TbMetaQuickColumnImportDTO buildImportDto(TbMetaQuickColumnDO entity) {
        TbMetaQuickColumnImportDTO importDTO = new TbMetaQuickColumnImportDTO();
        importDTO.setCategory(entity.getCategory());
        importDTO.setColumnName(entity.getColumnName());
        importDTO.setDescription(entity.getDescription());
        importDTO.setAwardMoney(entity.getAwardMoney());
        importDTO.setPunishMoney(entity.getPunishMoney());
        return importDTO;
    }



    @Override
    public Boolean exitQuickTableColumn(String enterpriseId, String columnName) {
        Integer count = tbMetaQuickColumnMapper.isExit(enterpriseId,columnName);
        return count>0;
    }

    @Override
    public List<TbMetaQuickColumnDO> getAllQuickMetaColumnList(String enterpriseId) {
        List<TbMetaQuickColumnDO> allColumnList = tbMetaQuickColumnMapper.selectAllColumnList(enterpriseId);
        return allColumnList;
    }

    public boolean lock(String eid, String key) {
        return redis.setNxExpire(String.format(key, eid), UUIDUtils.get8UUID(), ImportBaseService.LOCK_TIME);
    }

    @Override
    public TbMetaStaTableColumnDetailVO getMetaColumnById(String enterpriseId, Long metaColumnId) {
        TbMetaStaTableColumnDO metaStaTableColumnDO = tbMetaStaTableColumnMapper.selectByPrimaryKey(enterpriseId, metaColumnId);
        if (metaStaTableColumnDO == null) {
            throw new ServiceException(ErrorCodeEnum.META_COLUMN_NOT_EXIST);
        }
        TbMetaStaTableColumnDetailVO tbMetaStaColumnVO = new TbMetaStaTableColumnDetailVO();
        tbMetaStaColumnVO.setId(metaStaTableColumnDO.getId());
        tbMetaStaColumnVO.setCategory(metaStaTableColumnDO.getCategoryName());
        tbMetaStaColumnVO.setMetaTableId(metaStaTableColumnDO.getMetaTableId());
        tbMetaStaColumnVO.setColumnName(metaStaTableColumnDO.getColumnName());
        tbMetaStaColumnVO.setDescription(metaStaTableColumnDO.getDescription());
        tbMetaStaColumnVO.setQuestionHandlerId(metaStaTableColumnDO.getQuestionHandlerId());
        tbMetaStaColumnVO.setQuestionHandlerType(metaStaTableColumnDO.getQuestionHandlerType());

        tbMetaStaColumnVO.setQuestionRecheckerId(metaStaTableColumnDO.getQuestionRecheckerId());
        tbMetaStaColumnVO.setQuestionRecheckerType(metaStaTableColumnDO.getQuestionRecheckerType());
        tbMetaStaColumnVO.setQuestionApproveUser(metaStaTableColumnDO.getQuestionApproveUser());
        tbMetaStaColumnVO.setQuestionCcId(metaStaTableColumnDO.getQuestionCcId());
        tbMetaStaColumnVO.setSopId(metaStaTableColumnDO.getSopId());
        tbMetaStaColumnVO.setFreeCourse(metaStaTableColumnDO.getFreeCourse());
        tbMetaStaColumnVO.setCoolCourse(metaStaTableColumnDO.getCoolCourse());
        if (metaStaTableColumnDO.getSopId() != null && metaStaTableColumnDO.getSopId() > 0) {
            TaskSopVO taskSopVO = taskSopService.getSopById(enterpriseId, metaStaTableColumnDO.getSopId());
            if (taskSopVO != null) {
                tbMetaStaColumnVO.setSopName(taskSopVO.getFileName());
            }
        }
        //设置人员名称
        setMetaColumnName(metaStaTableColumnDO, tbMetaStaColumnVO, enterpriseId);
        return tbMetaStaColumnVO;
    }

    @Override
    public List<OptionDataVO> listColumnForOaPlugin(String enterpriseId) {
        List<TbMetaQuickColumnDO> tbMetaQuickColumnDOList = tbMetaQuickColumnMapper.listColumnForOaPlugin(enterpriseId);
        List<OptionDataVO> result = new ArrayList<>();
        ListUtils.emptyIfNull(tbMetaQuickColumnDOList).forEach(columnDO -> {
            OptionDataVO optionDataVO = new OptionDataVO();
            /*optionDataVO.setId(String.valueOf(columnDO.getId()));
            optionDataVO.setName(columnDO.getColumnName());*/
            result.add(optionDataVO);
        });
        return result;
    }

    public void setMetaColumnName(TbMetaStaTableColumnDO metaStaTableColumnDO, TbMetaStaTableColumnDetailVO tbMetaStaColumnVO, String enterpriseId){

        Set<String> userIds = new HashSet<>();
        Set<Long> positionIds = new HashSet<>();
        String position = UnifyTaskConstant.PersonType.POSITION;
        String person = UnifyTaskConstant.PersonType.PERSON;
        List<String> questionHandlerIdList = new ArrayList<>();
        List<Long> questionHandlerPositionIdList = new ArrayList<>();
        if (StringUtils.isNotBlank(metaStaTableColumnDO.getQuestionHandlerId())) {
            if (position.equals(metaStaTableColumnDO.getQuestionHandlerType())) {
                Arrays.stream(metaStaTableColumnDO.getQuestionHandlerId().split(Constants.COMMA)).forEach(data -> {
                    positionIds.add(Long.valueOf(data));
                    questionHandlerPositionIdList.add(Long.valueOf(data));
                });
            }
            if (person.equals(metaStaTableColumnDO.getQuestionHandlerType())) {
                questionHandlerIdList = Arrays.asList(metaStaTableColumnDO.getQuestionHandlerId().split(Constants.COMMA));
                userIds.addAll(questionHandlerIdList);

            }
        }

        List<String> questionRecheckerIdList = new ArrayList<>();
        List<Long> questionRecheckerPositionIdList = new ArrayList<>();

        if (StringUtils.isNotBlank(metaStaTableColumnDO.getQuestionRecheckerId())) {
            if (position.equals(metaStaTableColumnDO.getQuestionRecheckerType())) {
                Arrays.stream(metaStaTableColumnDO.getQuestionRecheckerId().split(Constants.COMMA)).forEach(data -> {
                    positionIds.add(Long.valueOf(data));
                    questionRecheckerPositionIdList.add(Long.valueOf(data));
                });
            }
            if (person.equals(metaStaTableColumnDO.getQuestionRecheckerType())) {
                questionRecheckerIdList = Arrays.asList(metaStaTableColumnDO.getQuestionRecheckerId().split(Constants.COMMA));
                userIds.addAll(questionRecheckerIdList);
            }
        }

        // Map: userId -> userName
        Map<String, String> userIdNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(userIds)) {
            List<EnterpriseUserDO> enterpriseUserDOList =
                    enterpriseUserDao.listByUserIdIgnoreActive(enterpriseId, new ArrayList<>(userIds));
            userIdNameMap.putAll(enterpriseUserDOList.stream().filter(a -> a.getUserId() != null && a.getName() != null)
                    .collect(Collectors.toMap(EnterpriseUserDO::getUserId, EnterpriseUserDO::getName, (a, b) -> a)));
        }
        // Map: roleId -> roleName
        Map<Long, String> positionIdNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(positionIds)) {
            List<SysRoleDO> roleList = sysRoleMapper.getRoleList(enterpriseId, new ArrayList<>(positionIds));
            positionIdNameMap.putAll(
                    roleList.stream()
                            .filter(a -> a.getId() != null && a.getRoleName() != null)
                            .collect(Collectors.toMap(SysRoleDO::getId, SysRoleDO::getRoleName, (a, b) -> a)));
        }

        // 设值
        if (StringUtils.isNotBlank(metaStaTableColumnDO.getQuestionHandlerId())) {
            List<String> userNameList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(questionHandlerIdList)) {
                for(String userId : questionHandlerIdList){
                    String userName = userIdNameMap.get(userId);
                    if(StringUtils.isNotBlank(userName)){
                        userNameList.add(userName);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(questionHandlerPositionIdList)) {
                for(Long positionId : questionHandlerPositionIdList){
                    String name = positionIdNameMap.get(positionId);
                    if(StringUtils.isNotBlank(name)){
                        userNameList.add(name);
                    }
                };
            }
            if(CollectionUtils.isNotEmpty(userNameList)){
                tbMetaStaColumnVO.setQuestionHandlerName(StringUtils.join(userNameList, Constants.COMMA));
            }
        }
        if (StringUtils.isNotBlank(metaStaTableColumnDO.getQuestionRecheckerId())) {
            List<String> userNameList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(questionRecheckerIdList)) {
                for(String userId : questionRecheckerIdList){
                    String userName = userIdNameMap.get(userId);
                    if(StringUtils.isNotBlank(userName)){
                        userNameList.add(userName);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(questionRecheckerPositionIdList)) {
                for(Long positionId : questionRecheckerPositionIdList){
                    String name = positionIdNameMap.get(positionId);
                    if(StringUtils.isNotBlank(name)){
                        userNameList.add(name);
                    }
                };
            }
            if(CollectionUtils.isNotEmpty(userNameList)){
                tbMetaStaColumnVO.setQuestionRecheckerName(StringUtils.join(userNameList, Constants.COMMA));
            }
        }
    }

}
