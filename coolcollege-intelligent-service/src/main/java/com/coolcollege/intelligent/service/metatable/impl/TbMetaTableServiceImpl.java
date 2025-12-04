package com.coolcollege.intelligent.service.metatable.impl;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.datasource.DynamicDataSourceContextHolder;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.PersonTypeEnum;
import com.coolcollege.intelligent.common.enums.UserRangeTypeEnum;
import com.coolcollege.intelligent.common.enums.YesOrNoEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaColumnTypeEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseStoreCheckSettingMapper;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserRoleMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.*;
import com.coolcollege.intelligent.dao.metatable.dao.TbMetaColumnReasonDao;
import com.coolcollege.intelligent.dao.patrolstore.TbPatrolStoreRecordMapper;
import com.coolcollege.intelligent.dao.pictureInspection.StoreSceneMapper;
import com.coolcollege.intelligent.dao.safetycheck.dao.TbMetaColumnAppealDao;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.tbdisplay.TbMetaDisplayTableColumnMapper;
import com.coolcollege.intelligent.dao.util.DataSourceHelper;
import com.coolcollege.intelligent.dto.MetaTableDetailDTO;
import com.coolcollege.intelligent.mapper.metatable.TbMetaTableUserAuthDAO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseStoreCheckSettingDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonUsePositionDTO;
import com.coolcollege.intelligent.model.enums.FormPickerEnum;
import com.coolcollege.intelligent.model.enums.UnifyTaskDataTypeEnum;
import com.coolcollege.intelligent.model.metatable.*;
import com.coolcollege.intelligent.model.metatable.dto.*;
import com.coolcollege.intelligent.model.metatable.request.*;
import com.coolcollege.intelligent.model.metatable.response.MetaTableMetaColumnResp;
import com.coolcollege.intelligent.model.metatable.vo.*;
import com.coolcollege.intelligent.model.patrolstore.TbPatrolStoreRecordDO;
import com.coolcollege.intelligent.model.patrolstore.request.QuickTableColumnRequest;
import com.coolcollege.intelligent.model.pictureInspection.StoreSceneDo;
import com.coolcollege.intelligent.model.safetycheck.TbMetaColumnAppealDO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.tbdisplay.TbMetaDisplayTableColumnDO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableDTO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableItemDTO;
import com.coolcollege.intelligent.model.tbdisplay.dto.TbDisplayTableQuery;
import com.coolcollege.intelligent.model.tbdisplay.vo.TbDisplayTaskShowVO;
import com.coolcollege.intelligent.model.tbdisplay.vo.UnifyTbDisplayTableDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.rpc.config.MetaTableRpcService;
import com.coolcollege.intelligent.service.ai.AiModelLibraryService;
import com.coolcollege.intelligent.service.enterprise.UserPersonInfoService;
import com.coolcollege.intelligent.service.metatable.TbMetaDataColumnService;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.metatable.calscore.AbstractColumnObserver;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.util.patrolStore.TableTypeUtil;
import com.coolstore.base.utils.MDCUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.TableTypeConstant.*;

@Slf4j
@Service
public class TbMetaTableServiceImpl implements TbMetaTableService {
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private TbMetaColumnResultMapper tbMetaColumnResultMapper;
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Resource
    private TbPatrolStoreRecordMapper tbPatrolStoreRecordMapper;
    @Resource
    private TbMetaDisplayTableColumnMapper tbMetaDisplayTableColumnMapper;
    @Resource
    private StoreSceneMapper storeSceneMapper;
    @Resource
    private EnterpriseStoreCheckSettingMapper enterpriseStoreCheckSettingMapper;

    @Resource
    private SysRoleService sysRoleService;
    private final String DEFAULT_CATEGORY = "其他";
    @Resource
    private TbMetaDefTableColumnMapper tbMetaDefTableColumnMapper;
    // 标准检查项相关Service
    @Lazy
    @Resource
    private TbMetaDataColumnService metaDataService;

    @Resource
    private TaskSopService taskSopService;

    @Resource
    private MetaTableRpcService metaTableRpcService;

    @Resource
    @Lazy
    private UserPersonInfoService userPersonInfoService;

    @Resource
    private TbMetaColumnReasonDao metaColumnReasonDao;

    @Resource
    private TbMetaColumnAppealDao metaColumnAppealDao;

    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Resource
    private EnterpriseUserRoleMapper enterpriseUserRoleMapper;

    @Resource
    TbMetaQuickColumnResultMapper tbMetaQuickColumnResultMapper;

    @Resource
    private TbMetaTableUserAuthDAO tbMetaTableUserAuthDAO;

    @Resource
    private AiModelLibraryService aiModelLibraryService;

    @Override
    public PageInfo getList(String enterpriseId, String name, String tableType, CurrentUser user, List<Long> tableIdList, Boolean isAll, String tableProperty) {
        String userId = user.getUserId();
        long startTime = System.currentTimeMillis();
        Boolean adminIs = sysRoleService.checkIsAdmin(enterpriseId, userId);
        List<String> tableTypeList = new ArrayList<>();
        if (StringUtils.isNotBlank(tableType)) {
            tableTypeList = Arrays.asList(tableType.split(","));
        }
        List<TbMetaTableDO> tbMetaTableDOS = new ArrayList<>();
        List<String> tablePropertyList = null;
        tablePropertyList = Collections.singletonList(String.valueOf(MetaTablePropertyEnum.STANDARD_TABLE.getCode()));
        if (StringUtils.isNotBlank(tableProperty)) {
            tablePropertyList = Arrays.asList(tableProperty.split(Constants.COMMA));
        }
        if (StringUtils.isBlank(tableProperty) && TB_DISPLAY.equals(tableType)) {
            tablePropertyList = null;
        }
        log.info("获取检查表1：{}", System.currentTimeMillis() - startTime);
        tbMetaTableDOS = tbMetaTableMapper.selectList(enterpriseId, name, tableTypeList, userId, adminIs, tableIdList, isAll, tablePropertyList, "using");
        log.info("获取检查表2：{}", System.currentTimeMillis() - startTime);
        List<Long> standardIdList = null;
        List<Long> defIdList = null;
        List<Long> idList = null;
        if (CollectionUtils.isNotEmpty(tableTypeList) && tableTypeList.contains(UnifyTaskDataTypeEnum.STANDARD.getCode())) {
            standardIdList = tbMetaTableDOS.stream().filter(x -> !MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                    .map(data -> data.getId()).collect(Collectors.toList());
            defIdList = tbMetaTableDOS.stream().filter(x -> MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                    .map(data -> data.getId()).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(tableTypeList) && tableTypeList.contains(UnifyTaskDataTypeEnum.TB_DISPLAY.getCode())) {
            idList = tbMetaTableDOS.stream().map(data -> data.getId()).collect(Collectors.toList());
        }

        Map<Long, List<TbMetaDefTableColumnDO>> defMap = new HashMap<>(16);
        Map<Long, List<TbMetaStaTableColumnDO>> staMap = new HashMap<>(16);
        Map<Long, List<TbMetaDisplayTableColumnDO>> tbDisplayMap = new HashMap<>(16);

        if (CollectionUtils.isNotEmpty(standardIdList)) {
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, standardIdList);
            staMap = tbMetaStaTableColumnDOList.stream().collect(Collectors.groupingBy(data -> data.getMetaTableId()));
        }

        if (CollectionUtils.isNotEmpty(defIdList)) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defIdList);
            defMap = tbMetaDefTableColumnDOList.stream().collect(Collectors.groupingBy(data -> data.getMetaTableId()));
        }

        if (StringUtils.isNotBlank(tableType) && UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(tableType)) {
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.selectAllColumnListByTableIdList(enterpriseId, idList);
            tbDisplayMap = tbMetaDisplayTableColumnDOList.stream().filter(date -> date.getCheckType().equals(Constants.INDEX_ZERO)).collect(Collectors.groupingBy(TbMetaDisplayTableColumnDO::getMetaTableId));
        }
        log.info("获取检查表3：{}", System.currentTimeMillis() - startTime);
        List<TbMetaTableVO> tbMetaTableVOList = new ArrayList<>();
        for (TbMetaTableDO pdo : tbMetaTableDOS) {
            TbMetaTableVO item = new TbMetaTableVO();
            item.setActive(pdo.getActive());
            item.setId(pdo.getId());
            item.setCreateTime(pdo.getCreateTime());
            item.setLocked(pdo.getLocked());
            item.setTableName(pdo.getTableName());
            item.setTableType(pdo.getTableType());
            item.setShareGroup(pdo.getShareGroup());
            item.setShareGroupName(pdo.getShareGroupName());
            item.setResultShareGroupName(pdo.getResultShareGroupName());
            item.setResultShareGroup(pdo.getResultShareGroup());
            item.setColumnCount(0);
            item.setColumnCategoryCount(Constants.INDEX_ZERO);
            item.setCreateUserId(pdo.getCreateUserId());
            item.setCreateUserName(pdo.getCreateUserName());
            item.setTableProperty(pdo.getTableProperty());
            item.setTotalScore(pdo.getTotalScore());
            item.setDefaultResultColumn(pdo.getDefaultResultColumn());
            item.setNoApplicableRule(pdo.getNoApplicableRule());
            item.setCategoryNameList(pdo.getCategoryNameList());
            item.setOrderNum(pdo.getOrderNum());
            item.setStatus(pdo.getStatus());
            item.setTopTime(pdo.getTopTime());
            if (StringUtils.isNotBlank(pdo.getTableType()) && UnifyTaskDataTypeEnum.STANDARD.getCode().equals(pdo.getTableType())) {
                Integer columnCount = staMap.get(pdo.getId()) == null ? 0 : staMap.get(pdo.getId()).size();
                //检查项不为空，筛选出检查项分类数
                if (!Constants.INDEX_ZERO.equals(columnCount)) {
                    Set<String> categoryList = staMap.get(pdo.getId()).stream().map(TbMetaStaTableColumnDO::getCategoryName).collect(Collectors.toSet());
                    item.setColumnCategoryCount(categoryList.size());
                }
                item.setColumnCount(columnCount);
            }
            if (TableTypeUtil.isUserDefinedTable(pdo.getTableProperty(), pdo.getTableType())) {
                Integer columnCount = defMap.get(pdo.getId()) == null ? 0 : defMap.get(pdo.getId()).size();
                item.setColumnCount(columnCount);
            }
            if (StringUtils.isNotBlank(pdo.getTableType()) && UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(pdo.getTableType())) {
                Integer columnCount = tbDisplayMap.get(pdo.getId()) == null ? 0 : tbDisplayMap.get(pdo.getId()).size();
                item.setColumnCount(columnCount);
            }
            setUpdateUserByDO(item, pdo);

            item.setUsePersonInfo(pdo.getUsePersonInfo());
            item.setUseRange(pdo.getUseRange());
            item.setResultViewRange(pdo.getResultViewRange());
            item.setResultViewPersonInfo(pdo.getResultViewPersonInfo());
            item.setEditFlag(false);
            if (userId.equals(item.getCreateUserId())) {
                item.setEditFlag(true);
            }
            if (adminIs) {
                item.setEditFlag(true);
            }
            item.setIsAiCheck(pdo.getIsAiCheck());
            tbMetaTableVOList.add(item);
        }
        PageInfo pageInfo = new PageInfo(tbMetaTableDOS);
        pageInfo.setList(tbMetaTableVOList);
        log.info("获取检查表总耗时：{}", System.currentTimeMillis()-startTime);
        return pageInfo;
    }

    @Override
    public PageInfo getListV2(String enterpriseId, CurrentUser user, TablePageRequest request) {
        String userId = user.getUserId();
        long startTime = System.currentTimeMillis();
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        Map<String, TbMetaTableUserAuthDO> businessUserAuthMap = new HashMap<>();
        if(!isAdmin){
            List<TbMetaTableUserAuthDO> userAuthMetaTableList = tbMetaTableUserAuthDAO.getUserAuthMetaTableList(enterpriseId, userId);
            if(CollectionUtils.isEmpty(userAuthMetaTableList)){
                return new PageInfo();
            }
            businessUserAuthMap = userAuthMetaTableList.stream().collect(Collectors.toMap(o->o.getBusinessId() + Constants.MOSAICS + o.getUserId(), x -> x));
            List<String> authMetaTableIds = userAuthMetaTableList.stream().map(TbMetaTableUserAuthDO::getBusinessId).distinct().collect(Collectors.toList());
            request.setAuthTableIds(authMetaTableIds);
        }
        String tableType = request.getTableType();
        List<String> tableTypeList = new ArrayList<>();
        if (StringUtils.isNotBlank(tableType)) {
            tableTypeList = Arrays.asList(tableType.split(","));
        }
        PageHelper.startPage(request.getPageNumber(), request.getPageSize());
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectListV2(enterpriseId, request);
        log.info("获取检查表2：{}", System.currentTimeMillis() - startTime);
        List<Long> standardIdList = null;
        List<Long> defIdList = null;
        List<Long> idList = null;
        if (CollectionUtils.isNotEmpty(tableTypeList) && tableTypeList.contains(UnifyTaskDataTypeEnum.STANDARD.getCode())) {
            standardIdList = tbMetaTableDOS.stream().filter(x -> !MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                    .map(TbMetaTableDO::getId).collect(Collectors.toList());
            defIdList = tbMetaTableDOS.stream().filter(x -> MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                    .map(TbMetaTableDO::getId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(tableTypeList) && tableTypeList.contains(UnifyTaskDataTypeEnum.TB_DISPLAY.getCode())) {
            idList = tbMetaTableDOS.stream().map(TbMetaTableDO::getId).collect(Collectors.toList());
        }
        Map<Long, List<TbMetaDefTableColumnDO>> defMap = new HashMap<>(16);
        Map<Long, List<TbMetaStaTableColumnDO>> staMap = new HashMap<>(16);
        Map<Long, List<TbMetaDisplayTableColumnDO>> tbDisplayMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(standardIdList)) {
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, standardIdList);
            staMap = tbMetaStaTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));
        }
        if (CollectionUtils.isNotEmpty(defIdList)) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defIdList);
            defMap = tbMetaDefTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
        }
        if (StringUtils.isNotBlank(tableType) && UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(tableType)) {
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.selectAllColumnListByTableIdList(enterpriseId, idList);
            tbDisplayMap = tbMetaDisplayTableColumnDOList.stream().filter(date -> date.getCheckType().equals(Constants.INDEX_ZERO)).collect(Collectors.groupingBy(TbMetaDisplayTableColumnDO::getMetaTableId));
        }
        log.info("获取检查表3：{}", System.currentTimeMillis() - startTime);
        List<TbMetaTableVO> tbMetaTableVOList = getTbMetaTableVOList(enterpriseId, userId, isAdmin, tbMetaTableDOS, defMap, staMap, tbDisplayMap, businessUserAuthMap);
        PageInfo pageInfo = new PageInfo(tbMetaTableDOS);
        pageInfo.setList(tbMetaTableVOList);
        log.info("获取检查表总耗时：{}", System.currentTimeMillis()-startTime);
        return pageInfo;
    }

    @Override
    public PageInfo getTableListByResultViewV2(String enterpriseId, CurrentUser user, TablePageRequest request) {
        String userId = user.getUserId();
        long startTime = System.currentTimeMillis();
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        Map<String, TbMetaTableUserAuthDO> businessUserAuthMap = new HashMap<>();
        if(!isAdmin){
            List<TbMetaTableUserAuthDO> userAuthMetaTableList = tbMetaTableUserAuthDAO.getUserAuthViewMetaTableList(enterpriseId, userId);
            if(CollectionUtils.isEmpty(userAuthMetaTableList)){
                return new PageInfo();
            }
            businessUserAuthMap = userAuthMetaTableList.stream().collect(Collectors.toMap(o->o.getBusinessId() + Constants.MOSAICS + o.getUserId(), x -> x));
            List<String> authMetaTableIds = userAuthMetaTableList.stream().map(TbMetaTableUserAuthDO::getBusinessId).distinct().collect(Collectors.toList());
            request.setAuthTableIds(authMetaTableIds);
        }
        String tableType = request.getTableType();
        List<String> tableTypeList = new ArrayList<>();
        if (StringUtils.isNotBlank(tableType)) {
            tableTypeList = Arrays.asList(tableType.split(","));
        }
        if(request.getPageNumber() != null && request.getPageNumber() != 0){
            PageHelper.startPage(request.getPageNumber(), request.getPageSize());
        }
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectListV2(enterpriseId, request);
        log.info("获取检查表2：{}", System.currentTimeMillis() - startTime);
        List<Long> standardIdList = null;
        List<Long> defIdList = null;
        List<Long> idList = null;
        if (CollectionUtils.isNotEmpty(tableTypeList) && tableTypeList.contains(UnifyTaskDataTypeEnum.STANDARD.getCode())) {
            standardIdList = tbMetaTableDOS.stream().filter(x -> !MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                    .map(TbMetaTableDO::getId).collect(Collectors.toList());
            defIdList = tbMetaTableDOS.stream().filter(x -> MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(x.getTableProperty()))
                    .map(TbMetaTableDO::getId).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(tableTypeList) && tableTypeList.contains(UnifyTaskDataTypeEnum.TB_DISPLAY.getCode())) {
            idList = tbMetaTableDOS.stream().map(TbMetaTableDO::getId).collect(Collectors.toList());
        }
        Map<Long, List<TbMetaDefTableColumnDO>> defMap = new HashMap<>(16);
        Map<Long, List<TbMetaStaTableColumnDO>> staMap = new HashMap<>(16);
        Map<Long, List<TbMetaDisplayTableColumnDO>> tbDisplayMap = new HashMap<>(16);
        if (CollectionUtils.isNotEmpty(standardIdList)) {
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, standardIdList);
            staMap = tbMetaStaTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getMetaTableId));
        }
        if (CollectionUtils.isNotEmpty(defIdList)) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, defIdList);
            defMap = tbMetaDefTableColumnDOList.stream().collect(Collectors.groupingBy(TbMetaDefTableColumnDO::getMetaTableId));
        }
        if (StringUtils.isNotBlank(tableType) && UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(tableType)) {
            List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.selectAllColumnListByTableIdList(enterpriseId, idList);
            tbDisplayMap = tbMetaDisplayTableColumnDOList.stream().filter(date -> date.getCheckType().equals(Constants.INDEX_ZERO)).collect(Collectors.groupingBy(TbMetaDisplayTableColumnDO::getMetaTableId));
        }
        log.info("获取检查表3：{}", System.currentTimeMillis() - startTime);
        List<TbMetaTableVO> tbMetaTableVOList = getTbMetaTableVOList(enterpriseId, userId, isAdmin, tbMetaTableDOS, defMap, staMap, tbDisplayMap, businessUserAuthMap);
        PageInfo pageInfo = new PageInfo(tbMetaTableDOS);
        pageInfo.setList(tbMetaTableVOList);
        log.info("获取检查表总耗时：{}", System.currentTimeMillis()-startTime);
        return pageInfo;
    }

    private List<TbMetaTableVO> getTbMetaTableVOList(String enterpriseId, String currentUserId, Boolean isAdmin, List<TbMetaTableDO> tbMetaTableDOS, Map<Long, List<TbMetaDefTableColumnDO>> defMap, Map<Long, List<TbMetaStaTableColumnDO>> staMap, Map<Long, List<TbMetaDisplayTableColumnDO>> tbDisplayMap, Map<String, TbMetaTableUserAuthDO> businessUserAuthMap){
        List<TbMetaTableVO> tbMetaTableVOList = new ArrayList<>();
        List<String> createUserIds = tbMetaTableDOS.stream().map(TbMetaTableDO::getCreateUserId).distinct().collect(Collectors.toList());
        List<String> onJobCreateUserIds = enterpriseUserDao.selectUsersByDingUserIds(enterpriseId, createUserIds);
        for (TbMetaTableDO pdo : tbMetaTableDOS) {
            TbMetaTableVO item = TbMetaTableVO.convertVO(pdo);
            item.setCreatorIsActive(CollectionUtils.isNotEmpty(onJobCreateUserIds) && onJobCreateUserIds.contains(pdo.getCreateUserId()));
            if (StringUtils.isNotBlank(pdo.getTableType()) && UnifyTaskDataTypeEnum.STANDARD.getCode().equals(pdo.getTableType())) {
                Integer columnCount = staMap.get(pdo.getId()) == null ? 0 : staMap.get(pdo.getId()).size();
                //检查项不为空，筛选出检查项分类数
                if (!Constants.INDEX_ZERO.equals(columnCount)) {
                    Set<String> categoryList = staMap.get(pdo.getId()).stream().map(TbMetaStaTableColumnDO::getCategoryName).collect(Collectors.toSet());
                    item.setColumnCategoryCount(categoryList.size());
                }
                item.setColumnCount(columnCount);
            }
            if (TableTypeUtil.isUserDefinedTable(pdo.getTableProperty(), pdo.getTableType())) {
                Integer columnCount = defMap.get(pdo.getId()) == null ? 0 : defMap.get(pdo.getId()).size();
                item.setColumnCount(columnCount);
            }
            if (StringUtils.isNotBlank(pdo.getTableType()) && UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(pdo.getTableType())) {
                Integer columnCount = tbDisplayMap.get(pdo.getId()) == null ? 0 : tbDisplayMap.get(pdo.getId()).size();
                item.setColumnCount(columnCount);
            }
            if (currentUserId.equals(item.getCreateUserId())) {
                item.setEditFlag(true);
            }else if(isAdmin){
                item.setEditFlag(true);
            }else{
                //共同编辑人列表
                TbMetaTableUserAuthDO userAuth = businessUserAuthMap.get(pdo.getId() + Constants.MOSAICS + currentUserId);
                TbMetaTableUserAuthDO allUseAuth = businessUserAuthMap.get(pdo.getId() + Constants.MOSAICS + "all_user_id");
                boolean allUserEditAuth = Optional.ofNullable(allUseAuth).map(TbMetaTableUserAuthDO::getEditAuth).orElse(false);
                boolean userEditAuth = Optional.ofNullable(userAuth).map(TbMetaTableUserAuthDO::getEditAuth).orElse(false);
                item.setEditFlag(userEditAuth || allUserEditAuth);
            }
            tbMetaTableVOList.add(item);
        }
        return tbMetaTableVOList;
    }

    @Override
    public TbDetailStaVO getDetailById(String enterpriseId, Long id) {
        TbDetailStaVO result = new TbDetailStaVO();
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, id);

        if (tbMetaTableDO != null && !TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType())) {
            result.setTable(tbMetaTableDO);
            List<TbMetaStaTableColumnDO> staDOList = metaDataService.getTableColumn(enterpriseId, Arrays.asList(id), Boolean.FALSE);
            List<TbMetaStaColumnVO> staVOList = new ArrayList<>();
            getMateStaColumnVOByDO(enterpriseId, staDOList, staVOList);
            result.setColumnList(staVOList);
            List<EnterpriseUserDO> userList = new ArrayList<>();
            List<EnterpriseUserDO> resultUserList = new ArrayList<>();
            String shareGroup = tbMetaTableDO.getShareGroup();
            String resultShareGroup = tbMetaTableDO.getResultShareGroup();
            if (StringUtils.isNotBlank(shareGroup)) {
                userList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(shareGroup.split(",")));
            }
            if (StringUtils.isNotBlank(resultShareGroup)) {
                resultUserList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(resultShareGroup.split(",")));
            }
            result.setUserList(userList);
            result.setResultUserList(resultUserList);
            result.setUsePersonInfo(tbMetaTableDO.getUsePersonInfo());
            result.setUseRange(tbMetaTableDO.getUseRange());
            result.setResultViewRange(tbMetaTableDO.getResultViewRange());
            result.setResultViewPersonInfo(tbMetaTableDO.getResultViewPersonInfo());
            //共同编辑人列表
            List<PersonDTO> personDTOList = new ArrayList<>();
            result.setCommonEditUserList(personDTOList);
        }
        return result;
    }

    @Override
    public MetaStaTableVO getMetaTableDetail(String enterpriseId, Long id, String userId, Boolean isFilterFreezeColumn) {
        // 检查表
        MetaStaTableVO result = new MetaStaTableVO();
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, id);
        if (tbMetaTableDO == null) {
            return null;
        }
        BeanUtils.copyProperties(tbMetaTableDO, result);
        Boolean isDefine = TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType());
        if (isDefine) {
            List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, id);
            result.setDefColumnList(tbMetaDefTableColumnDOList);
            return result;
        }
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByMetaTableId(enterpriseId, id);
        // 结果项
        List<TbMetaColumnResultDTO> columnResultDTOList = getMetaColumnResultList(enterpriseId, columnResultDOList);
        Map<Long, List<TbMetaColumnResultDTO>> columnIdResultDOsMap =
                columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));
        if (isFilterFreezeColumn == null) {
            isFilterFreezeColumn = Boolean.FALSE;
        }
        // 检查项
        List<TbMetaStaTableColumnDO> staColumnDOList =
                tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(id), isFilterFreezeColumn);

        //查询场景列表,判断场景是否删除
        List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(enterpriseId);
        Map<Long, StoreSceneDo> StoreSceneMap = storeSceneList.stream().collect(Collectors.toMap(StoreSceneDo::getId, t -> t));
        staColumnDOList.forEach(data -> {
            data.setStoreSceneIsDelete(false);
            StoreSceneDo tempStoreSceneDo = StoreSceneMap.getOrDefault(data.getStoreSceneId(), StoreSceneDo.builder().name("").build());
            data.setStoreSceneName(tempStoreSceneDo.getName());
            if (!StoreSceneMap.containsKey(data.getStoreSceneId())) {
                data.setStoreSceneIsDelete(true);
            }
        });


        List<Long> sopIdList = staColumnDOList.stream().map(TbMetaStaTableColumnDO::getSopId).collect(Collectors.toList());
        List<TaskSopVO> taskSopVOList = taskSopService.listByIdList(enterpriseId, sopIdList);
        Map<Long, TaskSopVO> taskSopVOMap = taskSopVOList.stream().collect(Collectors.toMap(TaskSopVO::getId, t -> t));// t->t 表示对象本身

        //不合格原因
        List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByMetaTableId(enterpriseId, id);
        Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnReasonDTOList)) {
            columnIdReasonMap =
                    columnReasonDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId));
        }
        //申诉快捷項
        List<TbMetaColumnAppealDTO> appealDTOList = metaColumnAppealDao.getListByMetaTableId(enterpriseId, id);
        Map<Long, List<TbMetaColumnAppealDTO>> appealDTOListMap = ListUtils.emptyIfNull(appealDTOList).stream().collect(Collectors.groupingBy(TbMetaColumnAppealDTO::getMetaColumnId));

        // 设置名字数据
        setNameData(enterpriseId, staColumnDOList);
        Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
        Set<String> aiModelCodes = CollStreamUtil.toSet(staColumnDOList, TbMetaStaTableColumnDO::getAiModel);
        Map<String, String> aiModelMap = aiModelLibraryService.getModelNameMapByCodes(new ArrayList<>(aiModelCodes));
        List<MetaStaColumnVO> staColumnVOList = staColumnDOList.stream().map(a -> {
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(a, metaStaColumnVO);
            //如果是采集项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())) {
                metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
            }

            metaStaColumnVO.setColumnTypeName(MetaColumnTypeEnum.getColumnTypeName(metaStaColumnVO.getColumnType()));
            // 结果项
            metaStaColumnVO.setColumnResultList(columnIdResultDOsMap.getOrDefault(a.getId(), new ArrayList<>()));
            // sop文档对象
            metaStaColumnVO.setTaskSopVO(taskSopVOMap.get(a.getSopId()));
            //酷学院课程信息
            if (StringUtils.isNoneEmpty(a.getCoolCourse())) {
                metaStaColumnVO.setCoolCourseVO(JSON.parseObject(a.getCoolCourse(), CoolCourseVO.class));
            }
            //免费课程信息
            if (StringUtils.isNoneEmpty(a.getFreeCourse())) {
                metaStaColumnVO.setFreeCourseVO(JSON.parseObject(a.getFreeCourse(), CoolCourseVO.class));
            }
            metaStaColumnVO.setCoolCourse(a.getCoolCourse());
            metaStaColumnVO.setFreeCourse(a.getFreeCourse());
            metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
            metaStaColumnVO.setColumnAppealList(appealDTOListMap.get(a.getId()));
            JSONObject extendInfo = JSONObject.parseObject(a.getExtendInfo());
            if (Objects.nonNull(extendInfo)) {
                metaStaColumnVO.setDescRequired(extendInfo.getBoolean(Constants.TableColumn.DESC_REQUIRED));
                metaStaColumnVO.setAutoQuestionTaskValidity(extendInfo.getInteger(Constants.TableColumn.AUTO_QUESTION_TASK_VALIDITY));
                metaStaColumnVO.setIsSetAutoQuestionTaskValidity(extendInfo.getBoolean(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY));
                metaStaColumnVO.setMinCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MIN_CHECK_PIC_NUM));
                metaStaColumnVO.setMaxCheckPicNum(extendInfo.getInteger(Constants.TableColumn.MAX_CHECK_PIC_NUM));
            }
            metaStaColumnVO.setIsAiCheck(a.getIsAiCheck());
            metaStaColumnVO.setAiModel(a.getAiModel());
            metaStaColumnVO.setAiModelName(aiModelMap.get(a.getAiModel()));
            return metaStaColumnVO;
        }).collect(Collectors.toList());

        //如果检查类别下检查项数量为0,不显示类别
        //不为0的类别但无排序
        List<String> oldOrderName = staColumnVOList.stream().map(MetaStaColumnVO::getCategoryName).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(oldOrderName)) {
            //排序但所有类别
            List<String> orderName = JSONObject.parseArray(tbMetaTableDO.getCategoryNameList(), String.class);
            List<Object> newName = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(orderName)) {
                newName = orderName.stream().filter(oldOrderName::contains).collect(Collectors.toList());
            }
            result.setCategoryNameList(JSONObject.toJSONString(newName));
        }
        result.setStaColumnList(staColumnVOList);

        // 可视范围
        List<EnterpriseUserDO> userList = new ArrayList<>();
        String shareGroup = tbMetaTableDO.getShareGroup();
        if (StringUtils.isNotBlank(shareGroup)) {
            userList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(shareGroup.split(",")));
        }
        List<EnterpriseUserDO> resultUserList = new ArrayList<>();
        String resultShareGroup = tbMetaTableDO.getResultShareGroup();
        if (StringUtils.isNotBlank(resultShareGroup)) {
            resultUserList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(resultShareGroup.split(",")));
        }
        result.setUserList(userList);
        result.setResultUserList(resultUserList);

        result.setUsePersonInfo(tbMetaTableDO.getUsePersonInfo());
        result.setUseRange(tbMetaTableDO.getUseRange());
        result.setResultViewRange(tbMetaTableDO.getResultViewRange());
        result.setResultViewPersonInfo(tbMetaTableDO.getResultViewPersonInfo());
        result.setCommonEditPersonInfo(tbMetaTableDO.getCommonEditPersonInfo());
        result.setEditFlag(false);
        if (userId.equals(tbMetaTableDO.getCreateUserId())) {
            result.setEditFlag(true);
        }
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if (isAdmin) {
            result.setEditFlag(true);
        }
        if(!result.getEditFlag()){
            List<TbMetaTableUserAuthDO> tableAuth = tbMetaTableUserAuthDAO.getTableAuth(enterpriseId, userId, Collections.singletonList(tbMetaTableDO.getId()));
            List<TbMetaTableUserAuthDO> editAuth = ListUtils.emptyIfNull(tableAuth).stream().filter(TbMetaTableUserAuthDO::getEditAuth).collect(Collectors.toList());
            result.setEditFlag(CollectionUtils.isNotEmpty(editAuth));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TbMetaTableDO saveSta(String enterpriseId, CurrentUser user, TbMetaStaTableDTO metaStaTableDTO) {
        if (tbMetaTableMapper.countCheckTableByName(enterpriseId, metaStaTableDTO.getTableName()) > 0) {
            throw new ServiceException(ErrorCodeEnum.TABLE_REPEAT);
        }
        //如果是权重表，校验权重之和是否是100
        if (MetaTablePropertyEnum.WEIGHT_TABLE.getCode().equals(metaStaTableDTO.getTableProperty())) {
            CheckWeightTablePrecent(metaStaTableDTO.getStaColumnDTOList());
        }
        Integer isAiCheck = metaStaTableDTO.getStaColumnDTOList().stream().anyMatch(a -> Objects.equals(a.getIsAiCheck(), YesOrNoEnum.YES.getCode())) ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode();
        //按照规则计算分值
        AbstractColumnObserver.getColumnMaxScoreMap(metaStaTableDTO);
        // 1.插入检查表数据
        PatrolMetaDTO patrolMetaDTO = getPatrolMetaDOFromDTO(enterpriseId, user.getUserId(), user, metaStaTableDTO);
        TbMetaTableDO tbMetaTableDO = patrolMetaDTO.getTbMetaTableDO();
        TbMetaTableDO leafTypeDO = tbMetaTableMapper.selectById(enterpriseId, metaStaTableDTO.getPid());
        tbMetaTableDO.setSopPath(leafTypeDO.getSopPath() + leafTypeDO.getId() + "/");
        tbMetaTableDO.setSopType(Constants.CHILD);
        tbMetaTableDO.setIsAiCheck(isAiCheck);
        tbMetaTableMapper.insertSelective(enterpriseId, tbMetaTableDO);
        tbMetaTableDO.setEditTime(new Date(System.currentTimeMillis()));

        // 2.循环插入检查项
        insertStaColumnList(enterpriseId, metaStaTableDTO.getStaColumnDTOList(), tbMetaTableDO, Constants.INDEX_ONE);
        String requestId = MDC.get(Constants.REQUEST_ID);
        CompletableFuture.runAsync(()->{asyncUpdateMetaTableUser(enterpriseId, patrolMetaDTO,  user.getDbName(), requestId);});
        return tbMetaTableDO;
    }

    /**
     * 插入检查项
     */
    private void insertStaColumnList(String enterpriseId, List<TbMetaStaColumnDTO> staColumnDTOList, TbMetaTableDO tbMetaTableDO, Integer orderNum) {
        AtomicInteger sortNum = new AtomicInteger(orderNum);
        //项的分值计算
        for (TbMetaStaColumnDTO staColumnDTO : staColumnDTOList) {
            List<TbMetaColumnResultDTO> columnResultDTOList = staColumnDTO.getColumnResultDTOList();
            // 2.1 插入检查项
            TbMetaStaTableColumnDO staColumnDO = getStaColumnDOFromDTO(staColumnDTO, tbMetaTableDO);
            staColumnDO.setOrderNum(sortNum.getAndIncrement());
            tbMetaStaTableColumnMapper.insert(enterpriseId, staColumnDO);
            //采集项 无结果项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(staColumnDTO.getColumnType())) {
                continue;
            }
            // 2.2 插入结果项，没有不插入
            if (CollectionUtils.isEmpty(columnResultDTOList)) {
                throw new ServiceException(ErrorCodeEnum.COLUMN_RESULT_ISNULL);
            }
            //检查项结果最少1条最多10条
            if (columnResultDTOList.size() < Constants.INDEX_ONE || columnResultDTOList.size() > Constants.INDEX_TEN) {
                throw new ServiceException(ErrorCodeEnum.COLUMN_RESULT_LIMIT);
            }
            List<TbMetaColumnResultDO> columnResultDOList = columnResultDTOList.stream()
                    .map(columnResultDTO -> getColumnResultDOFromDTO(columnResultDTO, staColumnDO))
                    .collect(Collectors.toList());
            tbMetaColumnResultMapper.batchInsert(enterpriseId, columnResultDOList);

            List<TbMetaColumnReasonDO> reasonDOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(staColumnDTO.getColumnReasonList())) {
                staColumnDTO.getColumnReasonList().forEach(resultDTO -> {
                    TbMetaColumnReasonDO metaColumnReasonDO = new TbMetaColumnReasonDO();
                    metaColumnReasonDO.setMetaColumnId(staColumnDO.getId());
                    metaColumnReasonDO.setMetaTableId(staColumnDO.getMetaTableId());
                    metaColumnReasonDO.setReasonName(resultDTO.getReasonName());
                    metaColumnReasonDO.setMappingResult(resultDTO.getMappingResult());
                    reasonDOList.add(metaColumnReasonDO);
                });
            }
            if (CollectionUtils.isNotEmpty(reasonDOList)) {
                ListUtils.partition(reasonDOList, Constants.BATCH_INSERT_COUNT).forEach(list ->
                        metaColumnReasonDao.batchInsert(enterpriseId, list));
            }
            //申诉
            List<TbMetaColumnAppealDO> appealDOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(staColumnDTO.getColumnAppealList())) {
                staColumnDTO.getColumnAppealList().forEach(appealDTO -> {
                    TbMetaColumnAppealDO appealDO = new TbMetaColumnAppealDO();
                    appealDO.setMetaColumnId(staColumnDO.getId());
                    appealDO.setMetaTableId(staColumnDO.getMetaTableId());
                    appealDO.setAppealName(appealDTO.getAppealName());
                    appealDOList.add(appealDO);
                });
            }
            if (CollectionUtils.isNotEmpty(appealDOList)) {
                ListUtils.partition(appealDOList, Constants.BATCH_INSERT_COUNT).forEach(list ->
                        metaColumnAppealDao.batchInsert(enterpriseId, list));
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public TbMetaTableDO updateSta(String enterpriseId, CurrentUser user, TbMetaStaTableDTO metaStaTableDTO) {
        //如果是权重表，校验权重
        if (MetaTablePropertyEnum.WEIGHT_TABLE.getCode().equals(metaStaTableDTO.getTableProperty())) {
            CheckWeightTablePrecent(metaStaTableDTO.getStaColumnDTOList());
        }
        List<TbMetaStaColumnDTO> staColumnDTOList = metaStaTableDTO.getStaColumnDTOList();
        if (CollectionUtils.isNotEmpty(staColumnDTOList)) {
            //更新的时候，，表内项至少有一个没有被冻结，，全部冻结，不支持更新
            List<TbMetaStaColumnDTO> notFreezeColumnList = staColumnDTOList.stream().filter(x -> !x.getStatus()).collect(Collectors.toList());
            if (notFreezeColumnList.size() < Constants.INDEX_ONE) {
                throw new ServiceException(ErrorCodeEnum.CHECK_TABLE_COLUMN_COUNT);
            }
        }
        //按照规则计算分值
        AbstractColumnObserver.getColumnMaxScoreMap(metaStaTableDTO);
        // 1.判断当前检查表是否已锁定
        TbMetaTableDO metaDO = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, metaStaTableDTO.getId());
        // 2.修改检查表
        PatrolMetaDTO patrolMetaDTO = getPatrolMetaDOFromDTO(enterpriseId, metaDO.getCreateUserId(), user, metaStaTableDTO);
        TbMetaTableDO newMetaDO = patrolMetaDTO.getTbMetaTableDO();
        newMetaDO.setCreateUserName(null);
        newMetaDO.setCreateUserId(null);
        newMetaDO.setId(metaStaTableDTO.getId());
        newMetaDO.setEditTime(new Date(System.currentTimeMillis()));
        newMetaDO.setStoreSceneId(metaStaTableDTO.getStoreSceneId());
        newMetaDO.setCopyModify(metaStaTableDTO.getCopyModify());
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        //只有管理员和创建人可以修改使用人和共同编辑人信息
        if (!isAdmin && !user.getUserId().equals(metaDO.getCreateUserId())) {
            newMetaDO.setUseRange(null);
            newMetaDO.setUsePersonInfo(null);
            newMetaDO.setCommonEditPersonInfo(null);
            newMetaDO.setResultViewRange(null);
            newMetaDO.setResultViewPersonInfo(null);
        }
        Integer isAiCheck = metaStaTableDTO.getStaColumnDTOList().stream().anyMatch(a -> Objects.equals(a.getIsAiCheck(), YesOrNoEnum.YES.getCode())) ? YesOrNoEnum.YES.getCode() : YesOrNoEnum.NO.getCode();
        newMetaDO.setIsAiCheck(isAiCheck);
        tbMetaTableMapper.updateByPrimaryKeySelective(enterpriseId, newMetaDO);
        if (metaDO.getLocked() == 1) {
            //优化，检查表运用到任务之后 可以继续添加检查项
            //之前应用到任务的检查项 继续更新
            List<TbMetaStaTableColumnDO> tbMetaStaTableColumnDOS = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Arrays.asList(metaStaTableDTO.getId()), Boolean.FALSE);
            List<Long> metaColumnIds = tbMetaStaTableColumnDOS.stream().map(TbMetaStaTableColumnDO::getId).collect(Collectors.toList());
            List<TbMetaStaColumnDTO> oldStaColumnList = metaStaTableDTO.getStaColumnDTOList().stream().filter(x -> metaColumnIds.contains(x.getId())).collect(Collectors.toList());
            updateMetaColumn(enterpriseId, metaStaTableDTO, newMetaDO, oldStaColumnList);
            //新增的检查项 插入
            List<TbMetaStaColumnDTO> addStaColumnList = metaStaTableDTO.getStaColumnDTOList().stream().filter(x -> !metaColumnIds.contains(x.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(addStaColumnList)) {
                //插入
                insertStaColumnList(enterpriseId, addStaColumnList, newMetaDO, oldStaColumnList.size() + Constants.INDEX_ONE);
            }
        } else {
            //物理删除旧的检查项（已锁定的检查表软删除）
            tbMetaColumnResultMapper.deleteByMetaTableIdList(enterpriseId,
                    Collections.singletonList(metaStaTableDTO.getId()));
            tbMetaStaTableColumnMapper.deleteAbsoluteByTableIdList(enterpriseId,
                    Collections.singletonList(metaStaTableDTO.getId()));
            metaColumnReasonDao.deleteByMetaTableId(enterpriseId, metaStaTableDTO.getId());
            // 3.循环插入检查项
            insertStaColumnList(enterpriseId, metaStaTableDTO.getStaColumnDTOList(), newMetaDO, Constants.INDEX_ONE);
        }
        String requestId = MDC.get(Constants.REQUEST_ID);
        CompletableFuture.runAsync(()->{asyncUpdateMetaTableUser(enterpriseId, patrolMetaDTO,  user.getDbName(), requestId);});
        return newMetaDO;
    }

    /**
     * 校验权重表 权重百分比是否是100
     *
     * @param staColumnDTOList
     */
    public void CheckWeightTablePrecent(List<TbMetaStaColumnDTO> staColumnDTOList) {
        BigDecimal weight = new BigDecimal(Constants.ZERO_STR);
        for (TbMetaStaColumnDTO staColumnDTO : staColumnDTOList) {
            //只计算非冻结的项权重
            if (staColumnDTO.getWeightPercent() != null && staColumnDTO.getStatus() != null && !staColumnDTO.getStatus()) {
                weight = weight.add(staColumnDTO.getWeightPercent());
            }
        }
        if (!weight.equals(new BigDecimal(Constants.ONE_HUNDRED))) {
            throw new ServiceException(ErrorCodeEnum.WEIGHT_PERCENT_TABLE);
        }
    }


    /**
     * 更新检查表中检查项内容
     *
     * @param enterpriseId
     * @param metaStaTableDTO
     * @param tbMetaTableDO
     * @param staColumnDTOList
     */
    private void updateMetaColumn(String enterpriseId, TbMetaStaTableDTO metaStaTableDTO, TbMetaTableDO tbMetaTableDO, List<TbMetaStaColumnDTO> staColumnDTOList) {
        List<TbMetaStaTableColumnDO> columnDOList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(metaStaTableDTO.getId()), Boolean.FALSE);
        Set<Long> columnSet = columnDOList.stream().map(data -> data.getId()).collect(Collectors.toSet());
        List<TbMetaColumnResultDO> resultDOS = tbMetaColumnResultMapper.selectByMetaTableId(enterpriseId, metaStaTableDTO.getId());
        List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByMetaTableId(enterpriseId, metaStaTableDTO.getId());
        Map<Long, List<Long>> resultIdMap = ListUtils.emptyIfNull(columnReasonDTOList).stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId,
                Collectors.mapping(TbMetaColumnReasonDTO::getId, Collectors.toList())));
        Set<Long> resultSet = resultDOS.stream().map(data -> data.getId()).collect(Collectors.toSet());
        AtomicInteger sortNum = new AtomicInteger(1);
        staColumnDTOList.stream().filter(data -> columnSet.contains(data.getId())).forEach(staColumnDTO -> {
            List<TbMetaColumnResultDTO> columnResultDTOList = staColumnDTO.getColumnResultDTOList();
            // 2.1 插入检查项
            TbMetaStaTableColumnDO staColumnDO = getStaColumnDOFromDTO(staColumnDTO, tbMetaTableDO);
            staColumnDO.setId(staColumnDTO.getId());
            staColumnDO.setOrderNum(sortNum.getAndIncrement());
            staColumnDO.setStoreSceneId(staColumnDTO.getStoreSceneId());
            tbMetaStaTableColumnMapper.updateByPrimaryKeySelective(enterpriseId, staColumnDO);

            if (CollectionUtils.isNotEmpty(columnResultDTOList)) {
                List<TbMetaColumnResultDO> columnResultDOList = columnResultDTOList.stream().filter(data -> resultSet.contains(data.getId()))
                        .map(columnResultDTO -> {
                            TbMetaColumnResultDO resultDO = getColumnResultDOFromDTO(columnResultDTO, staColumnDO);
                            resultDO.setId(columnResultDTO.getId());
                            return resultDO;
                        })
                        .collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(columnResultDOList)) {
                    tbMetaColumnResultMapper.updateResultByList(enterpriseId, columnResultDOList);
                }
            }

            List<Long> reasonIdList = resultIdMap.getOrDefault(staColumnDO.getId(), new ArrayList<>());
            if (CollectionUtils.isNotEmpty(staColumnDTO.getColumnReasonList())) {
                staColumnDTO.getColumnReasonList().forEach(columnReason -> {
                    TbMetaColumnReasonDO reasonDO = new TbMetaColumnReasonDO();
                    reasonDO.setReasonName(columnReason.getReasonName());
                    if (columnReason.getId() == null) {
                        reasonDO.setMetaColumnId(staColumnDO.getId());
                        reasonDO.setMetaTableId(staColumnDO.getMetaTableId());
                        reasonDO.setDeleted(false);
                        reasonDO.setMappingResult(columnReason.getMappingResult());
                        metaColumnReasonDao.insertSelective(enterpriseId, reasonDO);
                    } else {
                        reasonDO.setId(columnReason.getId());
                        reasonIdList.remove(columnReason.getId());
                        metaColumnReasonDao.updateByPrimaryKeySelective(enterpriseId, reasonDO);
                    }
                });
                if (CollectionUtils.isNotEmpty(reasonIdList)) {
                    metaColumnReasonDao.logicallyDeleteByIds(enterpriseId, reasonIdList);
                }
            } else {
                metaColumnReasonDao.deleteByColumnId(enterpriseId, staColumnDO.getId());
            }
        });

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TbMetaTableDO copySta(String enterpriseId, CurrentUser user, Long metaTableId) {

        //检查表详情
        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, metaTableId);
        Boolean copyModify = metaTableDO.getCopyModify();
        metaTableDO.setCreateUserId(user.getUserId());
        metaTableDO.setCreateUserName(user.getName());
        metaTableDO.setTableName(metaTableDO.getTableName() + UUIDUtils.get8UUID());
        metaTableDO.setEditUserId(user.getUserId());
        metaTableDO.setEditUserName(user.getName());
        metaTableDO.setLocked(0);
        metaTableDO.setTopTime(null);
        metaTableDO.setId(null);
        metaTableDO.setCopyModify(true);
        metaTableDO.setCreateTime(new Date());
        metaTableDO.setEditTime(new Date());
        tbMetaTableMapper.insertSelective(enterpriseId, metaTableDO);
        updateMetaTableAuth(enterpriseId, metaTableDO);
        List<TbMetaStaTableColumnDO> staDOList =
                metaDataService.getTableColumn(enterpriseId, Collections.singletonList(metaTableId), Boolean.TRUE);
        if (CollectionUtils.isEmpty(staDOList)) {
            return metaTableDO;
        }
        for (TbMetaStaTableColumnDO staDO : staDOList) {
            Long staColumnId = staDO.getId();
            staDO.setMetaTableId(metaTableDO.getId());
            staDO.setCreateUserName(user.getName());
            staDO.setCreateUserId(user.getUserId());
            staDO.setEditUserId(user.getUserId());
            staDO.setEditUserName(user.getName());
            staDO.setId(null);
            staDO.setCanModify(true);
            if (copyModify != null && !copyModify) {
                staDO.setCanModify(false);
            }
            tbMetaStaTableColumnMapper.insert(enterpriseId, staDO);
            List<TbMetaColumnResultDO> columnResultDOList =
                    tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, Collections.singletonList(staColumnId));
            if (CollectionUtils.isEmpty(columnResultDOList)) {
                continue;
            }
            columnResultDOList.forEach(resultDO -> {
                resultDO.setMetaTableId(metaTableDO.getId());
                resultDO.setMetaColumnId(staDO.getId());
                resultDO.setCreateUserId(staDO.getCreateUserId());
                resultDO.setId(null);
            });
            tbMetaColumnResultMapper.batchInsert(enterpriseId, columnResultDOList);
            List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByColumnId(enterpriseId, staColumnId);
            List<TbMetaColumnReasonDO> reasonDOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(columnReasonDTOList)) {
                columnReasonDTOList.forEach(resultDTO -> {
                    TbMetaColumnReasonDO metaColumnReasonDO = new TbMetaColumnReasonDO();
                    metaColumnReasonDO.setMetaColumnId(staDO.getId());
                    metaColumnReasonDO.setMetaTableId(staDO.getMetaTableId());
                    metaColumnReasonDO.setReasonName(resultDTO.getReasonName());
                    metaColumnReasonDO.setCreateUserId(staDO.getCreateUserId());
                    metaColumnReasonDO.setMappingResult(resultDTO.getMappingResult());
                    reasonDOList.add(metaColumnReasonDO);
                });
                metaColumnReasonDao.batchInsert(enterpriseId, reasonDOList);
            }

            //申诉项
            List<TbMetaColumnAppealDTO> appealDTOList = metaColumnAppealDao.getListByColumnId(enterpriseId, staColumnId);
            List<TbMetaColumnAppealDO> appealDOList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(appealDTOList)) {
                appealDTOList.forEach(resultDTO -> {
                    TbMetaColumnAppealDO appealDO = new TbMetaColumnAppealDO();
                    appealDO.setMetaColumnId(staDO.getId());
                    appealDO.setMetaTableId(staDO.getMetaTableId());
                    appealDO.setAppealName(resultDTO.getAppealName());
                    appealDO.setCreateUserId(staDO.getCreateUserId());
                    appealDOList.add(appealDO);
                });
                metaColumnAppealDao.batchInsert(enterpriseId, appealDOList);
            }
        }
        return metaTableDO;
    }

    @Override
    @Transactional
    public boolean delSta(String enterpriseId, CurrentUser user, List<Long> metaTableIdList) {
        List<TbMetaTableDO> tableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);

        //软删除id
        List<Long> updateIdList = tableDOList.stream().filter(data -> data.getLocked() == 1).map(data -> data.getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updateIdList)) {
            tbMetaColumnResultMapper.updateDelByMetaTableIdList(enterpriseId, updateIdList);
            tbMetaStaTableColumnMapper.deleteColumnByMetaTableId(enterpriseId, updateIdList);
            tbMetaTableMapper.updateDelByIds(enterpriseId, updateIdList);
        }
        //物理删除id
        List<Long> deleteIdList = tableDOList.stream().filter(data -> data.getLocked() == 0).map(data -> data.getId()).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            tbMetaColumnResultMapper.deleteByMetaTableIdList(enterpriseId, deleteIdList);
            tbMetaStaTableColumnMapper.deleteAbsoluteByTableIdList(enterpriseId, deleteIdList);
            tbMetaTableMapper.deleteByIdList(enterpriseId, deleteIdList);
            // 删除人表权限映射
            tbMetaTableUserAuthDAO.deleteByBusinessIds(enterpriseId, CollStreamUtil.toList(deleteIdList, String::valueOf), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE, null);
        }
        return Boolean.TRUE;
    }

    @Override
    public List<TbMetaTableDO> getSimpleMetaTableList(String enterpriseId, CurrentUser user, String tableType, Integer limitNum) {
        if (user == null) {
            return new ArrayList<>();
        }
        if (limitNum == null) {
            limitNum = 100;
        }

        String userId = user.getUserId();
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        TablePageRequest tablePageRequest = new TablePageRequest();
        tablePageRequest.setTableType(tableType);
        if(!isAdmin){
            List<TbMetaTableUserAuthDO> userAuthMetaTableList = tbMetaTableUserAuthDAO.getUserAuthMetaTableList(enterpriseId, userId);
            if(CollectionUtils.isEmpty(userAuthMetaTableList)){
                return Lists.newArrayList();
            }
            List<String> authMetaTableIds = userAuthMetaTableList.stream().map(TbMetaTableUserAuthDO::getBusinessId).distinct().collect(Collectors.toList());
            tablePageRequest.setAuthTableIds(authMetaTableIds);
        }
        return tbMetaTableMapper.selectListV2(enterpriseId, tablePageRequest);
    }

    @Override
    public List<TbMetaTableRecordVO> getTableListBySubTaskId(String enterpriseId, Long subTaskId) {
        List<TbMetaTableRecordVO> result = new ArrayList<>();
        if (subTaskId == null) {
            return result;
        }
        //获取巡店记录
        TbPatrolStoreRecordDO storeRecordDO = tbPatrolStoreRecordMapper.getRecordBySubTaskId(enterpriseId, subTaskId, null);
        if (storeRecordDO == null) {
            return result;
        }
        List<Long> metaTableIdList = new ArrayList<>();
        Map<Long, Long> metaTableIdBusinessIdMap = new HashMap<>();
        Map<Long, Long> metaTableIdSubTaskIdMap = new HashMap<>();
        //获取meta检查表id集合、建立meta表id和巡店记录id的映射关系

        Long businessId = storeRecordDO.getId();
        Long metaTableId = storeRecordDO.getMetaTableId();
        Long tmpSubTaskId = storeRecordDO.getSubTaskId();
        metaTableIdList.add(metaTableId);
        metaTableIdBusinessIdMap.put(metaTableId, businessId);
        metaTableIdSubTaskIdMap.put(metaTableId, tmpSubTaskId);

        //获取检查表信息
        List<TbMetaTableDO> tableList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
        //转换成vo
        tableList.forEach(tbMetaTableDO -> {
            Long tableId = tbMetaTableDO.getId();
            TbMetaTableRecordVO tbMetaTableRecordVO = new TbMetaTableRecordVO();
            tbMetaTableRecordVO.setTable(tbMetaTableDO);
            tbMetaTableRecordVO.setBusinessId(metaTableIdBusinessIdMap.get(tableId));
            tbMetaTableRecordVO.setSubTaskId(metaTableIdSubTaskIdMap.get(tableId));
            result.add(tbMetaTableRecordVO);
        });
        return result;
    }

    @Override
    public Boolean isStaTable(String enterpriseId, Long metaTableId) {
        Integer count = tbMetaTableMapper.isStaTable(enterpriseId, metaTableId);
        return count > 0;
    }

    @Override
    public boolean isCreatorOrAdmin(String enterpriseId, String userId, List<Long> metaTableIds) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if (!isAdmin) {
            Integer count = tbMetaTableMapper.isCreator(enterpriseId, metaTableIds, userId);
            if (metaTableIds.size() != count) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public boolean isCommonEditUser(String enterpriseId, String userId, Long metaTableId) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if (isAdmin) {
            return true;
        }
        // 1.判断当前检查表是否已锁定
        List<String> editAuthTableIds = tbMetaTableUserAuthDAO.getEditAuthTableIds(enterpriseId, userId, Arrays.asList(metaTableId));
        return editAuthTableIds.contains(String.valueOf(metaTableId));
    }

    private TbMetaColumnResultDO getColumnResultDOFromDTO(TbMetaColumnResultDTO dto,
                                                          TbMetaStaTableColumnDO staColumnDO) {
        return TbMetaColumnResultDO.builder().metaTableId(staColumnDO.getMetaTableId())
                .metaColumnId(staColumnDO.getId()).createUserId(staColumnDO.getCreateUserId())
                .resultName(dto.getResultName() == null ? "" : dto.getResultName())
                .score(dto.getScore() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getScore()).money(dto.getMoney() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getMoney())
                .mappingResult(dto.getMappingResult() == null ? "" : dto.getMappingResult())
                .mustPic(dto.getMustPic() == null ? 0 : dto.getMustPic()).maxScore(dto.getMaxScore() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getMaxScore())
                .minScore(dto.getMinScore() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getMinScore()).orderNum(dto.getOrderNum() == null ? 0 : dto.getOrderNum())
                .scoreIsDouble(dto.getScoreIsDouble() == null ? Constants.ZERO : dto.getScoreIsDouble())
                .awardIsDouble(dto.getAwardIsDouble() == null ? Constants.ZERO : dto.getAwardIsDouble())
                .description(dto.getDescription())
                .extendInfo(dto.convertToExtendInfo()).build();
    }

    private TbMetaStaTableColumnDO getStaColumnDOFromDTO(TbMetaStaColumnDTO dto, TbMetaTableDO tbMetaTableDO) {
        TbMetaStaTableColumnDO staDO = new TbMetaStaTableColumnDO();
        BigDecimal awardMoney = dto.getAwardMoney() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getAwardMoney();
        staDO.setAwardMoney(awardMoney);
        String categoryName = dto.getCategoryName();
        categoryName = StringUtils.isBlank(categoryName) ? DEFAULT_CATEGORY : categoryName;
        staDO.setCategoryName(categoryName);
        staDO.setColumnName(dto.getColumnName());
        staDO.setCreateUserId(tbMetaTableDO.getEditUserId());
        staDO.setDeleted(false);
        staDO.setDescription(dto.getDescription() == null ? "" : dto.getDescription());
        staDO.setMetaTableId(tbMetaTableDO.getId());
        staDO.setOrderNum(dto.getOrderNum() == null ? 0 : dto.getOrderNum());
        BigDecimal punishMoney = dto.getPunishMoney() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getPunishMoney();
        staDO.setPunishMoney(punishMoney);
        staDO.setQuestionHandlerId(dto.getQuestionHandlerId() == null ? "" : dto.getQuestionHandlerId());
        staDO.setQuestionHandlerType(dto.getQuestionHandlerType() == null ? "" : dto.getQuestionHandlerType());
        staDO.setQuestionRecheckerId(dto.getQuestionRecheckerId() == null ? "" : dto.getQuestionRecheckerId());
        staDO.setQuestionRecheckerType(dto.getQuestionRecheckerType() == null ? "" : dto.getQuestionRecheckerType());
        staDO.setCreateUserApprove(dto.getCreateUserApprove());
        staDO.setQuestionCcId(dto.getQuestionCcId() == null ? "" : dto.getQuestionCcId());
        staDO.setQuestionCcType(dto.getQuestionCcType() == null ? "" : dto.getQuestionCcType());
        staDO.setStandardPic(dto.getStandardPic() == null ? "" : dto.getStandardPic());
        staDO.setLevel(dto.getLevel() == null ? "" : dto.getLevel());
        staDO.setSupportScore(dto.getSupportScore() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getSupportScore());
        staDO.setLowestScore(dto.getLowestScore() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getLowestScore());
        staDO.setStoreSceneId(dto.getStoreSceneId());
        // sop文档id
        staDO.setSopId(dto.getSopId() == null ? 0 : dto.getSopId());
        //酷学院课程
        staDO.setCoolCourse(dto.getCoolCourse() == null ? null : JSON.toJSONString(dto.getCoolCourse()));
        //免费课程
        staDO.setFreeCourse(dto.getFreeCourse() == null ? null : JSON.toJSONString(dto.getFreeCourse()));
        staDO.setQuestionApproveUser(dto.getQuestionApproveUser());
        staDO.setThreshold(dto.getThreshold() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getThreshold());
        staDO.setAiType(dto.getAiType());
        staDO.setWeightPercent(dto.getWeightPercent() == null ? new BigDecimal(Constants.ZERO_STR) : dto.getWeightPercent());
        staDO.setUserDefinedScore(dto.getUserDefinedScore() == null ? 0 : dto.getUserDefinedScore());
        staDO.setConfigType(dto.getConfigType() == null ? 0 : dto.getConfigType());
        staDO.setQuickColumnId(dto.getQuickColumnId());
        staDO.setStatus(dto.getStatus() == null ? Boolean.FALSE : dto.getStatus());
        staDO.setColumnType(dto.getColumnType() == null ? 0 : dto.getColumnType());
        staDO.setEditTime(new Date());
        staDO.setCanModify(true);
        if (dto.getId() != null) {
            staDO.setCanModify(dto.getCanModify());
        }
        if (dto.getMustPic() != null) {
            staDO.setMustPic(dto.getMustPic());
        }
        staDO.setIsAiCheck(dto.getIsAiCheck());
        staDO.setAiCheckStdDesc(dto.getAiCheckStdDesc());
        staDO.setExtendInfo(fillExtendField(dto));
        return staDO;
    }

    /**
     * 扩展信息填充
     * @param obj 请求对象
     * @return 扩展信息
     */
    public static String fillExtendField(Object obj) {
        Boolean descRequired = null;
        Integer autoQuestionTaskValidity = null;
        Boolean isSetAutoQuestionTaskValidity = null;
        String aiModel = null;
        Integer minCheckPicNum = null;
        Integer maxCheckPicNum = null;
        if (obj instanceof TbMetaStaColumnDTO) {
            descRequired = ((TbMetaStaColumnDTO) obj).getDescRequired();
            autoQuestionTaskValidity = ((TbMetaStaColumnDTO) obj).getAutoQuestionTaskValidity();
            isSetAutoQuestionTaskValidity = ((TbMetaStaColumnDTO) obj).getIsSetAutoQuestionTaskValidity();
            aiModel = ((TbMetaStaColumnDTO) obj).getAiModel();
            minCheckPicNum = ((TbMetaStaColumnDTO) obj).getMinCheckPicNum();
            maxCheckPicNum = ((TbMetaStaColumnDTO) obj).getMaxCheckPicNum();
        } else if (obj instanceof QuickTableColumnRequest) {
            descRequired = ((QuickTableColumnRequest) obj).getDescRequired();
            autoQuestionTaskValidity = ((QuickTableColumnRequest) obj).getAutoQuestionTaskValidity();
            isSetAutoQuestionTaskValidity = ((QuickTableColumnRequest) obj).getIsSetAutoQuestionTaskValidity();
            aiModel = ((QuickTableColumnRequest) obj).getAiModel();
            minCheckPicNum = ((QuickTableColumnRequest) obj).getMinCheckPicNum();
            maxCheckPicNum = ((QuickTableColumnRequest) obj).getMaxCheckPicNum();
        } else {
            return null;
        }
        JSONObject jsonObject = new JSONObject();
        if (Objects.nonNull(descRequired)) {
            jsonObject.put(Constants.TableColumn.DESC_REQUIRED, descRequired);
        }else{
            jsonObject.put(Constants.TableColumn.DESC_REQUIRED, false);
        }
        if (Objects.nonNull(autoQuestionTaskValidity)) {
            jsonObject.put(Constants.TableColumn.AUTO_QUESTION_TASK_VALIDITY, autoQuestionTaskValidity);
        }
        if (Objects.nonNull(minCheckPicNum)) {
            jsonObject.put(Constants.TableColumn.MIN_CHECK_PIC_NUM, minCheckPicNum);
        }
        if (Objects.nonNull(maxCheckPicNum)) {
            jsonObject.put(Constants.TableColumn.MAX_CHECK_PIC_NUM, maxCheckPicNum);
        }
        if (Objects.nonNull(isSetAutoQuestionTaskValidity)) {
            jsonObject.put(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY, isSetAutoQuestionTaskValidity);
        }else{
            jsonObject.put(Constants.TableColumn.IS_SET_AUTO_QUESTION_TASK_VALIDITY, false);
        }
        if (StringUtils.isNotBlank(aiModel)) {
            jsonObject.put(Constants.STORE_WORK_AI.AI_MODEL, aiModel);
        }
        return jsonObject.toJSONString();
    }

    @Resource
    SysRoleMapper roleMapper;

    /**
     * 标准检查表插入 根据前端入参生成检查表DO实体
     *
     * @param user
     * @param metaStaTableDTO
     * @return
     */
    private PatrolMetaDTO getPatrolMetaDOFromDTO(String enterpriseId, String createUserId ,CurrentUser user, TbMetaStaTableDTO metaStaTableDTO) {

        TbMetaTableDO tbMetaTableDO = new TbMetaTableDO();
        tbMetaTableDO.setCreateUserId(createUserId);
        tbMetaTableDO.setCreateUserName(user.getName());
        tbMetaTableDO.setEditUserId(user.getUserId());
        tbMetaTableDO.setEditUserName(user.getName());
        tbMetaTableDO.setDescription(metaStaTableDTO.getDescription());
        tbMetaTableDO.setSupportScore(metaStaTableDTO.getSupportScore());
        tbMetaTableDO.setTableName(metaStaTableDTO.getTableName());
        tbMetaTableDO.setTableType(metaStaTableDTO.getTableType());
        tbMetaTableDO.setLevelRule(metaStaTableDTO.getLevelRule());
        tbMetaTableDO.setLevelInfo(metaStaTableDTO.getLevelInfo());
        tbMetaTableDO.setDefaultResultColumn(metaStaTableDTO.getDefaultResultColumn());
        tbMetaTableDO.setNoApplicableRule(metaStaTableDTO.getNoApplicableRule());
        tbMetaTableDO.setTableProperty(metaStaTableDTO.getTableProperty());
        tbMetaTableDO.setTotalScore(metaStaTableDTO.getTotalScore() == null ? new BigDecimal(0) : metaStaTableDTO.getTotalScore());
        tbMetaTableDO.setCategoryNameList(JSONObject.toJSONString(metaStaTableDTO.getCategoryNameList()));
        tbMetaTableDO.setUsePersonInfo(metaStaTableDTO.getUsePersonInfo());
        tbMetaTableDO.setUseRange(metaStaTableDTO.getUseRange());
        tbMetaTableDO.setResultViewPersonInfo(metaStaTableDTO.getResultViewPersonInfo());
        tbMetaTableDO.setResultViewRange(metaStaTableDTO.getResultViewRange());
        tbMetaTableDO.setCommonEditPersonInfo(getEditPersonInfo(enterpriseId, metaStaTableDTO.getCommonEditUserIdList(), metaStaTableDTO.getCommonEditPersonInfo()));
        //使用人同时作为结果查看人
        PatrolMetaDTO patrolMetaDTO = userPersonInfoService.dealMetaTableUserInfo(enterpriseId, tbMetaTableDO, metaStaTableDTO.getResultViewUserWithUserRang());
        tbMetaTableDO.setIsSupportNegativeScore(metaStaTableDTO.getIsSupportNegativeScore());
        tbMetaTableDO.setPid(metaStaTableDTO.getPid());
        TbMetaTableDO tbMetaTableDO1 = tbMetaTableMapper.selectById(enterpriseId, metaStaTableDTO.getPid());
        if(tbMetaTableDO1 != null){
            tbMetaTableDO.setSopPath(tbMetaTableDO1.getSopPath()+tbMetaTableDO1.getId()+"/");
        }
        //查看人处理
        shareGroupDataHandler(enterpriseId, tbMetaTableDO, metaStaTableDTO.getShareGroup(), metaStaTableDTO.getShareGroupPosition());
        //结果查看人处理
        resultShareGroupDataHandler(enterpriseId, tbMetaTableDO, metaStaTableDTO.getResultShareGroup(), metaStaTableDTO.getResultShareGroupPosition());
        tbMetaTableDO.setCopyModify(metaStaTableDTO.getCopyModify());

        tbMetaTableDO.setAiResultMethod(metaStaTableDTO.getAiResultMethod());
        tbMetaTableDO.setIsAiCheck(metaStaTableDTO.getIsAiCheck());
        return patrolMetaDTO;
    }

    public String getEditPersonInfo(String enterpriseId, List<String> commonEditUserIdList, String commonEditPersonInfo){
        List<PersonUsePositionDTO> personPositionDTOList = new ArrayList<>();
        if(StringUtils.isNotBlank(commonEditPersonInfo)){
            personPositionDTOList = JSONObject.parseArray(commonEditPersonInfo, PersonUsePositionDTO.class);
        }
        if(CollectionUtils.isNotEmpty(commonEditUserIdList)){
            Map<String, String> userNameMap = enterpriseUserDao.getUserNameMap(enterpriseId, commonEditUserIdList);
            for (String s : commonEditUserIdList) {
                personPositionDTOList.add(PersonUsePositionDTO.builder().type(PersonTypeEnum.PERSON.getType()).value(s).name(userNameMap.getOrDefault(s, "")).value(s).build());
            }
        }
        return JSONObject.toJSONString(personPositionDTOList);
    }

    /**
     * 检查表查看人处理
     *
     * @param enterpriseId
     * @param tbMetaTableDO
     * @param oldShareGroup
     * @param shareGroupPosition
     */
    public void shareGroupDataHandler(String enterpriseId, TbMetaTableDO tbMetaTableDO, String oldShareGroup, String shareGroupPosition) {
        //结果查看人  可能选人 可能选职位
        String shareGroupName = null;
        //加工之后的检查表查看人(老的查看人+职位对应的查看人)
        String newShareGroup = null;
        Set<String> shareGroupNameSet = new HashSet<>();
        Set<String> shareGroupSet = new HashSet<>();
        //如果选择职位
        if (StringUtils.isNotBlank(shareGroupPosition)) {
            List<Long> positionIdList = Arrays.asList(shareGroupPosition.split(Constants.COMMA)).stream().map(data -> Long.parseLong(data)).collect(Collectors.toList());
            List<EnterpriseUserDTO> personsByRole = sysRoleMapper.getPersonsByRole(enterpriseId, positionIdList, null);
            if (CollectionUtils.isNotEmpty(personsByRole)) {
                shareGroupNameSet = personsByRole.stream().map(data -> data.getName()).collect(Collectors.toSet());
                shareGroupSet = personsByRole.stream().map(data -> data.getUserId()).collect(Collectors.toSet());
            }
        }
        //如果选人
        if (StringUtils.isNotEmpty(oldShareGroup)) {
            String userNameList = findUserName(oldShareGroup, enterpriseId);
            shareGroupNameSet.addAll(Arrays.asList(userNameList.split(Constants.COMMA)).stream().collect(Collectors.toSet()));
            shareGroupSet.addAll(Arrays.asList(oldShareGroup.split(Constants.COMMA)).stream().collect(Collectors.toSet()));
        }

        if (CollectionUtils.isNotEmpty(shareGroupNameSet)) {
            shareGroupName = shareGroupNameSet.stream().collect(Collectors.joining(Constants.COMMA));
            tbMetaTableDO.setShareGroupName(shareGroupName);
        }
        if (CollectionUtils.isNotEmpty(shareGroupSet)) {
            newShareGroup = shareGroupSet.stream().collect(Collectors.joining(Constants.COMMA));
            tbMetaTableDO.setShareGroup(newShareGroup);
        }
    }


    /**
     * 结果查看人处理
     *
     * @param enterpriseId
     * @param tbMetaTableDO
     * @param oldResultShareGroup
     * @param resultShareGroupPosition
     */
    public void resultShareGroupDataHandler(String enterpriseId, TbMetaTableDO tbMetaTableDO, String oldResultShareGroup, String resultShareGroupPosition) {
        //结果查看人 getResultShareGroupPosition 职位ID  getResultShareGroup 人员ID
        String resultShareGroupName = null;
        String newResultShareGroup = null;
        Set<String> resultShareGroupNameSet = new HashSet<>();
        Set<String> resultShareGroupSet = new HashSet<>();
        if (StringUtils.isNotBlank(resultShareGroupPosition)) {
            if (StringUtils.isNotBlank(resultShareGroupPosition)) {
                List<Long> positionIdList = Arrays.asList(resultShareGroupPosition.split(Constants.COMMA)).stream().map(data -> Long.parseLong(data)).collect(Collectors.toList());
                List<EnterpriseUserDTO> personsByRole = sysRoleMapper.getPersonsByRole(enterpriseId, positionIdList, null);
                if (CollectionUtils.isNotEmpty(personsByRole)) {
                    resultShareGroupNameSet = personsByRole.stream().map(data -> data.getName()).collect(Collectors.toSet());
                    resultShareGroupSet = personsByRole.stream().map(data -> data.getUserId()).collect(Collectors.toSet());
                }
            }
        }

        if (StringUtils.isNotBlank(oldResultShareGroup)) {
            String userNameList = findUserName(oldResultShareGroup, enterpriseId);
            resultShareGroupNameSet.addAll(Arrays.asList(userNameList.split(Constants.COMMA)).stream().collect(Collectors.toSet()));
            resultShareGroupSet.addAll(Arrays.asList(oldResultShareGroup.split(Constants.COMMA)).stream().collect(Collectors.toSet()));
        }

        if (CollectionUtils.isNotEmpty(resultShareGroupNameSet)) {
            resultShareGroupName = resultShareGroupNameSet.stream().collect(Collectors.joining(Constants.COMMA));
            tbMetaTableDO.setResultShareGroupName(resultShareGroupName);
        }
        if (CollectionUtils.isNotEmpty(resultShareGroupSet)) {
            newResultShareGroup = resultShareGroupSet.stream().collect(Collectors.joining(Constants.COMMA));
            tbMetaTableDO.setResultShareGroup(newResultShareGroup);
        }

    }


    /**
     * 通过id查询用户名称
     *
     * @param userIds 用户id，逗号隔开
     * @return
     */
    private String findUserName(String userIds, String enterpriseId) {
        if (userIds.endsWith(Constants.COMMA)) {
            userIds = userIds.substring(0, userIds.length() - 1);
        }
        String[] userIdList = userIds.split(Constants.COMMA);
        List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(userIdList));
        String userNameList = CollectionUtils.emptyIfNull(userDOList).stream().map(userDO -> userDO.getName()).collect(Collectors.joining(","));
        return userNameList;
    }


    /**
     * 查询巡店检查表详情 通过DO生成VO 返回数据时 对工单处理人、复检人进行格式化
     *
     * @param columnDOList 数据库查询出来的基础检查项列表
     * @param staVOList    返回数据用的基础检查项列表
     */
    private void getMateStaColumnVOByDO(String enterpriseId, List<TbMetaStaTableColumnDO> columnDOList,
                                        List<TbMetaStaColumnVO> staVOList) {
        Set<Long> positionIdList = new HashSet<>();
        Set<String> personIdList = new HashSet<>();
        //1.id分类
        columnDOList.stream().forEach(columnDO -> {
            if (FormPickerEnum.POSITION.getCode().equals(columnDO.getQuestionHandlerType())) {
                if (StringUtils.isNotBlank(columnDO.getQuestionHandlerId())) {
                    Arrays.asList(columnDO.getQuestionHandlerId().split(",")).stream().forEach(data -> positionIdList.add(Long.parseLong(data)));
                }
            } else if (StringUtils.isNotBlank(columnDO.getQuestionHandlerId())) {
                Arrays.asList(columnDO.getQuestionHandlerId().split(",")).stream().forEach(data -> personIdList.add(data));
            }
            if (FormPickerEnum.POSITION.getCode().equals(columnDO.getQuestionRecheckerType())) {
                if (StringUtils.isNotBlank(columnDO.getQuestionRecheckerId())) {
                    Arrays.asList(columnDO.getQuestionRecheckerId().split(",")).stream().forEach(data -> positionIdList.add(Long.parseLong(data)));
                }
            } else if (StringUtils.isNotBlank(columnDO.getQuestionRecheckerId())) {
                Arrays.asList(columnDO.getQuestionRecheckerId().split(",")).stream().forEach(data -> personIdList.add(data));
            }
        });
        List<SysRoleDO> positionDTOList = new ArrayList<>();
        List<EnterpriseUserDO> allUserDOList = new ArrayList<>();
        //2.查列表
        if (CollectionUtils.isNotEmpty(positionIdList)) {
            positionDTOList = sysRoleMapper.getRoleList(enterpriseId, new ArrayList<>(positionIdList));
        }
        if (CollectionUtils.isNotEmpty(personIdList)) {
            allUserDOList = enterpriseUserDao.selectByUserIds(enterpriseId, new ArrayList<>(personIdList));
        }
        //3.映射成map
        Map<Long, SysRoleDO> positionMap = CollectionUtils.emptyIfNull(positionDTOList).stream()
                .collect(Collectors.toMap(positionDTO -> positionDTO.getId(), data -> data, (a, b) -> a));
        log.info("getMateStaColumnVOByDO positionMap：{}",JSONObject.toJSONString(positionMap));
        Map<String, EnterpriseUserDO> personMap = CollectionUtils.emptyIfNull(allUserDOList).stream()
                .collect(Collectors.toMap(enterpriseUserDO -> enterpriseUserDO.getUserId(), data -> data, (a, b) -> a));
        //4.转换成vo
        for (TbMetaStaTableColumnDO columnDO : columnDOList) {
            TbMetaStaColumnVO pmsVO = new TbMetaStaColumnVO();
            pmsVO.setAwardMoney(columnDO.getAwardMoney());
            pmsVO.setCategory(columnDO.getCategoryName());
            pmsVO.setColumnName(columnDO.getColumnName());
            pmsVO.setCreateTime(columnDO.getCreateTime());
            pmsVO.setCreateUser(columnDO.getCreateUserId());
            pmsVO.setDescription(columnDO.getDescription());
            pmsVO.setEditTime(columnDO.getEditTime());
            pmsVO.setId(columnDO.getId());
            pmsVO.setMetaTableId(columnDO.getMetaTableId());
            pmsVO.setPunishMoney(columnDO.getPunishMoney());
            pmsVO.setStandardPic(columnDO.getStandardPic());
            pmsVO.setLevel(columnDO.getLevel());
            pmsVO.setSupportScore(columnDO.getSupportScore());
            pmsVO.setLowestScore(columnDO.getLowestScore());
            pmsVO.setOrderNum(columnDO.getOrderNum());
            pmsVO.setQuestionHandlerType(columnDO.getQuestionHandlerType());
            pmsVO.setQuestionHandlerId(columnDO.getQuestionHandlerId());
            pmsVO.setQuestionRecheckerId(columnDO.getQuestionRecheckerId());
            pmsVO.setQuestionRecheckerType(columnDO.getQuestionRecheckerType());
            pmsVO.setCreateUserApprove(columnDO.getCreateUserApprove());
            pmsVO.setQuestionApproveUser(columnDO.getQuestionApproveUser());
            pmsVO.setWeightPercent(columnDO.getWeightPercent());
            pmsVO.setUserDefinedScore(columnDO.getUserDefinedScore());
            pmsVO.setConfigType(columnDO.getConfigType());
            pmsVO.setColumnType(columnDO.getColumnType());
            pmsVO.setQuickColumnId(columnDO.getQuickColumnId());
            pmsVO.setStatus(columnDO.getStatus());
            if (FormPickerEnum.POSITION.getCode().equals(columnDO.getQuestionHandlerType())) {
                if (StringUtils.isNotBlank(columnDO.getQuestionHandlerId())) {
                    String positionName = Arrays.asList(columnDO.getQuestionHandlerId().split(",")).stream().map(data -> {
                        SysRoleDO positionDTO = positionMap.get(Long.parseLong(data));
                        if (Objects.isNull(positionDTO)){
                            return "";
                        }
                        log.info("positionDTO ：{}",JSONObject.toJSONString(positionDTO));
                        return positionDTO.getRoleName();
                    }).collect(Collectors.joining(","));
                    pmsVO.setQuestionHandlerName(positionName);
                }
            } else {
                if (StringUtils.isNotBlank(columnDO.getQuestionHandlerId())) {
                    String personName = Arrays.asList(columnDO.getQuestionHandlerId().split(",")).stream().map(data -> {
                        EnterpriseUserDO enterpriseUserDO = personMap.get(data);
                        return enterpriseUserDO.getName();
                    }).collect(Collectors.joining(","));
                    pmsVO.setQuestionHandlerName(personName);
                }
            }
            if (FormPickerEnum.POSITION.getCode().equals(columnDO.getQuestionRecheckerType())) {
                if (StringUtils.isNotBlank(columnDO.getQuestionRecheckerId())) {
                    String positionName = Arrays.asList(columnDO.getQuestionRecheckerId().split(",")).stream().filter(r -> positionMap.get(Long.parseLong(r)) != null).map(data -> {
                        SysRoleDO positionDTO = positionMap.get(Long.parseLong(data));
                        return positionDTO.getRoleName();
                    }).collect(Collectors.joining(","));
                    pmsVO.setQuestionRecheckerName(positionName);
                }
            } else {
                if (StringUtils.isNotBlank(columnDO.getQuestionRecheckerId())) {
                    String personName = Arrays.asList(columnDO.getQuestionRecheckerId().split(",")).stream().map(data -> {
                        EnterpriseUserDO enterpriseUserDO = personMap.get(data);
                        return enterpriseUserDO.getName();
                    }).collect(Collectors.joining(","));
                    pmsVO.setQuestionRecheckerName(personName);
                }
            }
            staVOList.add(pmsVO);
        }

    }


    private void setUpdateUserByDO(TbMetaTableVO item, TbMetaTableDO pdo) {
        String updateUserName;
        String updateUserId;
        Date updateDateTime;
        if (StringUtils.isNotEmpty(pdo.getEditUserName())) {
            updateUserName = pdo.getEditUserName();
            updateDateTime = pdo.getEditTime();
            updateUserId = pdo.getEditUserId();
        } else {
            updateUserName = pdo.getCreateUserName();
            updateDateTime = pdo.getCreateTime();
            updateUserId = pdo.getCreateUserId();
        }
        item.setUpdateTime(updateDateTime);
        item.setUpdateUserName(updateUserName);
        item.setUpdateUserId(updateUserId);
        item.setCreateUserId(pdo.getCreateUserId());
        item.setCreateUserName(pdo.getCreateUserName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long configMetaDefTable(String enterpriseId, CurrentUser user, ConfigMetaDefTableParam param) {
        if ("-2".equals(param.getPid())) {
            TbMetaTableDO defaultMetaTableDO = tbMetaTableMapper.selectByDefault(enterpriseId);
            param.setPid(String.valueOf(defaultMetaTableDO.getId()));
        }
        TbMetaTableDO tbMetaTableDO = null;
        Long tableId = param.getTableId();
        if (tableId == null) {
            // 新增自定义检查表
            tbMetaTableDO = TbMetaTableDO.builder()
                    .tableName(param.getTableName() == null ? "" : param.getTableName())
                    .description(param.getDescription() == null ? "" : param.getDescription())
                    .createUserId(user.getUserId()).createUserName(user.getName()).editUserId(user.getUserId()).editUserName(user.getName())
                    .supportScore(0).tableType(STANDARD).tableProperty(MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode())
                    .pid(Long.valueOf(param.getPid()))
                    .build();
            TbMetaTableDO leafTypeDO = tbMetaTableMapper.selectById(enterpriseId, tbMetaTableDO.getPid());
            tbMetaTableDO.setSopPath(leafTypeDO.getSopPath() + leafTypeDO.getId() + "/");
            tbMetaTableDO.setSopType(Constants.CHILD);
            //拜访表单独处理
            if (StringUtils.isNotBlank(param.getTableType()) && VISIT.equals(param.getTableType())) {
                tbMetaTableDO.setTableType(param.getTableType());
                //拜访表不需要, 默认0
                tbMetaTableDO.setTableProperty(MetaTablePropertyEnum.STANDARD_TABLE.getCode());
            }
            this.shareGroupDataHandler(enterpriseId, tbMetaTableDO, param.getShareGroup(), param.getShareGroupPosition());
            this.resultShareGroupDataHandler(enterpriseId, tbMetaTableDO, param.getResultShareGroup(), param.getResultShareGroupPosition());
            this.userPersonInf(enterpriseId, tbMetaTableDO, param, user.getUserId());
            tbMetaTableMapper.insertTable(enterpriseId, tbMetaTableDO);
            tableId = tbMetaTableDO.getId();
        } else {
            // 编辑自定义检查表
            TbMetaTableDO newTbMetaTableDO =
                    TbMetaTableDO.builder().id(tableId).tableName(param.getTableName() == null ? "" : param.getTableName())
                            .description(param.getDescription() == null ? "" : param.getDescription())
                            .editTime(new Date()).editUserId(user.getUserId()).editUserName(user.getName()).supportScore(0)
                            .build();
            TbMetaTableDO leafTypeDO = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(param.getPid()));
            newTbMetaTableDO.setSopPath(leafTypeDO.getSopPath() + leafTypeDO.getId() + "/");
            newTbMetaTableDO.setSopType(Constants.CHILD);
            this.shareGroupDataHandler(enterpriseId, newTbMetaTableDO, param.getShareGroup(), param.getShareGroupPosition());
            this.resultShareGroupDataHandler(enterpriseId, newTbMetaTableDO, param.getResultShareGroup(), param.getResultShareGroupPosition());
            this.userPersonInf(enterpriseId, newTbMetaTableDO, param, user.getUserId());
            tbMetaTableMapper.updateByPrimaryKeySelective(enterpriseId, newTbMetaTableDO);
            tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, tableId);
            if (tbMetaTableDO.getLocked() == 1) {
                updateDefColumn(tableId, param.getProperties(), enterpriseId, user);
                return tableId;
            }

            // 硬删除旧的自定义检查项
            tbMetaDefTableColumnMapper.delByTableId(enterpriseId, tableId);
        }
        // 解析自定义检查项
        Long finalTableId = tableId;
        JSONObject properties = JSON.parseObject(param.getProperties());
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = properties.keySet().stream().map(k -> {
            JSONObject property = properties.getJSONObject(k);
            String columnName = (String) property.getOrDefault("title", "");
            if (StringUtils.isNotBlank(columnName) && columnName.length() > 100) {
                throw new ServiceException(ErrorCodeEnum.DEF_COLUMN_NAME_MAX_LENGTH, Constants.DEF_COLUMN_NAME_MAX_LENGTH);
            }
            return TbMetaDefTableColumnDO.builder().metaTableId(finalTableId)
                    .columnName((String) property.getOrDefault("title", ""))
                    .description((String) property.getOrDefault("description", ""))
                    .columnLength((Integer) property.getOrDefault("column_length", 0))
                    .format((String) property.getOrDefault("x-component", ""))
                    .required((boolean) property.getOrDefault("required", false) ? 1 : 0).createrUserId(user.getUserId())
                    .orderNum((int) property.getOrDefault("order", 0))
                    .chooseValues(((JSONArray) property.getOrDefault("enum", new JSONArray())).stream().map(String::valueOf)
                            .collect(Collectors.joining(",")))
                    .schema(property.toJSONString()).build();
        }).collect(Collectors.toList());
        tbMetaDefTableColumnMapper.batchInsert(enterpriseId, tbMetaDefTableColumnDOList);
        updateMetaTableAuth(enterpriseId, tbMetaTableDO);
        return finalTableId;
    }

    private void updateDefColumn(Long tableId, String properties, String enterpriseId, CurrentUser user) {
        List<TbMetaDefTableColumnDO> columnDOS = tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, tableId);
        Set<String> set = columnDOS.stream().map(data -> String.valueOf(data.getId())).collect(Collectors.toSet());
        JSONObject parseObject = JSON.parseObject(properties);
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList = parseObject.keySet().stream().filter(data -> set.contains(data)).map(k -> {
            JSONObject property = parseObject.getJSONObject(k);
            String columnName = (String) property.getOrDefault("title", "");
            if (StringUtils.isNotBlank(columnName) && columnName.length() > 100) {
                throw new ServiceException(ErrorCodeEnum.DEF_COLUMN_NAME_MAX_LENGTH, Constants.DEF_COLUMN_NAME_MAX_LENGTH);
            }

            return TbMetaDefTableColumnDO.builder().metaTableId(tableId)
                    .id(Long.parseLong(k))
                    .columnName((String) property.getOrDefault("title", ""))
                    .description((String) property.getOrDefault("description", ""))
                    .columnLength((Integer) property.getOrDefault("column_length", 0))
                    .format((String) property.getOrDefault("x-component", ""))
                    .required((boolean) property.getOrDefault("required", false) ? 1 : 0)
                    .orderNum((int) property.getOrDefault("order", 0))
                    .chooseValues(((JSONArray) property.getOrDefault("enum", new JSONArray())).stream().map(String::valueOf)
                            .collect(Collectors.joining(",")))
                    .schema(property.toJSONString()).build();
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(tbMetaDefTableColumnDOList)) {
            tbMetaDefTableColumnMapper.batchUpdateStaColumn(enterpriseId, tbMetaDefTableColumnDOList);
        }
        Long finalTableId = tableId;
        List<TbMetaDefTableColumnDO> newAddTbMetaDefTableColumnDOList = parseObject.keySet().stream().filter(data -> !set.contains(data)).map(k -> {
            JSONObject property = parseObject.getJSONObject(k);
            String columnName = (String) property.getOrDefault("title", "");
            if (StringUtils.isNotBlank(columnName) && columnName.length() > 100) {
                throw new ServiceException(ErrorCodeEnum.DEF_COLUMN_NAME_MAX_LENGTH, Constants.DEF_COLUMN_NAME_MAX_LENGTH);
            }
            return TbMetaDefTableColumnDO.builder().metaTableId(finalTableId)
                    .columnName((String) property.getOrDefault("title", ""))
                    .description((String) property.getOrDefault("description", ""))
                    .columnLength((Integer) property.getOrDefault("column_length", 0))
                    .format((String) property.getOrDefault("x-component", ""))
                    .required((boolean) property.getOrDefault("required", false) ? 1 : 0).createrUserId(user.getUserId())
                    .orderNum((int) property.getOrDefault("order", 0))
                    .chooseValues(((JSONArray) property.getOrDefault("enum", new JSONArray())).stream().map(String::valueOf)
                            .collect(Collectors.joining(",")))
                    .schema(property.toJSONString()).build();
        }).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(newAddTbMetaDefTableColumnDOList)) {
            tbMetaDefTableColumnMapper.batchInsert(enterpriseId, newAddTbMetaDefTableColumnDOList);
        }

    }

    @Override
    public TbMetaDefTableVO getMetaDefTable(String enterpriseId, Long metaTableId, String userId) {
        List<String> editAuthTableIds = tbMetaTableUserAuthDAO.getEditAuthTableIds(enterpriseId, userId, Arrays.asList(metaTableId));
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, metaTableId);
        List<TbMetaDefTableColumnDO> tbMetaDefTableColumnDOList =
                tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, metaTableId);
        Map<String, JSONObject> columnIdSchemaMap = tbMetaDefTableColumnDOList.stream().collect(Collectors
                .toMap(column -> String.valueOf(column.getId()), column -> JSON.parseObject(column.getSchema()),
                        (a, b) -> a));
        List<Long> columnOrder =
                tbMetaDefTableColumnDOList.stream().sorted(Comparator.comparing(TbMetaDefTableColumnDO::getOrderNum))
                        .map(TbMetaDefTableColumnDO::getId).collect(Collectors.toList());
        boolean editFlag = false;
        if (userId.equals(tbMetaTableDO.getCreateUserId())) {
            editFlag = true;
        }
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if (isAdmin) {
            editFlag = true;
        }else{
            editFlag = editAuthTableIds.contains(String.valueOf(metaTableId));
        }
        //共同编辑人列表
        List<PersonDTO> personDTOList = new ArrayList<>();
        int lastSlashIndex = tbMetaTableDO.getSopPath().lastIndexOf("/");
        int secondLastSlashIndex = tbMetaTableDO.getSopPath().lastIndexOf("/", lastSlashIndex - 1);
        String substring = tbMetaTableDO.getSopPath().substring(secondLastSlashIndex + 1, lastSlashIndex);
        TbMetaTableDO tbMetaTableDO1 = tbMetaTableMapper.selectById(enterpriseId, Long.valueOf(substring));
        return TbMetaDefTableVO.builder()
                .properties(JSON.toJSONString(columnIdSchemaMap))
                .columnOrder(columnOrder)
                .editFlag(editFlag)
                .table(tbMetaTableDO)
                .commonEditUserList(personDTOList)
                .columnList(tbMetaDefTableColumnDOList)
                .sopPath(tbMetaTableDO.getSopPath())
                .sopType(tbMetaTableDO.getSopType())
                .pid(tbMetaTableDO1.getId())
                .groupName(tbMetaTableDO1.getTableName())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyMetaDefTable(String enterpriseId, CurrentUser user, Long metaTableId) {
        TbMetaTableDO oldDefTable = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
        // 判断是不是有效的自定义检查表
        boolean valid = oldDefTable != null && oldDefTable.getDeleted() == 0 &&
                TableTypeUtil.isUserDefinedTable(oldDefTable.getTableProperty(), oldDefTable.getTableType());
        if (!valid) {
            throw new ServiceException("不存在该自定义检查表");
        }
        // 获取自定义检查项
        List<TbMetaDefTableColumnDO> oldDefColumnList =
                tbMetaDefTableColumnMapper.selectByTableId(enterpriseId, metaTableId);
        // 新增自定义检查表
        TbMetaTableDO newDefTable =
                TbMetaTableDO.builder()
                        .tableName(oldDefTable.getTableName() + UUIDUtils.get8UUID())
                        .description(oldDefTable.getDescription())
                        .shareGroup(oldDefTable.getShareGroup())
                        .shareGroupName(oldDefTable.getShareGroupName())
                        .createUserId(user.getUserId())
                        .createUserName(user.getName())
                        .editUserId(user.getUserId())
                        .editUserName(user.getName())
                        .tableProperty(MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode())
                        .supportScore(oldDefTable.getSupportScore()).tableType(STANDARD)
                        .usePersonInfo(oldDefTable.getUsePersonInfo())
                        .useRange(oldDefTable.getUseRange())
                        .resultViewPersonInfo(oldDefTable.getResultViewPersonInfo())
                        .resultViewRange(oldDefTable.getResultViewRange())
                        .commonEditPersonInfo(oldDefTable.getCommonEditPersonInfo())
                        .resultShareGroup(oldDefTable.getResultShareGroup())
                        .resultShareGroupName(oldDefTable.getResultShareGroupName())
                        .sopPath(oldDefTable.getSopPath())
                        .sopType(oldDefTable.getSopType())
                        .build();
        tbMetaTableMapper.insertTable(enterpriseId, newDefTable);
        // 新增自定义检查项
        Long newDefTableId = newDefTable.getId();
        if (CollectionUtils.isNotEmpty(oldDefColumnList)) {
            List<TbMetaDefTableColumnDO> newDefColumnList = oldDefColumnList.stream()
                    .map(a -> TbMetaDefTableColumnDO.builder().metaTableId(newDefTableId)
                            .columnName(a.getColumnName()).description(a.getDescription()).columnLength(a.getColumnLength())
                            .format(a.getFormat()).required(a.getRequired()).createrUserId(user.getUserId())
                            .orderNum(a.getOrderNum()).chooseValues(a.getChooseValues()).schema(a.getSchema()).build())
                    .collect(Collectors.toList());
            tbMetaDefTableColumnMapper.batchInsert(enterpriseId, newDefColumnList);
        }
        return newDefTableId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delMetaDefTable(String enterpriseId, CurrentUser user, List<Long> metaTableIdList) {
        if(CollectionUtils.isEmpty(metaTableIdList)){
            return false;
        }
        tbMetaTableMapper.updateDelByIds(enterpriseId, metaTableIdList);
        tbMetaDefTableColumnMapper.updateDelByTableIds(enterpriseId, metaTableIdList);
        tbMetaTableUserAuthDAO.deleteByBusinessIds(enterpriseId, metaTableIdList.stream().map(String::valueOf).collect(Collectors.toList()), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE);
        return Boolean.TRUE;
    }

    @Override
    public List<ColumnCategoryDTO> getTableColumnCategory(String enterpriseId, Long tableId) {
        List<ColumnCategoryDTO> result = new ArrayList<>();
        List<ColumnCategoryDTO> category = tbMetaStaTableColumnMapper.getCategoryByTableId(enterpriseId, tableId);
        Map<String, List<ColumnCategoryDTO>> categoryMap = category.stream().collect(Collectors.groupingBy(data -> data.getCategoryName()));
        for (String categoryName : categoryMap.keySet()) {
            ColumnCategoryDTO columnCategoryDTO = new ColumnCategoryDTO();
            columnCategoryDTO.setCategoryName(categoryName);
            columnCategoryDTO.setCount(categoryMap.get(categoryName).size());
            result.add(columnCategoryDTO);
        }
        return result;
    }

    @Override
    public TbDetailStaVO getTableDetailByIdAndCategory(String enterpriseId, Long tableId, String category) {
        TbDetailStaVO result = new TbDetailStaVO();
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, tableId);
        if (tbMetaTableDO != null && !TableTypeUtil.isUserDefinedTable(tbMetaTableDO.getTableProperty(), tbMetaTableDO.getTableType())) {
            result.setTable(tbMetaTableDO);
            List<TbMetaStaTableColumnDO> staDOList = tbMetaStaTableColumnMapper.getTableColumnByIdAndCategory(enterpriseId, tableId, category);

            List<TbMetaStaColumnVO> staVOList = new ArrayList<>();
            getMateStaColumnVOByDO(enterpriseId, staDOList, staVOList);
            result.setColumnList(staVOList);
        }
        List<EnterpriseUserDO> userList = new ArrayList<>();
        String shareGroup = tbMetaTableDO.getShareGroup();
        if (StringUtils.isNotBlank(shareGroup)) {
            userList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(shareGroup.split(",")));
        }
        result.setUserList(userList);
        return result;
    }

    @Override
    public MetaTableMetaColumnResp getMetaTableMetaColumn(String enterpriseId, List<Long> tableIdList) {
        MetaTableMetaColumnResp resp = new MetaTableMetaColumnResp();
        List<TbMetaDefTableColumnDO> defColumnList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId, tableIdList);
        List<TbMetaStaTableColumnDO> staColumnList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, tableIdList);
        resp.setDefMetaColumnList(defColumnList);
        resp.setStaMetaColumnList(staColumnList);
        return resp;
    }

    @Override
    public List<MetaStaColumnVO> getStaColumnTailById(String enterpriseId, List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            throw new ServiceException("id不能为空");
        }
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList = tbMetaColumnResultMapper.selectByColumnIds(enterpriseId, idList);
        List<TbMetaColumnResultDTO> columnResultDTOList = getMetaColumnResultList(enterpriseId, columnResultDOList);
        Map<Long, List<TbMetaColumnResultDTO>> columnIdResultDOsMap =
                columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));
        List<TbMetaStaTableColumnDO> columnDOList = tbMetaStaTableColumnMapper.getDetailByIdList(enterpriseId, idList);
        //查询所有得门店场景
        if (columnDOList != null) {
            List<StoreSceneDo> storeSceneList = storeSceneMapper.getStoreSceneList(enterpriseId);
            Map<Long, String> storeSceneMap = storeSceneList.stream().collect(Collectors.toMap(StoreSceneDo::getId, StoreSceneDo::getName));
            columnDOList.stream().forEach(data -> {
                if (storeSceneMap.containsKey(data.getStoreSceneId())) {
                    data.setStoreSceneName(storeSceneMap.get(data.getStoreSceneId()));
                }
            });
        }
        List<Long> sopIdList = columnDOList.stream().map(TbMetaStaTableColumnDO::getSopId).collect(Collectors.toList());
        List<TaskSopVO> taskSopVOList = taskSopService.listByIdList(enterpriseId, sopIdList);
        Map<Long, TaskSopVO> taskSopVOMap = taskSopVOList.stream().collect(Collectors.toMap(TaskSopVO::getId, t -> t));// t->t 表示对象本身
        List<Long> metaTableIdList = columnDOList.stream().map(TbMetaStaTableColumnDO::getMetaTableId).collect(Collectors.toList());
        Map<Long, String> metaTableNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(metaTableIdList)) {
            List<TbMetaTableDO> mataTableList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIdList);
            metaTableNameMap = ListUtils.emptyIfNull(mataTableList).stream().collect(Collectors.toMap(TbMetaTableDO::getId, TbMetaTableDO::getTableName));
        }
        // 设置名字数据
        setNameData(enterpriseId, columnDOList);
        Map<Long, String> finalMetaTableNameMap = metaTableNameMap;

        //不合格原因
        List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByColumnIdList(enterpriseId, idList);
        Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnReasonDTOList)) {
            columnIdReasonMap =
                    columnReasonDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId));
        }
        Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
        return columnDOList.stream().map(a -> {
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(a, metaStaColumnVO);
            //如果是采集项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())) {
                metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
            }
            // 结果项
            metaStaColumnVO.setColumnResultList(columnIdResultDOsMap.getOrDefault(a.getId(), new ArrayList<>()));
            metaStaColumnVO.fillColumnResultList();
            // sop文档对象
            if (taskSopVOMap != null) {
                metaStaColumnVO.setTaskSopVO(taskSopVOMap.get(a.getSopId()));
            }
            //酷学院课程
            if (StringUtils.isNotBlank(a.getCoolCourse())) {
                metaStaColumnVO.setCoolCourseVO(JSON.parseObject(a.getCoolCourse(), CoolCourseVO.class));
            }
            //酷学院课程
            if (StringUtils.isNotBlank(a.getFreeCourse())) {
                metaStaColumnVO.setFreeCourseVO(JSON.parseObject(a.getFreeCourse(), CoolCourseVO.class));
            }
            metaStaColumnVO.setMetaTableName(finalMetaTableNameMap.get(a.getMetaTableId()));
            metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
            return metaStaColumnVO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ColumnCategoryVO> getColumnGroupByCategory(String enterpriseId, Long tableId) {
        List<ColumnCategoryVO> result = new ArrayList<>();
        // 结果项
        List<TbMetaColumnResultDO> columnResultDOList =
                tbMetaColumnResultMapper.selectByMetaTableId(enterpriseId, tableId);
        List<TbMetaColumnResultDTO> columnResultDTOList = getMetaColumnResultList(enterpriseId, columnResultDOList);
        Map<Long, List<TbMetaColumnResultDTO>> columnIdResultDOsMap =
                columnResultDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnResultDTO::getMetaColumnId));
        // 检查项
        List<TbMetaStaTableColumnDO> staColumnList = tbMetaStaTableColumnMapper.getAllColumnBymetaTableIdList(enterpriseId, Arrays.asList(tableId));
        // 设置名字数据
        setNameData(enterpriseId, staColumnList);
        //不合格原因
        List<TbMetaColumnReasonDTO> columnReasonDTOList = metaColumnReasonDao.getListByMetaTableId(enterpriseId, tableId);
        Map<Long, List<TbMetaColumnReasonDTO>> columnIdReasonMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(columnReasonDTOList)) {
            columnIdReasonMap =
                    columnReasonDTOList.stream().collect(Collectors.groupingBy(TbMetaColumnReasonDTO::getMetaColumnId));
        }
        Map<Long, List<TbMetaColumnReasonDTO>> finalColumnIdReasonMap = columnIdReasonMap;
        List<MetaStaColumnVO> staColumnVOList = staColumnList.stream().map(a -> {
            MetaStaColumnVO metaStaColumnVO = new MetaStaColumnVO();
            BeanUtils.copyProperties(a, metaStaColumnVO);
            //如果是采集项
            if (MetaColumnTypeEnum.COLLECT_COLUMN.getCode().equals(metaStaColumnVO.getColumnType())) {
                metaStaColumnVO.setMaxScore(metaStaColumnVO.getSupportScore());
                metaStaColumnVO.setMinScore(metaStaColumnVO.getLowestScore());
            }
            // 结果项
            metaStaColumnVO.setColumnResultList(columnIdResultDOsMap.getOrDefault(a.getId(), new ArrayList<>()));
            metaStaColumnVO.fillColumnResultList();
            metaStaColumnVO.setColumnReasonList(finalColumnIdReasonMap.get(a.getId()));
            return metaStaColumnVO;
        }).collect(Collectors.toList());
        Map<String, List<MetaStaColumnVO>> columnMap =
                staColumnVOList.stream().collect(Collectors.groupingBy(TbMetaStaTableColumnDO::getCategoryName));
        for (String key : columnMap.keySet()) {
            ColumnCategoryVO entity = new ColumnCategoryVO();
            List<MetaStaColumnVO> columnList = columnMap.get(key);
            entity.setColumnName(key);
            entity.setColumnList(columnList);
            entity.setColumnCount(columnList.size());
            result.add(entity);
        }
        return result;
    }

    @Override
    public void setNameData(String enterpriseId, List<TbMetaStaTableColumnDO> metaStaColumnList) {
        Set<String> userIds = new HashSet<>();
        Set<Long> positionIds = new HashSet<>();
        String position = "position";
        String person = "person";
        metaStaColumnList.forEach(a -> {
            if (StringUtils.isNotBlank(a.getQuestionHandlerId())) {
                if (position.equals(a.getQuestionHandlerType())) {
                    Arrays.stream(a.getQuestionHandlerId().split(",")).forEach(data -> {
                        positionIds.add(Long.valueOf(data));
                    });

                }
                if (person.equals(a.getQuestionHandlerType())) {
                    Arrays.stream(a.getQuestionHandlerId().split(",")).forEach(data -> {
                        userIds.add(data);
                    });
                }
            }
            if (StringUtils.isNotBlank(a.getQuestionRecheckerId())) {
                if (position.equals(a.getQuestionRecheckerType())) {
                    Arrays.stream(a.getQuestionRecheckerId().split(",")).forEach(data -> {
                        positionIds.add(Long.valueOf(data));
                    });
                }
                if (person.equals(a.getQuestionRecheckerType())) {
                    Arrays.stream(a.getQuestionRecheckerId().split(",")).forEach(data -> {
                        userIds.add(data);
                    });
                }
            }

        });
        // Map: userId -> userName
        Map<String, String> userIdNameMap  = enterpriseUserDao.getUserNameMap(enterpriseId, new ArrayList<>(userIds));
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
        metaStaColumnList.forEach(a -> {
            if (StringUtils.isNotBlank(a.getQuestionHandlerId())) {
                if (position.equals(a.getQuestionHandlerType())) {
                    StringBuffer sb = new StringBuffer();
                    Arrays.stream(a.getQuestionHandlerId().split(",")).forEach(data -> {
                        sb.append(positionIdNameMap.get(Long.valueOf(data))).append(",");
                    });
                    int lastSplitIndex = sb.lastIndexOf(",");
                    if (lastSplitIndex > 0) {
                        sb.deleteCharAt(lastSplitIndex);
                    }
                    a.setQuestionHandlerName(sb.toString());
                }
                if (person.equals(a.getQuestionHandlerType())) {
                    StringBuffer sb = new StringBuffer();
                    Arrays.stream(a.getQuestionHandlerId().split(",")).forEach(data -> {
                        sb.append(userIdNameMap.get(data)).append(",");
                    });
                    int lastSplitIndex = sb.lastIndexOf(",");
                    if (lastSplitIndex > 0) {
                        sb.deleteCharAt(lastSplitIndex);
                    }
                    a.setQuestionHandlerName(sb.toString());
                }
            }
            if (StringUtils.isNotBlank(a.getQuestionRecheckerId())) {
                if (position.equals(a.getQuestionRecheckerType())) {
                    StringBuffer sb = new StringBuffer();
                    Arrays.stream(a.getQuestionRecheckerId().split(",")).forEach(data -> {
                        sb.append(positionIdNameMap.get(Long.valueOf(data))).append(",");
                    });
                    int lastSplitIndex = sb.lastIndexOf(",");
                    if (lastSplitIndex > 0) {
                        sb.deleteCharAt(lastSplitIndex);
                    }
                    a.setQuestionRecheckerName(sb.toString());
                }
                if (person.equals(a.getQuestionRecheckerType())) {
                    StringBuffer sb = new StringBuffer();
                    Arrays.stream(a.getQuestionRecheckerId().split(",")).forEach(data -> {
                        sb.append(userIdNameMap.get(data)).append(",");
                    });
                    int lastSplitIndex = sb.lastIndexOf(",");
                    if (lastSplitIndex > 0) {
                        sb.deleteCharAt(lastSplitIndex);
                    }
                    a.setQuestionRecheckerName(sb.toString());
                }
            }

            if (StringUtils.isNotBlank(a.getQuestionCcId())) {
                cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(a.getQuestionCcId());
                List<PersonPositionDTO> ccIdList = JSONUtil.toList(jsonArray, PersonPositionDTO.class);
                a.setCcPeopleList(ccIdList);
            }
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public TbMetaTableDO addOrUpdateDisplayMetaTable(String enterpriseId, CurrentUser user, TbDisplayTableDTO metaStaTableDTO) {
        TbMetaTableDO old = null;
        if (metaStaTableDTO.getId() != null) {
            old = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, metaStaTableDTO.getId());
        }
        String createUserId = Optional.ofNullable(old).map(TbMetaTableDO::getCreateUserId).orElse(user.getUserId());
        TbMetaTableDO tbMetaTableDO = new TbMetaTableDO();
        Integer tableProperty = metaStaTableDTO.getTableProperty();
        tbMetaTableDO.setTableName(metaStaTableDTO.getName());
        tbMetaTableDO.setTableType(MetaTableConstant.TableTypeConstant.TB_DISPLAY);
        tbMetaTableDO.setDescription("");
        if (metaStaTableDTO.getDeleted() != null) {
            tbMetaTableDO.setDeleted(metaStaTableDTO.getDeleted());
        }

        // 当适用范围不为空时追加用户自己的可视权限
        String shareGroup = metaStaTableDTO.getScope();
        tbMetaTableDO.setShareGroup("");
        tbMetaTableDO.setShareGroupName("");
        if (StringUtils.isNotEmpty(shareGroup)) {
            if (shareGroup.endsWith(Constants.COMMA)) {
                shareGroup = shareGroup.substring(0, shareGroup.length() - 1);
            }
            String[] userIdList = shareGroup.split(Constants.COMMA);
            List<EnterpriseUserDO> userDOList = enterpriseUserDao.selectByUserIds(enterpriseId, Arrays.asList(userIdList));
            String userNameList = CollectionUtils.emptyIfNull(userDOList).stream().map(EnterpriseUserDO::getName).collect(Collectors.joining(","));
            shareGroup = CollectionUtils.emptyIfNull(userDOList).stream().map(EnterpriseUserDO::getUserId).collect(Collectors.joining(","));
            tbMetaTableDO.setShareGroupName(userNameList);
            tbMetaTableDO.setShareGroup(shareGroup);
        }
        tbMetaTableDO.setUsePersonInfo(metaStaTableDTO.getUsePersonInfo());
        tbMetaTableDO.setUseRange(metaStaTableDTO.getUseRange());
        tbMetaTableDO.setCommonEditPersonInfo(getEditPersonInfo(enterpriseId, metaStaTableDTO.getCommonEditUserIdList(), null));
        tbMetaTableDO.setCreateUserId(createUserId);

        if (old != null) {
            //1.1检查表修改
            tbMetaTableDO.setEditTime(new Date());
            tbMetaTableDO.setEditUserId(user.getUserId());
            tbMetaTableDO.setEditUserName(user.getName());
            tbMetaTableDO.setId(metaStaTableDTO.getId());
            boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
            //只有管理员和创建人可以修改使用人和共同编辑人信息
            if (!isAdmin && !user.getUserId().equals(old.getCreateUserId())) {
                tbMetaTableDO.setUseRange(null);
                tbMetaTableDO.setUsePersonInfo(null);
                tbMetaTableDO.setCommonEditPersonInfo(null);
                tbMetaTableDO.setResultViewRange(null);
                tbMetaTableDO.setResultViewPersonInfo(null);
            }
            tbMetaTableMapper.update(enterpriseId, tbMetaTableDO);
            if (old.getLocked() == 1) {
                //检查项
                List<TbDisplayTableItemDTO> checkItemVOS = metaStaTableDTO.getTableItemList();
                //检查内容
                List<TbDisplayTableItemDTO> tableContentList = metaStaTableDTO.getTableContentList();
                if (CollectionUtils.isNotEmpty(tableContentList)) {
                    checkItemVOS.addAll(tableContentList);
                }
                List<TbMetaDisplayTableColumnDO> columnDOS = tbMetaDisplayTableColumnMapper.listByTableIdList(enterpriseId, Collections.singletonList(metaStaTableDTO.getId()));
                Set<Long> columnSet = columnDOS.stream().map(data -> data.getId()).collect(Collectors.toSet());
                checkItemVOS.stream().filter(data -> columnSet.contains(data.getId())).forEach(ck -> {
                    TbMetaDisplayTableColumnDO tableColumnDO = new TbMetaDisplayTableColumnDO();
                    tableColumnDO.setColumnName(ck.getColumnName());
                    tableColumnDO.setDescription(ck.getDescription());
                    tableColumnDO.setStandardPic(ck.getStandardPic());
                    // 检查项新增
                    tableColumnDO.setMetaTableId(tbMetaTableDO.getId());
                    tableColumnDO.setQuickColumnId(ck.getQuickColumnId());
                    tableColumnDO.setId(ck.getId());
                    tableColumnDO.setOrderNum(ck.getOrderNum());
                    tableColumnDO.setScore(ck.getScore() == null ? new BigDecimal(10) : ck.getScore());
                    tableColumnDO.setIsAiCheck(ck.getIsAiCheck());
                    tableColumnDO.setAiCheckStdDesc(ck.getAiCheckStdDesc());
                    tableColumnDO.setMustPic(ck.getMustPic());
                    tbMetaDisplayTableColumnMapper.updateByPrimaryKeySelective(enterpriseId, tableColumnDO);
                });
                return tbMetaTableDO;
            }
            //删除老数据项
            tbMetaDisplayTableColumnMapper.deleteColumnByMetaTableId(enterpriseId, Collections.singletonList(metaStaTableDTO.getId()));

        } else {
            //2.2检查表新增
            tbMetaTableDO.setCreateUserId(user.getUserId());
            tbMetaTableDO.setCreateTime(new Date());
            tbMetaTableDO.setCreateUserName(user.getName());
            tbMetaTableDO.setSupportScore(1);
            tbMetaTableDO.setTableProperty(tableProperty != null && tableProperty == 1 ? 1 : 0);
            tbMetaTableMapper.insertTable(enterpriseId, tbMetaTableDO);
        }
        //2.检查项新增或修改
        List<TbDisplayTableItemDTO> checkItemVOS = metaStaTableDTO.getTableItemList();
        //checkType 为false的时候 是检查项
        this.hangldCheckColunm(enterpriseId, user, tbMetaTableDO, checkItemVOS, Boolean.FALSE);
        //检查内容新增或者修改
        List<TbDisplayTableItemDTO> tableContentList = metaStaTableDTO.getTableContentList();
        //checkType 为false的时候 是检查内容
        this.hangldCheckColunm(enterpriseId, user, tbMetaTableDO, tableContentList, Boolean.TRUE);
        updateMetaTableAuth(enterpriseId, tbMetaTableDO);
        return tbMetaTableDO;
    }

    public PatrolMetaDTO updateMetaTableAuth(String enterpriseId, TbMetaTableDO tbMetaTableDO){
        PatrolMetaDTO patrolMetaDTO = userPersonInfoService.dealMetaTableUserInfo(enterpriseId, tbMetaTableDO, false);
        // 同步到用户检查表权限
        List<TbMetaTableUserAuthDO> authList = TbMetaTableUserAuthDO.buildUserAuthList(patrolMetaDTO.getCommonEditUserIds(), patrolMetaDTO.getUseUserIds(), patrolMetaDTO.getResultViewUserIds(), tbMetaTableDO.getId(), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE);
        tbMetaTableUserAuthDAO.batchAddOrUpdate(enterpriseId, authList);
        Set<String> authUserIds = CollStreamUtil.toSet(authList, TbMetaTableUserAuthDO::getUserId);
        if (CollectionUtils.isNotEmpty(authList)) {
            // 删除其他用户权限
            tbMetaTableUserAuthDAO.deleteByBusinessIds(enterpriseId, Collections.singletonList(String.valueOf(tbMetaTableDO.getId())), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE, new ArrayList<>(authUserIds));
        }
        return patrolMetaDTO;
    }


    /**
     * 检查项或者检查内容新增或者修改
     *
     * @param enterpriseId
     * @param user
     * @param tbMetaTableDO
     * @param list
     * @param checkType
     */
    public void hangldCheckColunm(String enterpriseId, CurrentUser user, TbMetaTableDO tbMetaTableDO, List<TbDisplayTableItemDTO> list, boolean checkType) {
        int sortNum = 1;
        if (list != null) {
            for (TbDisplayTableItemDTO ck : list) {
                TbMetaDisplayTableColumnDO tableColumnDO = new TbMetaDisplayTableColumnDO();
                //如果是高级检查表，这个地方使用检查内容名称
                tableColumnDO.setColumnName(ck.getColumnName());
                tableColumnDO.setDescription(ck.getDescription());
                tableColumnDO.setStandardPic(ck.getStandardPic());
                tableColumnDO.setOrderNum(sortNum);
                // 检查内容新增
                tableColumnDO.setCreateUserId(user.getUserId());
                tableColumnDO.setMetaTableId(tbMetaTableDO.getId());
                tableColumnDO.setCreateUserName(user.getName());
                tableColumnDO.setCreateTime(new Date());
                tableColumnDO.setQuickColumnId(ck.getQuickColumnId());
                tableColumnDO.setScore(ck.getScore() == null ? new BigDecimal(10) : ck.getScore());
                tableColumnDO.setIsAiCheck(ck.getIsAiCheck());
                tableColumnDO.setAiCheckStdDesc(ck.getAiCheckStdDesc());
                //是否是检查内容
                if (checkType) {
                    tableColumnDO.setCheckType(1);
                }
                tableColumnDO.setMustPic(ck.getMustPic());
                tbMetaDisplayTableColumnMapper.insertSelective(enterpriseId, tableColumnDO);
                sortNum++;
            }
        }
    }


    @Override
    public boolean delTbDisplay(String enterpriseId, CurrentUser user, List<Long> metaTableIds) {

        List<TbMetaTableDO> tableDOList = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIds);

        //软删除id
        List<Long> updateIdList = tableDOList.stream().filter(data -> data.getLocked() == 1).map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(updateIdList)) {
//            tbMetaDisplayTableColumnMapper.deleteColumnByMetaTableId(enterpriseId, updateIdList);
            tbMetaTableMapper.updateDelByIds(enterpriseId, updateIdList);
        }
        //物理删除id
        List<Long> deleteIdList = tableDOList.stream().filter(data -> data.getLocked() == 0).map(TbMetaTableDO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            tbMetaDisplayTableColumnMapper.deleteAbsoluteByTableIdList(enterpriseId, deleteIdList);
            tbMetaTableMapper.deleteByIdList(enterpriseId, deleteIdList);
            tbMetaTableUserAuthDAO.deleteByBusinessIds(enterpriseId, deleteIdList.stream().map(String::valueOf).collect(Collectors.toList()), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE);
        }
        return Boolean.TRUE;
    }

    @Override
    public UnifyTbDisplayTableDTO displayMetaTableDetailList(String enterpriseId, TbDisplayTableQuery query) {
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, query.getDisplayTableId());


        //获取当前用户
        CurrentUser user = UserHolder.getUser();
        //判断当前是不是管理员
        //判断是否是管理员
        Boolean adminFlag = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());

        UnifyTbDisplayTableDTO unifyTemplateDO = new UnifyTbDisplayTableDTO();

        unifyTemplateDO.setAdminIs(adminFlag);
        unifyTemplateDO.setShareGroupName(tbMetaTableDO.getShareGroupName());
        unifyTemplateDO.setShareGroup(tbMetaTableDO.getShareGroup());
        unifyTemplateDO.setCreateUserId(tbMetaTableDO.getCreateUserId());
        unifyTemplateDO.setCreateUserName(tbMetaTableDO.getCreateUserName());
        unifyTemplateDO.setUpdateUserId(tbMetaTableDO.getEditUserId());
        unifyTemplateDO.setUpdateUserName(tbMetaTableDO.getEditUserName());
        unifyTemplateDO.setCreateTime(tbMetaTableDO.getCreateTime().getTime());
        unifyTemplateDO.setUpdateTime(tbMetaTableDO.getEditTime().getTime());
        unifyTemplateDO.setName(tbMetaTableDO.getTableName());
        unifyTemplateDO.setId(tbMetaTableDO.getId());
        List<TbMetaDisplayTableColumnDO> allCheckItems = tbMetaDisplayTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(query.getDisplayTableId()));
        unifyTemplateDO.setCheckItems(allCheckItems);
        unifyTemplateDO.setUseRange(tbMetaTableDO.getUseRange());
        unifyTemplateDO.setUsePersonInfo(tbMetaTableDO.getUsePersonInfo());
        unifyTemplateDO.setCommonEditPersonInfo(tbMetaTableDO.getCommonEditPersonInfo());
        unifyTemplateDO.setEditFlag(false);
        if (adminFlag) {
            unifyTemplateDO.setEditFlag(true);
        }else{
            List<String> editAuthTableIds = tbMetaTableUserAuthDAO.getEditAuthTableIds(enterpriseId, user.getUserId(), Collections.singletonList(query.getDisplayTableId()));
            unifyTemplateDO.setEditFlag(editAuthTableIds.contains(String.valueOf(query.getDisplayTableId())));
        }
        //共同编辑人列表
        List<PersonDTO> personDTOList = new ArrayList<>();
        unifyTemplateDO.setCommonEditUserList(personDTOList);
        return unifyTemplateDO;
    }

    @Override
    public TbDisplayTaskShowVO getQuickColumnIdListByMetaTableId(String enterpriseId, Long metaTableId) {
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
        if (tbMetaTableDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR.getCode(), "检查表不存在【" + metaTableId + "】");
        }
        List<TbMetaDisplayTableColumnDO> tbMetaDisplayTableColumnDOList = tbMetaDisplayTableColumnMapper.listByMetaTableId(enterpriseId, metaTableId);
        List<Long> quickColumnIdList = tbMetaDisplayTableColumnDOList.stream().map(TbMetaDisplayTableColumnDO::getQuickColumnId).collect(Collectors.toList());
        TbDisplayTaskShowVO vo = new TbDisplayTaskShowVO();
        vo.setTbMetaDisplayTableColumnDOList(tbMetaDisplayTableColumnDOList);
        return vo;
    }

    @Override
    public int updateLockedByIds(String enterpriseId, List<Long> metaTableIds) {
        return tbMetaTableMapper.updateLockedByIds(enterpriseId, metaTableIds);
    }

    @Override
    public Boolean hasResultAuth(String enterpriseId, Long metaTableId, String userId) {
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(enterpriseId, metaTableId);
        if (tableDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "检查表不存在,id:" + metaTableId);
        }
        if (UserRangeTypeEnum.ALL.getType().equals(tableDO.getResultViewRange())) {
            return true;
        }
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, userId);
        if(isAdmin){
            return true;
        }
        List<TbMetaTableUserAuthDO> tableAuth = tbMetaTableUserAuthDAO.getTableAuth(enterpriseId, userId, Collections.singletonList(metaTableId));
        if (CollectionUtils.isEmpty(tableAuth)) {
            return Boolean.TRUE;
        }
        Map<String, TbMetaTableUserAuthDO> userAuthMap = tableAuth.stream().collect(Collectors.toMap(o -> o.getBusinessId() + Constants.MOSAICS + o.getUserId(), o -> o, (o1, o2) -> o1));
        TbMetaTableUserAuthDO userAuth = userAuthMap.get(metaTableId + Constants.MOSAICS + userId);
        TbMetaTableUserAuthDO allUserAuth = userAuthMap.get(metaTableId + Constants.MOSAICS + "all_user_id");
        boolean userResultAuth = Optional.ofNullable(userAuth).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
        boolean allResultAuth = Optional.ofNullable(allUserAuth).map(TbMetaTableUserAuthDO::getViewAuth).orElse(false);
        Boolean hasResultAuth = userResultAuth || allResultAuth;
        Boolean isCreator = userId.equals(tableDO.getCreateUserId()) ? Boolean.TRUE : Boolean.FALSE;
        return hasResultAuth || isCreator;
    }

    @Override
    public Boolean raiseCheck(String enterpriseId, Long tableId) {
        TbMetaTableDO tableDO = tbMetaTableMapper.selectById(enterpriseId, tableId);
        if (tableDO == null) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_INVALID_ERROR.getCode(), "检查表不存在,id:" + tableId);
        }
        if (tableDO.getLocked() == 1 || tableDO.getTableProperty() == 1) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public void raiseStaTable(String enterpriseId, Long id, CurrentUser user) {

        DataSourceHelper.reset();
        EnterpriseStoreCheckSettingDO enterpriseStoreCheckSettingDO = enterpriseStoreCheckSettingMapper.getEnterpriseStoreCheckSetting(enterpriseId);
        Boolean uploadImgNeed = enterpriseStoreCheckSettingDO != null && enterpriseStoreCheckSettingDO.getUploadImgNeed();

        Boolean uploadLocalImg = enterpriseStoreCheckSettingDO != null && enterpriseStoreCheckSettingDO.getUploadLocalImg();
        int mustPic = 0;
        if (uploadImgNeed && uploadLocalImg) {
            mustPic = 2;
        } else if (uploadImgNeed) {
            mustPic = 1;
        }
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        //1.检查表升级为高级检查表
        tbMetaTableMapper.raiseStaTable(enterpriseId, id);
        List<TbMetaStaTableColumnDO> columnList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId, Collections.singletonList(id), Boolean.FALSE);
        //2.修改检查项级别为一般
        tbMetaStaTableColumnMapper.updateLevelByTableId(enterpriseId, id, "general");
        Date now = new Date(System.currentTimeMillis());
        List<TbMetaColumnResultDO> columnResult = new ArrayList<>();
        //3.配置默认结果项
        Integer finalMustPic = mustPic;
        columnList.stream().forEach(data -> {
            TbMetaColumnResultDO pass = TbMetaColumnResultDO.builder()
                    .metaColumnId(data.getId())
                    .metaTableId(data.getMetaTableId())
                    .mappingResult("PASS")
                    .resultName("合格")
                    .money(data.getAwardMoney() == null ? new BigDecimal(Constants.ZERO_STR) : data.getAwardMoney())
                    .mustPic(finalMustPic)
                    .orderNum(0)
                    .score(data.getSupportScore() == null ? new BigDecimal(1) : data.getSupportScore())
                    .createUserId(user.getUserId())
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            columnResult.add(pass);
            TbMetaColumnResultDO fail = TbMetaColumnResultDO.builder()
                    .metaColumnId(data.getId())
                    .metaTableId(data.getMetaTableId())
                    .mappingResult("FAIL")
                    .resultName("不合格")
                    .money(data.getPunishMoney() == null ? new BigDecimal(Constants.ZERO_STR) : (data.getPunishMoney().abs().multiply(new BigDecimal("-1"))))
                    .mustPic(finalMustPic)
                    .orderNum(0)
                    .score(new BigDecimal(Constants.ZERO_STR))
                    .createUserId(user.getUserId())
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            columnResult.add(fail);
            TbMetaColumnResultDO inapplicable = TbMetaColumnResultDO.builder()
                    .metaColumnId(data.getId())
                    .metaTableId(data.getMetaTableId())
                    .mappingResult("INAPPLICABLE")
                    .resultName("不适用")
                    .money(new BigDecimal(Constants.ZERO_STR))
                    .mustPic(finalMustPic)
                    .orderNum(0)
                    .score(new BigDecimal(Constants.ZERO_STR))
                    .createUserId(user.getUserId())
                    .deleted(0)
                    .description("ignore")
                    .createTime(now)
                    .build();
            columnResult.add(inapplicable);
        });
        if (CollectionUtils.isNotEmpty(columnResult)) {
            tbMetaColumnResultMapper.batchInsert(enterpriseId, columnResult);
        }
    }

    @Override
    public List<TbMetaTableDO> getAll(String enterpriseId, String tableType, List<Long> metaTableIds) {
        return tbMetaTableMapper.getAll(enterpriseId, tableType, metaTableIds);
    }

    @Override
    public List<MetaTableColumnSimpleVO> getAllColumnByTableId(String enterpriseId, Long metaTableId) {
        List<MetaTableColumnSimpleVO> result = new ArrayList<>();
        TbMetaTableDO table = tbMetaTableMapper.selectByPrimaryKey(enterpriseId, metaTableId);
        if (table == null) {
            return result;
        }
        if (!TableTypeUtil.isUserDefinedTable(table.getTableProperty(), table.getTableType())) {
            List<TbMetaStaTableColumnDO> staColumnList = tbMetaStaTableColumnMapper.selectColumnListByTableIdList(enterpriseId,
                    Collections.singletonList(metaTableId), Boolean.FALSE);
            if (CollectionUtils.isNotEmpty(staColumnList)) {
                staColumnList.forEach(col -> {
                    MetaTableColumnSimpleVO vo = new MetaTableColumnSimpleVO(col.getId(), col.getColumnName());
                    result.add(vo);
                });
            }
        }
        if (UnifyTaskDataTypeEnum.TB_DISPLAY.getCode().equals(table.getTableType())) {
            List<TbMetaDisplayTableColumnDO> disColumnList = tbMetaDisplayTableColumnMapper.selectColumnListByTableIdList(enterpriseId,
                    Collections.singletonList(metaTableId));
            if (CollectionUtils.isNotEmpty(disColumnList)) {
                disColumnList.forEach(col -> {
                    MetaTableColumnSimpleVO vo = new MetaTableColumnSimpleVO(col.getId(), col.getColumnName());
                    result.add(vo);
                });
            }
        }
        if (TableTypeUtil.isUserDefinedTable(table.getTableProperty(), table.getTableType())) {
            List<TbMetaDefTableColumnDO> defColumnList = tbMetaDefTableColumnMapper.getAllColumnByMetaTableIdList(enterpriseId,
                    Collections.singletonList(metaTableId));
            if (CollectionUtils.isNotEmpty(defColumnList)) {
                defColumnList.forEach(col -> {
                    MetaTableColumnSimpleVO vo = new MetaTableColumnSimpleVO(col.getId(), col.getColumnName());
                    result.add(vo);
                });
            }
        }
        return result;
    }

    @Override
    public MetaStaTableVO addStaMetaTableByTemplate(String enterpriseId, Long metaTableTemplateId, CurrentUser user) {

        MetaTableDetailDTO metaTableDetailDTO = metaTableRpcService.getMetaTableDetail(metaTableTemplateId);
        if (metaTableDetailDTO == null) {
            throw new ServiceException(ErrorCodeEnum.METATABLE_TEMPLATE_NOT_EXIST);
        }
        TbMetaStaTableDTO metaStaTableDTO = new TbMetaStaTableDTO();
        String tableName = metaTableDetailDTO.getTableName();
        if (tbMetaTableMapper.countCheckTableByName(enterpriseId, metaTableDetailDTO.getTableName()) > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            tableName = "(副本" + LocalDateTime.now().format(formatter) + ")" + tableName;
        }
        metaStaTableDTO.setTableName(tableName);
        metaStaTableDTO.setTableType(UnifyTaskDataTypeEnum.STANDARD.getCode());
        metaStaTableDTO.setTableProperty(metaTableDetailDTO.getTableProperty());
        metaStaTableDTO.setDescription(metaTableDetailDTO.getDescription());
        metaStaTableDTO.setDefaultResultColumn(metaTableDetailDTO.getDefaultResultColumn());
        metaStaTableDTO.setNoApplicableRule(metaTableDetailDTO.getNoApplicableRule());
        metaStaTableDTO.setLevelRule(metaTableDetailDTO.getLevelRule());
        metaStaTableDTO.setLevelInfo(metaTableDetailDTO.getLevelInfo());
        List<String> categoryNameList = Lists.newArrayList();
        List<TbMetaStaColumnDTO> staColumnDTOList = metaTableDetailDTO.getStaColumnList().stream().map(a -> {
            if (!categoryNameList.contains(a.getCategoryName())) {
                categoryNameList.add(a.getCategoryName());
            }
            TbMetaStaColumnDTO tbMetaStaColumnDTO = new TbMetaStaColumnDTO();
            BeanUtils.copyProperties(a, tbMetaStaColumnDTO);
            List<TbMetaColumnResultDTO> columnResultDTOList = a.getColumnResultList().stream().map(x -> {
                TbMetaColumnResultDTO tbMetaColumnResultDTO = new TbMetaColumnResultDTO();
                BeanUtils.copyProperties(x, tbMetaColumnResultDTO);
                return tbMetaColumnResultDTO;
            }).collect(Collectors.toList());
            // 结果项
            tbMetaStaColumnDTO.setColumnResultDTOList(columnResultDTOList);
            return tbMetaStaColumnDTO;
        }).collect(Collectors.toList());
        metaStaTableDTO.setStaColumnDTOList(staColumnDTOList);
        metaStaTableDTO.setCategoryNameList(categoryNameList);
        metaStaTableDTO.setUseRange(UserRangeTypeEnum.ALL.getType());
        metaStaTableDTO.setResultViewRange(UserRangeTypeEnum.ALL.getType());
        log.info("根据模板库创建检查表构建参数={}", JSON.toJSONString(metaStaTableDTO));
        TbMetaTableDO tbMetaTableDO = this.saveSta(enterpriseId, user, metaStaTableDTO);
        return this.getMetaTableDetail(enterpriseId, tbMetaTableDO.getId(), user.getUserId(), null);
    }

    void userPersonInf(String enterpriseId, TbMetaTableDO tbMetaTableDO, ConfigMetaDefTableParam param, String userId) {
        tbMetaTableDO.setUsePersonInfo(param.getUsePersonInfo());
        tbMetaTableDO.setUseRange(param.getUseRange());
        //使用人同时作为结果查看人
        if (param.getResultViewUserWithUserRang() != null && param.getResultViewUserWithUserRang()) {
            if (UserRangeTypeEnum.ALL.getType().equals(param.getUseRange())) {
                tbMetaTableDO.setResultViewPersonInfo("");
                tbMetaTableDO.setResultViewRange(UserRangeTypeEnum.ALL.getType());
            } else if (UserRangeTypeEnum.SELF.getType().equals(param.getUseRange())) {
                tbMetaTableDO.setResultViewPersonInfo("");
                tbMetaTableDO.setResultViewRange(UserRangeTypeEnum.SELF.getType());
            } else if (UserRangeTypeEnum.PART.getType().equals(param.getUseRange()) && UserRangeTypeEnum.PART.getType().equals(param.getResultViewRange())) {
                List<PersonUsePositionDTO> userPersonPositionDTOList = JSONObject.parseArray(param.getUsePersonInfo(), PersonUsePositionDTO.class);
                List<PersonUsePositionDTO> resultPersonPositionDTOList = new ArrayList<>();
                if (StringUtils.isNotBlank(param.getResultViewPersonInfo())) {
                    resultPersonPositionDTOList = JSONObject.parseArray(param.getResultViewPersonInfo(), PersonUsePositionDTO.class);
                }
                resultPersonPositionDTOList.addAll(userPersonPositionDTOList);
                resultPersonPositionDTOList = resultPersonPositionDTOList.stream().distinct().collect(Collectors.toList());

                tbMetaTableDO.setResultViewPersonInfo(JSONObject.toJSONString(resultPersonPositionDTOList));
                tbMetaTableDO.setResultViewRange(UserRangeTypeEnum.PART.getType());
            } else {
                tbMetaTableDO.setResultViewPersonInfo(param.getResultViewPersonInfo());
                tbMetaTableDO.setResultViewRange(param.getResultViewRange());
            }
        } else {
            tbMetaTableDO.setResultViewPersonInfo(param.getResultViewPersonInfo());
            tbMetaTableDO.setResultViewRange(param.getResultViewRange());
        }
        tbMetaTableDO.setCommonEditPersonInfo(getEditPersonInfo(enterpriseId, param.getCommonEditUserIdList(), null));
    }

    @Override
    public List<TbMetaColumnResultDTO> getMetaColumnResultList(String enterpriseId, List<TbMetaColumnResultDO> columnResultDOList) {
        List<TbMetaColumnResultDTO> columnResultDTOList = new ArrayList<>();
        columnResultDOList.forEach(columnReason -> {
            JSONObject extendInfo = JSONObject.parseObject(columnReason.getExtendInfo());
            TbMetaColumnResultDTO tbMetaColumnResultDTO = new TbMetaColumnResultDTO();
            tbMetaColumnResultDTO.setId(columnReason.getId());
            tbMetaColumnResultDTO.setResultName(columnReason.getResultName());
            tbMetaColumnResultDTO.setScore(columnReason.getScore());
            tbMetaColumnResultDTO.setMoney(columnReason.getMoney());
            tbMetaColumnResultDTO.setMappingResult(columnReason.getMappingResult());
            tbMetaColumnResultDTO.setMustPic(columnReason.getMustPic());
            tbMetaColumnResultDTO.setMetaColumnId(columnReason.getMetaColumnId());
            tbMetaColumnResultDTO.setOrderNum(columnReason.getOrderNum());
            tbMetaColumnResultDTO.setDescription(columnReason.getDescription());
            tbMetaColumnResultDTO.setMaxScore(columnReason.getMaxScore());
            tbMetaColumnResultDTO.setMinScore(columnReason.getMinScore());
            tbMetaColumnResultDTO.setAiMaxScore(Objects.nonNull(extendInfo) ? extendInfo.getBigDecimal("aiMaxScore") : null);
            tbMetaColumnResultDTO.setAiMinScore(Objects.nonNull(extendInfo) ? extendInfo.getBigDecimal("aiMinScore") : null);
            tbMetaColumnResultDTO.setScoreIsDouble(columnReason.getScoreIsDouble());
            tbMetaColumnResultDTO.setAwardIsDouble(columnReason.getAwardIsDouble());
            tbMetaColumnResultDTO.setMetaTableId(columnReason.getMetaTableId());
            columnResultDTOList.add(tbMetaColumnResultDTO);
        });
        return columnResultDTOList;
    }

    @Override
    public List<SopDTO> sopTreeList(String enterpriseId, CurrentUser user, SopTreeListRequest param) {
        log.info("sopTreeList enterpriseId：{}，param：{}", enterpriseId, JSONObject.toJSONString(param));
        List<SopDTO> sopList = new ArrayList<>();
        Boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        if (StringUtils.isBlank(param.getName())) {
            TbMetaTableDO tbMetaTableDO = new TbMetaTableDO();
            if (param.getPid() == -1) {
                tbMetaTableDO.setSopPath(Constants.ROOT_DELETE_REGION_PATH);
            } else {
                tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, param.getPid());
                tbMetaTableDO.setSopPath(tbMetaTableDO.getSopPath() + tbMetaTableDO.getId() + "/");
            }
            List<String> authMetaTableIds = null;
            if(!isAdmin){
                List<TbMetaTableUserAuthDO> userAuthMetaTableList = tbMetaTableUserAuthDAO.getUserAuthMetaTableList(enterpriseId, user.getUserId());
                if(CollectionUtils.isEmpty(userAuthMetaTableList)){
                    return Lists.newArrayList();
                }
                authMetaTableIds = userAuthMetaTableList.stream().map(TbMetaTableUserAuthDO::getBusinessId).distinct().collect(Collectors.toList());
            }
            sopList = tbMetaTableMapper.selectBySopType(enterpriseId, Constants.LEAF, tbMetaTableDO.getSopPath(),authMetaTableIds,user.getUserId());
            for (SopDTO sopDTO : sopList) {
                sopDTO.setNodePath(sopDTO.getNodePath() + sopDTO.getId() + "/");
            }
            List<String> nodePathList = sopList.stream().map(SopDTO::getNodePath).collect(Collectors.toList());
            List<SopDTO> subList = tbMetaTableMapper.selectByNodePathList(enterpriseId, nodePathList,authMetaTableIds,user.getUserId());
            Map<String, List<SopDTO>> subSopMap = ListUtils.emptyIfNull(subList)
                    .stream()
                    .collect(Collectors.groupingBy(SopDTO::getNodePath));
            for (SopDTO sopDTO : sopList) {
                List<SopDTO> subSopList = sopDTO.getSubSopList();
                List<SopDTO> sopMapList = subSopMap.get(sopDTO.getNodePath());
                if (CollectionUtils.isEmpty(sopMapList)) {
                    continue;
                }
                subSopList.addAll(sopMapList);
            }
        } else {
            sopList = tbMetaTableMapper.selectByName(enterpriseId, Constants.LEAF, param.getName());
        }
        return sopList;
    }

    @Override
    public boolean addSopNode(String enterpriseId, CurrentUser user, AddSopNodeRequest param) {
        log.info("addSopNode enterpriseId：{}，param：{}", enterpriseId, JSONObject.toJSONString(param));
        if (StringUtils.isBlank(enterpriseId) || Objects.isNull(param)) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_VALIDATE_ERROR);
        }
        String isExist = tbMetaTableMapper.judgeName(enterpriseId, param.getNodeName(), null);
        if (StringUtils.isNotBlank(isExist)) {
            throw new ServiceException(ErrorCodeEnum.SOP_CHECK_LIST_IS_REPEATED);
        }
        TbMetaTableDO addParam = new TbMetaTableDO();
        addParam.setTableName(param.getNodeName());
        addParam.setDescription("SOP节点");
        addParam.setCreateUserId(user.getUserId());
        addParam.setCreateUserName(user.getName());
        addParam.setNoApplicableRule(false);
        addParam.setTableProperty(-1);
        addParam.setSopType(Constants.LEAF);
        addParam.setCommonEditPersonInfo(param.getCommonEditPersonInfo());
        addParam.setResultViewRange(param.getResultViewRange());
        addParam.setResultViewPersonInfo(param.getResultViewPersonInfo());
        addParam.setUsePersonInfo(param.getUsePersonInfo());
        addParam.setUseRange(param.getUseRange());
         if (param.getParentNode() == null || param.getParentNode() == -1) {
            addParam.setSopPath(Constants.ROOT_DELETE_REGION_PATH);
        } else {
            TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, param.getParentNode());
            addParam.setSopPath(tbMetaTableDO.getSopPath() + tbMetaTableDO.getId() + "/");
        }
        tbMetaTableMapper.addSopNode(enterpriseId, addParam);
         updateMetaTableAuth(enterpriseId, addParam);
        return true;
    }

    @Override
    public String updateSopNode(String enterpriseId, CurrentUser user, UpdateSopNodeRequest param) {
        String isExist = tbMetaTableMapper.judgeName(enterpriseId, param.getNodeName(), param.getId());
        if (StringUtils.isNotBlank(isExist)) {
            throw new ServiceException(ErrorCodeEnum.SOP_CHECK_LIST_IS_REPEATED);
        }
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, param.getId());
        tbMetaTableDO.setTableName(param.getNodeName());
        tbMetaTableDO.setCommonEditPersonInfo(param.getCommonEditPersonInfo());
        tbMetaTableDO.setResultViewRange(param.getResultViewRange());
        tbMetaTableDO.setResultViewPersonInfo(param.getResultViewPersonInfo());
        tbMetaTableDO.setUseRange(param.getUseRange());
        tbMetaTableDO.setUsePersonInfo(param.getUsePersonInfo());
        int result = tbMetaTableMapper.update(enterpriseId, tbMetaTableDO);
        // 关联大区
        if (StringUtils.isNotBlank(param.getCommonEditPersonInfo())
                || StringUtils.isNotBlank(param.getUseRange())) {
            tbMetaTableMapper.batchUserIdByUpdatePath(enterpriseId, tbMetaTableDO.getSopPath() + tbMetaTableDO.getId() + '/', param.getUsePersonInfo(),
                    param.getUseRange(),
                    param.getResultViewPersonInfo(), param.getResultViewRange(),
                    param.getCommonEditPersonInfo());
        }
        updateMetaTableAuth(enterpriseId, tbMetaTableDO);
        return param.getNodeName();
    }

    @Override
    public SopGroupDTO sopNodeDetail(String enterpriseId, Long id) {
        //查出将要删除的数据
        TbMetaTableDO metaTableDO = tbMetaTableMapper.selectById(enterpriseId, id);
        if (metaTableDO == null){
            throw new ServiceException("分组不存在");
        }
        SopGroupDTO sopGroupDTO = new SopGroupDTO();
        sopGroupDTO.setNodeName(metaTableDO.getTableName());
        sopGroupDTO.setId(metaTableDO.getId());
        sopGroupDTO.setCommonEditPersonInfo(metaTableDO.getCommonEditPersonInfo());
        sopGroupDTO.setUsePersonInfo(metaTableDO.getUsePersonInfo());
        sopGroupDTO.setUseRange(metaTableDO.getUseRange());
        sopGroupDTO.setResultViewPersonInfo(metaTableDO.getResultViewPersonInfo());
        sopGroupDTO.setResultViewRange(metaTableDO.getResultViewRange());
        return sopGroupDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSopNode(String enterpriseId, CurrentUser user, Long id) {
        //查出将要删除的数据
        TbMetaTableDO deletedDo = tbMetaTableMapper.selectById(enterpriseId, id);
        if (deletedDo.getTableName().equals("默认分组") && deletedDo.getSopType().equals(Constants.LEAF)){
            throw new ServiceException("默认分组不可删除");
        }
        deletedDo.setDeleted(1);
        tbMetaTableMapper.update(enterpriseId, deletedDo);
        String childPath = deletedDo.getSopPath() + deletedDo.getId() + "/";
        tbMetaTableMapper.moveDefaultGroup(enterpriseId,childPath,Constants.CHILD);
        return true;
    }

    @Override
    public boolean moveSopNode(String enterpriseId, CurrentUser user, List<Long> ids, long pid) {
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, pid);
        String path = tbMetaTableDO.getSopPath() + tbMetaTableDO.getId() + "/";
        tbMetaTableMapper.batchUpdatePath(enterpriseId, path, ids);
        return true;
    }

    @Override
    public TbMetaQuickColumnResultDO findColumnManAndMin(String enterpriseId, Long columnId) {
        TbMetaStaTableColumnDO tbMetaStaTableColumnDO = tbMetaStaTableColumnMapper.selectByPrimaryKey(enterpriseId, columnId);
        if (Objects.nonNull(tbMetaStaTableColumnDO) && tbMetaStaTableColumnDO.getQuickColumnId() != null){
            TbMetaQuickColumnResultDO failDetailByQuickId = tbMetaQuickColumnResultMapper.getFailDetailByQuickId(enterpriseId, tbMetaStaTableColumnDO.getQuickColumnId());
            return failDetailByQuickId;
        }
        return null;
    }

    @Override
    public List<TbMetaTableDO> getDisplayTableAndUsedUserContainUserId(String enterpriseId, String userId, String name, String startTime, String endTime) {
        List<TbMetaTableUserAuthDO> tableList = tbMetaTableUserAuthDAO.getUserAuthMetaTableList(enterpriseId, userId);
        List<Long> metaTableIds = tableList.stream().map(TbMetaTableUserAuthDO::getBusinessId).map(Long::valueOf).distinct().collect(Collectors.toList());
        return tbMetaTableMapper.selectDisplayTableAndUsedUserContainUserId(enterpriseId, metaTableIds, name, startTime, endTime);
    }

    @Override
    public Boolean updateTableCreateUser(String enterpriseId, UpdateTableCreateUserRequest request) {
        String createUserId = request.getCreateUserId();
        String username = enterpriseUserDao.selectNameByUserId(enterpriseId, createUserId);
        if(Objects.isNull(username)){
            throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
        }
        TbMetaTableDO tbMetaTableDO = tbMetaTableMapper.selectById(enterpriseId, request.getMetaTableId());
        if(Objects.isNull(tbMetaTableDO)){
            throw new ServiceException(ErrorCodeEnum.CHECKTABLE_IS_NULL);
        }
        TbMetaTableDO update = new TbMetaTableDO();
        update.setId(request.getMetaTableId());
        update.setCreateUserId(createUserId);
        update.setCreateUserName(username);
        tbMetaTableMapper.update(enterpriseId, update);
        tbMetaTableDO.setCreateUserId(createUserId);
        PatrolMetaDTO patrolMetaDTO = userPersonInfoService.dealMetaTableUserInfo(enterpriseId, tbMetaTableDO, false);
        asyncUpdateMetaTableUser(enterpriseId, patrolMetaDTO, DynamicDataSourceContextHolder.getDataSourceType(), MDC.get(Constants.REQUEST_ID));
        return true;
    }

    void asyncUpdateMetaTableUser(String enterpriseId, PatrolMetaDTO patrolMetaDTO, String dbName, String requestId) {
        log.info("异步更新检查表权限");
        MDCUtils.put(Constants.REQUEST_ID, requestId);
        MDCUtils.put(Constants.MESSAGE_ID, UUIDUtils.get8UUID());
        DataSourceHelper.changeToSpecificDataSource(dbName);
        TbMetaTableDO tbMetaTableDO = patrolMetaDTO.getTbMetaTableDO();
        // 同步到用户检查表权限
        List<TbMetaTableUserAuthDO> authList = TbMetaTableUserAuthDO.buildUserAuthList(patrolMetaDTO.getCommonEditUserIds(), patrolMetaDTO.getUseUserIds(), patrolMetaDTO.getResultViewUserIds(), tbMetaTableDO.getId(), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE);
        tbMetaTableUserAuthDAO.batchAddOrUpdate(enterpriseId, authList);
        Set<String> authUserIds = CollStreamUtil.toSet(authList, TbMetaTableUserAuthDO::getUserId);
        if (CollectionUtils.isNotEmpty(authList)) {
            // 删除其他用户权限
            tbMetaTableUserAuthDAO.deleteByBusinessIds(enterpriseId, Collections.singletonList(String.valueOf(tbMetaTableDO.getId())), TbMetaTableUserAuthDO.TABLE_BUSINESS_TYPE, new ArrayList<>(authUserIds));
        }
    }

    @Override
    public void updateMetaTableUser(String enterpriseId, List<Long> metaTableIds) {
        boolean isContinue = true;
        int pageNum = 1, pageSize = 100;
        while (isContinue){
            PageHelper.startPage(pageNum++, pageSize, false);
            List<TbMetaTableDO> allMetaTable = tbMetaTableMapper.getAllMetaTable(enterpriseId, metaTableIds);
            if(CollectionUtils.isEmpty(allMetaTable)){
                break;
            }
            if(allMetaTable.size() < pageSize){
                isContinue = false;
            }
            try {
                for (TbMetaTableDO tbMetaTableDO : allMetaTable) {
                    if (Constants.INDEX_ONE.equals(tbMetaTableDO.getStatus()) || Constants.INDEX_ONE.equals(tbMetaTableDO.getDeleted())) {
                        continue;
                    }
                    PatrolMetaDTO patrolMetaDTO = updateMetaTableAuth(enterpriseId, tbMetaTableDO);
                    TbMetaTableDO table = patrolMetaDTO.getTbMetaTableDO();
                    tbMetaTableMapper.updateByPrimaryKeySelective(enterpriseId, table);
                }
            } catch (Exception e) {
                log.error("异常了", e);
            }
        }
    }
}
