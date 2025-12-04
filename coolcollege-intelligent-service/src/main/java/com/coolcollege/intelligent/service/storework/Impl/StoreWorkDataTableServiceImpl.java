package com.coolcollege.intelligent.service.storework.Impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.constant.RedisConstant;
import com.coolcollege.intelligent.common.constant.StoreWorkConstant;
import com.coolcollege.intelligent.common.constant.UnifyTaskConstant;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.QuestionCreateTypeEnum;
import com.coolcollege.intelligent.common.enums.TimeCycleEnum;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import com.coolcollege.intelligent.common.enums.patrol.QuestionTypeEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkCycleEnum;
import com.coolcollege.intelligent.common.enums.storework.StoreWorkNoticeEnum;
import com.coolcollege.intelligent.common.enums.video.ResourceStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.DateUtil;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseUserDao;
import com.coolcollege.intelligent.dao.metatable.TbMetaStaTableColumnMapper;
import com.coolcollege.intelligent.dao.metatable.TbMetaTableMapper;
import com.coolcollege.intelligent.dao.storework.dao.*;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dto.EnterpriseQuestionSettingsDTO;
import com.coolcollege.intelligent.dto.EnterpriseStoreWorkSettingsDTO;
import com.coolcollege.intelligent.model.authentication.UserAuthMappingDO;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseUserSingleDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionDTO;
import com.coolcollege.intelligent.model.enterprise.dto.PersonPositionListDTO;
import com.coolcollege.intelligent.model.enums.DingMsgEnum;
import com.coolcollege.intelligent.model.enums.UnifyNodeEnum;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.patrolstore.dto.StaColumnDTO;
import com.coolcollege.intelligent.model.question.dto.BuildQuestionDTO;
import com.coolcollege.intelligent.model.question.dto.QuestionTaskInfoDTO;
import com.coolcollege.intelligent.model.question.request.BuildQuestionRequest;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.region.dto.RegionPathDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import com.coolcollege.intelligent.model.storework.*;
import com.coolcollege.intelligent.model.storework.dto.*;
import com.coolcollege.intelligent.model.storework.request.*;
import com.coolcollege.intelligent.model.storework.vo.*;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.unifytask.dto.GeneralDTO;
import com.coolcollege.intelligent.model.unifytask.dto.TaskProcessDTO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.video.SmallVideoDTO;
import com.coolcollege.intelligent.model.video.SmallVideoInfoDTO;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.authentication.UserAuthMappingService;
import com.coolcollege.intelligent.service.question.QuestionParentInfoService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.storework.StoreWorkDataTableService;
import com.coolcollege.intelligent.service.system.SysRoleService;
import com.coolcollege.intelligent.service.task.JmsTaskService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/9/16 17:02
 * @Version 1.0
 */
@Service
@Slf4j
public class StoreWorkDataTableServiceImpl implements StoreWorkDataTableService {

    @Resource
    RegionService regionService;
    @Resource
    SwStoreWorkDataTableDao swStoreWorkDataTableDao;
    @Resource
    SwStoreWorkRecordDao swStoreWorkRecordDao;
    @Resource
    SysRoleMapper sysRoleMapper;
    @Resource
    SwStoreWorkTableMappingDao swStoreWorkTableMappingDao;
    @Resource
    SwStoreWorkDataTableColumnDao swStoreWorkDataTableColumnDao;
    @Resource
    private TbMetaStaTableColumnMapper tbMetaStaTableColumnMapper;
    @Resource
    private SimpleMessageService simpleMessageService;
    @Resource
    @Lazy
    private JmsTaskService jmsTaskService;
    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;
    @Autowired
    private RedisUtilPool redisUtil;
    @Resource
    private TaskSopService taskSopService;
    @Resource
    private QuestionParentInfoService questionParentInfoService;
    @Resource
    SwStoreWorkDao swStoreWorkDao;
    @Resource
    private UserAuthMappingService userAuthMappingService;
    @Resource
    private SysRoleService sysRoleService;
    @Resource
    private TbMetaTableMapper tbMetaTableMapper;
    @Resource
    private EnterpriseUserDao enterpriseUserDao;

    @Override
    public List<StoreWorkExecutionPageVO> getStoreWorkExecutionPage(String enterpriseId, CurrentUser user, StoreWorkTableRequest request) {
        if (request.getWorkCycle()==null||request.getStoreId()==null||request.getCurrentDate()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //时间处理
        String queryDate = DateUtil.format(request.getCurrentDate(), DateUtils.DATE_FORMAT_DAY);
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTable(enterpriseId, queryDate, request.getWorkCycle(), user.getUserId(),request.getStoreId());
        List<Long> tableMappingIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIds);
        Map<Long, String> tableInfoMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));

        if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
            return Collections.emptyList();
        }
        List<StoreWorkExecutionPageVO> result = new ArrayList<>();
        swStoreWorkDataTableDOS.forEach(x->{
            StoreWorkExecutionPageVO storeWorkExecutionPageVO = new StoreWorkExecutionPageVO();
            storeWorkExecutionPageVO.setBeginTime(x.getBeginTime());
            storeWorkExecutionPageVO.setEndTime(x.getEndTime());
            storeWorkExecutionPageVO.setMetaTableId(x.getMetaTableId());
            storeWorkExecutionPageVO.setCommentStatus(x.getCommentStatus());
            storeWorkExecutionPageVO.setCompleteStatus(x.getCompleteStatus());
            storeWorkExecutionPageVO.setFinishColumnNum(x.getFinishColumnNum());
            storeWorkExecutionPageVO.setTotalColumnNum(x.getTotalColumnNum());
            storeWorkExecutionPageVO.setCollectColumnNum(x.getCollectColumnNum());
            storeWorkExecutionPageVO.setMetaTableName(x.getTableName());
            storeWorkExecutionPageVO.setBusinessId(x.getTcBusinessId());
            storeWorkExecutionPageVO.setWorkCycle(x.getWorkCycle());
            storeWorkExecutionPageVO.setStoreId(x.getStoreId());
            storeWorkExecutionPageVO.setOverdueContinue(x.getOverdueContinue());
            storeWorkExecutionPageVO.setTableInfo(tableInfoMap.getOrDefault(x.getTableMappingId(),""));
            storeWorkExecutionPageVO.setDataTableId(x.getId());
            storeWorkExecutionPageVO.setCurrentDate(request.getCurrentDate());
            storeWorkExecutionPageVO.setIsAiProcess(x.getIsAiProcess());
            result.add(storeWorkExecutionPageVO);
        });
        return result;
    }

    @Override
    public Boolean currentUserStoreWorkAllCommentComplete(String enterpriseId, CurrentUser user, StoreWorkTableRequest storeWorkTableRequest) {
        if (storeWorkTableRequest.getStoreId()==null||storeWorkTableRequest.getCurrentDate()==null||storeWorkTableRequest.getWorkCycle()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //时间处理
        String queryDate = DateUtil.format(storeWorkTableRequest.getCurrentDate(), DateUtils.DATE_FORMAT_DAY);
        List<SwStoreWorkDataTableDO> list = swStoreWorkDataTableDao.noCommentStoreWorkCount(enterpriseId, queryDate,
                storeWorkTableRequest.getWorkCycle(), null, storeWorkTableRequest.getStoreId());
        //如果没有任务，直接返回false
        if (CollectionUtils.isEmpty(list)){
            return Boolean.FALSE;
        }
        //有任务没有点评 直接返回false
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS =  list.stream().filter(x->Constants.INDEX_ZERO.equals(x.getCommentStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(swStoreWorkDataTableDOS)){
            return Boolean.FALSE;
        }
        //有任务 且全部点评完成 放回True
        return Boolean.TRUE;
    }

    @Override
    public StoreWorkOverviewVO getStoreWorkOverViewData(String enterpriseId, CurrentUser user, StoreWorkTableRequest storeWorkTableRequest) {
        //时间处理
        String queryDate = "";
        if (StringUtils.isEmpty(storeWorkTableRequest.getBusinessId())){
            queryDate = DateUtil.format(storeWorkTableRequest.getCurrentDate(), DateUtils.DATE_FORMAT_DAY);
        }
        //当前统计数据不跟人挂钩，后期可能变化
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableList = Collections.emptyList();
        if (storeWorkTableRequest.getBusinessId()!=null){
            swStoreWorkDataTableList = swStoreWorkDataTableDao.getSwStoreWorkDataTableListByBusinessId(enterpriseId,null,storeWorkTableRequest.getBusinessId());
        }else {
            swStoreWorkDataTableList = swStoreWorkDataTableDao.getSwStoreWorkDataTableList(enterpriseId, queryDate,
                    storeWorkTableRequest.getWorkCycle(), null, storeWorkTableRequest.getStoreId());
        }

        StoreWorkOverviewVO storeWorkOverviewVO = new StoreWorkOverviewVO();
        if (CollectionUtils.isEmpty(swStoreWorkDataTableList)){
            return storeWorkOverviewVO;
        }
        storeWorkOverviewVO.setBusinessId(swStoreWorkDataTableList.get(Constants.INDEX_ZERO).getTcBusinessId());
        if (StringUtils.isNotEmpty(storeWorkTableRequest.getBusinessId())){
            SwStoreWorkRecordDO swStoreWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, storeWorkTableRequest.getBusinessId());
            storeWorkOverviewVO.setCommentStatus(swStoreWorkRecordDO.getCommentStatus());
        }

        //过滤自定义表
        swStoreWorkDataTableList = swStoreWorkDataTableList.stream().filter(x -> !x.getTableProperty().equals(MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode())).collect(Collectors.toList());

        //查询汇总数据
        SwStoreWorkRecordDO swStoreWorkRecordDO = swStoreWorkRecordDao.getStoreWorkOverViewData(enterpriseId, queryDate,
                storeWorkTableRequest.getWorkCycle(), storeWorkTableRequest.getStoreId(), Boolean.TRUE,storeWorkTableRequest.getBusinessId());


        StoreWorkOverViewDataDTO storeWorkOverViewData = swStoreWorkDataTableDao.getStoreWorkOverViewData(enterpriseId, queryDate,
                storeWorkTableRequest.getWorkCycle(), null, storeWorkTableRequest.getStoreId(), Boolean.TRUE, storeWorkTableRequest.getBusinessId());


        //合格项率 不合格项率 不适用项率计算时候的分母
        BigDecimal passRate = new BigDecimal("0.00");
        if (swStoreWorkRecordDO.getAvgPassRate().compareTo(new BigDecimal(Constants.INDEX_ZERO))!=0){
            passRate = swStoreWorkRecordDO.getAvgPassRate().multiply(new BigDecimal(100)).setScale(2);
        }
        String passRateStr = String.format("%s%%", passRate);


        Integer num = 0;
        if(storeWorkOverViewData != null){
            num = storeWorkOverViewData.getCommentTotalColumnNum();
        }
        BigDecimal failRate = new BigDecimal("0.00");
        if (storeWorkOverViewData != null && swStoreWorkRecordDO.getFailColumnNum()!=0&&num!=0){
            failRate = new BigDecimal(storeWorkOverViewData.getFailColumnNum()).multiply(new BigDecimal(100)).divide(new BigDecimal(num),2,BigDecimal.ROUND_HALF_UP);
        }
        String failRateStr = String.format("%s%%", failRate);

        BigDecimal inapplicableRate = new BigDecimal("0.00");
        if (storeWorkOverViewData != null &&storeWorkOverViewData.getInapplicableColumnNum()!=0&&num!=0){
            inapplicableRate = new BigDecimal(storeWorkOverViewData.getInapplicableColumnNum()).multiply(new BigDecimal(100)).divide(new BigDecimal(num),2,BigDecimal.ROUND_HALF_UP);
        }
        String inapplicableRateStr = String.format("%s%%", inapplicableRate);

        //平均得分
        BigDecimal avgScore = new BigDecimal("0.00");
        if (swStoreWorkRecordDO.getCommentTableNum()!=0){
            avgScore = swStoreWorkRecordDO.getTotalGetScore().divide(new BigDecimal(swStoreWorkRecordDO.getCommentTableNum()),2,BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal avgPassRate = new BigDecimal("0.00");
        if (swStoreWorkRecordDO.getAvgPassRate().compareTo(new BigDecimal(Constants.INDEX_ZERO))!=0){
            avgPassRate = swStoreWorkRecordDO.getAvgPassRate().multiply(new BigDecimal(100)).setScale(2);
        }
        String avgPassRateStr = String.format("%s%%", avgPassRate);

        BigDecimal avgScoreRate = new BigDecimal("0.00");
        if (swStoreWorkRecordDO.getAvgScoreRate().compareTo(new BigDecimal(Constants.INDEX_ZERO))!=0){
            avgScoreRate = swStoreWorkRecordDO.getAvgScoreRate().multiply(new BigDecimal(100)).setScale(2);
        }
        String avgScoreRateStr = String.format("%s%%", avgScoreRate);

        storeWorkOverviewVO.setTotalColumnNum(swStoreWorkRecordDO.getTotalColumnNum());
        storeWorkOverviewVO.setCollectColumnNum(swStoreWorkRecordDO.getCollectColumnNum());
        storeWorkOverviewVO.setPassColumnNum(swStoreWorkRecordDO.getPassColumnNum());
        storeWorkOverviewVO.setFailColumnNum(swStoreWorkRecordDO.getFailColumnNum());
        storeWorkOverviewVO.setInapplicableColumnNum(swStoreWorkRecordDO.getInapplicableColumnNum());
        storeWorkOverviewVO.setPassRate(passRateStr);
        storeWorkOverviewVO.setFailRate(failRateStr);
        storeWorkOverviewVO.setInapplicableRate(inapplicableRateStr);
        storeWorkOverviewVO.setAvgScore(avgScore);
        storeWorkOverviewVO.setAvgScoreRate(avgScoreRateStr);
        storeWorkOverviewVO.setAvgPassRate(avgPassRateStr);
        return storeWorkOverviewVO;
    }

    @Override
    public List<StoreWorkOverviewVO> getStoreWorkOverViewDataList(String enterpriseId,String workCycle,String storeId,Long currentDate,  CurrentUser user, String businessId) {
        //时间处理 跟人不关联
        String queryDate = "";
        if (StringUtils.isEmpty(businessId)){
            queryDate = DateUtil.format(new Date(currentDate), DateUtils.DATE_FORMAT_DAY);
        }
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableList = Collections.emptyList();
        if (StringUtils.isNotEmpty(businessId)){
            swStoreWorkDataTableList = swStoreWorkDataTableDao.getSwStoreWorkDataTableListByBusinessId(enterpriseId, null, businessId);
        }else{
            swStoreWorkDataTableList = swStoreWorkDataTableDao.getSwStoreWorkDataTableList(enterpriseId,queryDate,workCycle,null,storeId);
        }
        List<StoreWorkOverviewVO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(swStoreWorkDataTableList)){
            return result;
        }
        List<Long> metaTableIds = swStoreWorkDataTableList.stream().map(SwStoreWorkDataTableDO::getMetaTableId).collect(Collectors.toList());
        List<TbMetaTableDO> tbMetaTableDOS = tbMetaTableMapper.selectByIds(enterpriseId, metaTableIds);
        Map<Long, TbMetaTableDO> metaTableMap = tbMetaTableDOS.stream().collect(Collectors.toMap(TbMetaTableDO::getId, v -> v, (a, b) -> a));
        //实际处理人id集合
        List<String> userIds = swStoreWorkDataTableList.stream().map(SwStoreWorkDataTableDO::getActualHandleUserId).collect(Collectors.toList());
        List<Long> tableMappingIds = swStoreWorkDataTableList.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIds);
        Map<Long, String> tableInfoMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));


        Map<String, EnterpriseUserDO> userNameMap = enterpriseUserDao.getUserMap(enterpriseId, userIds);
        swStoreWorkDataTableList.forEach(x->{
            StoreWorkOverviewVO storeWorkOverviewVO = new StoreWorkOverviewVO();
            if (x.getTableProperty().equals(MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode())){
                storeWorkOverviewVO = this.getDefStoreWorkOverviewVO(x);
                result.add(storeWorkOverviewVO);
            }else {
                storeWorkOverviewVO = handleStoreWorkOverviewTableVO(enterpriseId, x);
                storeWorkOverviewVO.setCommentTabDisplayFlag(Boolean.FALSE);
                if (user!=null){
                    // 需要AI点评的情况下点评人已点评，则无法再次点评
                    boolean aiCommentCanRecheck = Constants.INDEX_ONE.equals(x.getIsAiProcess()) && (x.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_COMMENTED) == 0;
                    Boolean commentTabDisplayFlag = getCommentTabDisplayFlag(x.getCommentStatus(), x.getCompleteStatus(), x.getEndTime(), x.getCommentUserIds(), user, aiCommentCanRecheck);
                    storeWorkOverviewVO.setCommentTabDisplayFlag(commentTabDisplayFlag);
                }
                result.add(storeWorkOverviewVO);
            }
            storeWorkOverviewVO.setTableInfo(tableInfoMap.getOrDefault(x.getTableMappingId(),""));
            //处理人处理
            if (StringUtils.isNotEmpty(x.getActualHandleUserId())){
                EnterpriseUserDO enterpriseUserDO = userNameMap.getOrDefault(x.getActualHandleUserId(), new EnterpriseUserDO());
                HandlerUserVO handlerUserVO = new HandlerUserVO();
                handlerUserVO.setUserId(enterpriseUserDO.getUserId());
                handlerUserVO.setUserName(enterpriseUserDO.getName());
                handlerUserVO.setUserMobile(enterpriseUserDO.getMobile());
                handlerUserVO.setAvatar(enterpriseUserDO.getAvatar());
                List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, enterpriseUserDO.getUserId());
                if (CollectionUtils.isNotEmpty(sysRoleList)){
                    handlerUserVO.setUserRoles(sysRoleList);
                }
                storeWorkOverviewVO.setHandlerUserVO(handlerUserVO);
            }
            setAiStatusDisplayFlag(storeWorkOverviewVO, metaTableMap.get(x.getMetaTableId()));
        });
        return result;
    }

    /**
     * 设置前端用于AI状态展示的字段
     * @param vo 店务AI字段VO
     * @param metaTableDO 检查表
     */
    public static void setAiStatusDisplayFlag(StoreWorkAIFieldVO vo, TbMetaTableDO metaTableDO) {
        if (Objects.isNull(metaTableDO)) {
            throw new ServiceException(ErrorCodeEnum.CHECKTABLE_IS_NULL);
        }
        if (Constants.INDEX_ONE.equals(vo.getIsAiProcess())) {
            if ((vo.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_FAIL) == Constants.STORE_WORK_AI.AI_STATUS_FAIL) {
                vo.setAiStatusDisplayFlag(Constants.STORE_WORK_AI.AI_STATUS_FAIL);
            } else if (Constants.INDEX_ONE.equals(metaTableDO.getAiResultMethod())) {
                // 作为检查结果
                vo.setAiStatusDisplayFlag(vo.getAiStatus());
            } else {
                // 仅作为参考的情况下，只有未点评和已点评两种状态
                vo.setAiStatusDisplayFlag((vo.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_COMMENTED));
            }
        }
    }

    @Override
    public StoreWorkDataTableSimpleVO selectDataTableByStoreWorkId(String enterpriseId, Long queryDate, String userId, Long storeWorkId) {
        if (storeWorkId == null || queryDate == null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        String storeWorkDate = DateUtil.format(new Date(queryDate), DateUtils.DATE_FORMAT_DAY);
        SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDao.selectDataTableByStoreWorkId(enterpriseId, storeWorkDate, userId, storeWorkId);
        StoreWorkDataTableSimpleVO storeWorkDataTableSimpleVO = new StoreWorkDataTableSimpleVO();
        if(swStoreWorkDataTableDO != null){
            BeanUtils.copyProperties(swStoreWorkDataTableDO, storeWorkDataTableSimpleVO);
        }
        return storeWorkDataTableSimpleVO;
    }

    public StoreWorkOverviewVO getDefStoreWorkOverviewVO(SwStoreWorkDataTableDO swStoreWorkDataTableDO){
        StoreWorkOverviewVO storeWorkOverviewVO = new StoreWorkOverviewVO();
        storeWorkOverviewVO.setTableProperty(swStoreWorkDataTableDO.getTableProperty());
        storeWorkOverviewVO.setMetaTableName(swStoreWorkDataTableDO.getTableName());
        storeWorkOverviewVO.setBeginTime(swStoreWorkDataTableDO.getBeginTime());
        storeWorkOverviewVO.setBeginHandleTime(swStoreWorkDataTableDO.getBeginHandleTime());
        storeWorkOverviewVO.setEndHandleTime(swStoreWorkDataTableDO.getEndHandleTime());
        storeWorkOverviewVO.setDataTableId(swStoreWorkDataTableDO.getId());
        storeWorkOverviewVO.setCompleteStatus(swStoreWorkDataTableDO.getCompleteStatus());
        storeWorkOverviewVO.setEndTime(swStoreWorkDataTableDO.getEndTime());
        //自定义表不能点评
        storeWorkOverviewVO.setCommentTabDisplayFlag(Boolean.FALSE);
        return storeWorkOverviewVO;
    }

    /**
     * 点评标识
     * @param commentStatus
     * @param handleStatus
     * @param endTime
     * @param comment_user_ids
     * @param user
     * @param aiCommentCanRecheck 需要AI点评的情况下点评人能否复核
     * @return
     */
    public Boolean getCommentTabDisplayFlag(Integer commentStatus, Integer handleStatus, Date endTime,String comment_user_ids,CurrentUser user, boolean aiCommentCanRecheck){
        if (StringUtils.isEmpty(user.getUserId())){
            return Boolean.FALSE;
        }
        // 如果是点评过且不是AI点评的，直接返回false
        if (Constants.INDEX_ONE.equals(commentStatus) && !aiCommentCanRecheck ||!comment_user_ids.contains(user.getUserId())){
            return Boolean.FALSE;
        }
        //执行过或者没有执行过但是当前时间大于结束时间
        if (Constants.INDEX_ONE.equals(handleStatus)||(Constants.INDEX_ZERO.equals(handleStatus)&&endTime.getTime()<System.currentTimeMillis())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }


    public Boolean getHandleFlag(Integer overdueContinue, Integer handleStatus, Date endTime,String handle_user_ids,CurrentUser user){
        //如果是执行过的 直接返回false
        if (Constants.INDEX_ONE.equals(handleStatus) || !handle_user_ids.contains(user.getUserId())){
            return Boolean.FALSE;
        }
        //未结束，或结束了 可继续执行
        if (endTime.getTime() > System.currentTimeMillis() || (Constants.INDEX_ONE.equals(overdueContinue) && endTime.getTime()<System.currentTimeMillis())){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public StoreWorkOverviewVO getStoreWorkTableData(String enterpriseId, Long dataTableId) {
        SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        if (swStoreWorkDataTableDO==null){
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_RECORD_TABLE_IS_NOT_EXIST);
        }
        StoreWorkOverviewVO storeWorkOverviewVO = handleStoreWorkOverviewTableVO(enterpriseId, swStoreWorkDataTableDO);
        Map<String, EnterpriseUserDO> userNameMap = enterpriseUserDao.getUserMap(enterpriseId, Arrays.asList(swStoreWorkDataTableDO.getActualHandleUserId()));
        if (StringUtils.isNotEmpty(swStoreWorkDataTableDO.getActualHandleUserId())){
            EnterpriseUserDO enterpriseUserDO = userNameMap.getOrDefault(swStoreWorkDataTableDO.getActualHandleUserId(), new EnterpriseUserDO());
            HandlerUserVO handlerUserVO = new HandlerUserVO();
            handlerUserVO.setUserId(enterpriseUserDO.getUserId());
            handlerUserVO.setUserName(enterpriseUserDO.getName());
            handlerUserVO.setUserMobile(enterpriseUserDO.getName());
            handlerUserVO.setAvatar(enterpriseUserDO.getAvatar());
            List<SysRoleDO> sysRoleList = sysRoleMapper.getSysRoleByUserId(enterpriseId, enterpriseUserDO.getUserId());
            if (CollectionUtils.isNotEmpty(sysRoleList)){
                handlerUserVO.setUserRoles(sysRoleList);
            }
            storeWorkOverviewVO.setHandlerUserVO(handlerUserVO);
        }
        return storeWorkOverviewVO;
    }

    @Override
    public  List<StoreWorkClearVO> getStoreWorkClear(String enterpriseId, CurrentUser user, StoreWorkClearRequest storeWorkClearRequest) {
        //日期集合
        List<String> timeCycleStrList = TimeCycleEnum.getTimeCycleStrList(storeWorkClearRequest.getTimeCycle(), storeWorkClearRequest.getTimeUnion());
        //查询指定人员指定门店 日期范围内的店务记录表
        List<SwStoreWorkDataTableDO> swStoreWorkRecordDOS = swStoreWorkDataTableDao.selectSpecialTimeNoCompleteStoreWork(enterpriseId, storeWorkClearRequest.getStoreId(),
                user.getUserId(), storeWorkClearRequest.getTimeCycle().getCode().toUpperCase(),timeCycleStrList);
        //根据时间分组
        Map<Date, List<SwStoreWorkDataTableDO>> listMap = swStoreWorkRecordDOS.stream().filter(x->Constants.INDEX_ZERO.equals(x.getCompleteStatus())).collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getStoreWorkDate));

        Map<Date, List<SwStoreWorkDataTableDO>> swStoreWorkDataTableDOMap = swStoreWorkRecordDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getStoreWorkDate));
        List<StoreWorkClearVO> list = new ArrayList<>();
        timeCycleStrList.forEach(x->{
            StoreWorkClearVO storeWorkClearVO = new StoreWorkClearVO();
            storeWorkClearVO.setIsFinish(Boolean.FALSE);
            String dateStr = DateUtil.convert(x, DateUtils.DATE_FORMAT_DAY, DatePattern.PURE_DATE_PATTERN);
            storeWorkClearVO.setTimeUnion(Integer.valueOf(dateStr));
            List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = listMap.get(DateUtil.parse(x,DateUtils.DATE_FORMAT_DAY));
            if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
                storeWorkClearVO.setIsFinish(Boolean.TRUE);
            }
            List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOList = swStoreWorkDataTableDOMap.get(DateUtil.parse(x,DateUtils.DATE_FORMAT_DAY));
            if (CollectionUtils.isEmpty(swStoreWorkDataTableDOList)){
                storeWorkClearVO.setIsFinish(null);
            }
            list.add(storeWorkClearVO);
        });
        return list;
    }

    @Override
    public Boolean transferHandler(String enterpriseId, CurrentUser user, String transferUserId, List<Long> storeWorkDataTableIds) {
        //需要转交的检查表
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectByIds(storeWorkDataTableIds, enterpriseId);
        if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
            return Boolean.TRUE;
        }
        List<SwStoreWorkDataTableDO> result= new ArrayList<>();
        swStoreWorkDataTableDOS.forEach(x->{
            SwStoreWorkDataTableDO swStoreWorkDataTableDO = new SwStoreWorkDataTableDO();
            String handleUserIds = x.getHandleUserIds();
            String newHandleUserId = handleUserIds.replace(String.format("%s%s%s", Constants.COMMA, user.getUserId(), Constants.COMMA), String.format("%s%s%s", Constants.COMMA, transferUserId, Constants.COMMA));
            swStoreWorkDataTableDO.setUpdateTime(new Date());
            swStoreWorkDataTableDO.setUpdateUserId(user.getUserId());
            swStoreWorkDataTableDO.setHandleUserIds(newHandleUserId);
            swStoreWorkDataTableDO.setId(x.getId());
            result.add(swStoreWorkDataTableDO);
        });
        Boolean updateFlag = swStoreWorkDataTableDao.batchUpdate(enterpriseId,result);
        swStoreWorkDataTableDOS.forEach(x->{
            HashMap<String, String> paramMap = new HashMap<>();
            paramMap.put("fromUserId", user.getUserId());
            paramMap.put("toUserId", transferUserId);
            // 发送转交工作通知
            jmsTaskService.sendStoreWorkMessage(enterpriseId, x.getId(), StoreWorkNoticeEnum.TURN_NOTICE.getOperate(), paramMap);
            // 取消待办
            cancelUpcoming(enterpriseId, user.getDingCorpId(), user.getAppType(), x.getId(), StoreWorkNoticeEnum.BEFORE_START_REMIND.getOperate(), Collections.singletonList(user.getUserId()));
            // cancelUpcoming(enterpriseId, user.getDingCorpId(), user.getAppType(), x.getId(), StoreWorkNoticeEnum.TURN_NOTICE.getOperate(), Collections.singletonList(user.getUserId()));

        });

        return updateFlag;
    }

    @Override
    public Boolean transferHandlerAndComment(String enterpriseId, CurrentUser user,List<TransferHandlerCommentRequest> requestList) {
        if (CollectionUtils.isEmpty(requestList)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //校验
        requestList.forEach(x->{
            if (CollectionUtils.isEmpty(x.getStoreWorkDataTableIds())||CollectionUtils.isEmpty(x.getHandleUserIds())){
                throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
            }});
        requestList.forEach(data->{
            List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectByIds(data.getStoreWorkDataTableIds(), enterpriseId);
            if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
                log.info("没有对应的数据");
                return;
            }
            SwStoreWorkDataTableDO sw = swStoreWorkDataTableDOS.get(0);

            List<SwStoreWorkDataTableDO> result= new ArrayList<>();
            List<String> handleUserIds = data.getHandleUserIds();
            swStoreWorkDataTableDOS.forEach(x->{
                SwStoreWorkDataTableDO swStoreWorkDataTableDO = new SwStoreWorkDataTableDO();
                swStoreWorkDataTableDO.setUpdateTime(new Date());
                swStoreWorkDataTableDO.setUpdateUserId(user.getUserId());
                String commentUserIdsStr = CollectionUtils.isNotEmpty(data.getCommentUserIds())?
                        String.format("%s%s%s", Constants.COMMA,data.getCommentUserIds().stream().collect(Collectors.joining(Constants.COMMA)),Constants.COMMA) :"";
                swStoreWorkDataTableDO.setCommentUserIds(commentUserIdsStr);
                swStoreWorkDataTableDO.setHandleUserIds( String.format("%s%s%s", Constants.COMMA, handleUserIds.stream().collect(Collectors.joining(Constants.COMMA)), Constants.COMMA));
                swStoreWorkDataTableDO.setId(x.getId());
                result.add(swStoreWorkDataTableDO);
            });
            swStoreWorkDataTableDao.batchUpdate(enterpriseId,result);

            String oldHandleUserIds = sw.getHandleUserIds();
            if (StringUtils.isNotEmpty(oldHandleUserIds)){
                String[] split = oldHandleUserIds.split(Constants.COMMA);
                List<String> oldHandleUserIdList = Arrays.asList(split);
                handleUserIds.removeAll(oldHandleUserIdList);
                //handleUserIds新增的处理人消息
            }
            log.info("转交新增人员ID:{}",JSONObject.toJSONString(handleUserIds));
            if (CollectionUtils.isNotEmpty(handleUserIds)){
                swStoreWorkDataTableDOS.forEach(y->{
                    //已经完成不发工作通知
                    if (y.getCompleteStatus()==1){
                        return;
                    }
                    handleUserIds.forEach(x->{
                        HashMap<String, String> paramMap = new HashMap<>();
                        paramMap.put("fromUserId", user.getUserId());
                        paramMap.put("toUserId", x);
                        // 发送转交工作通知
                        jmsTaskService.sendStoreWorkMessage(enterpriseId, y.getId(), StoreWorkNoticeEnum.TURN_NOTICE.getOperate(), paramMap);
                    });
                });
            }
        });
        return Boolean.TRUE;
    }

    @Override
    public SwStoreWorkReturnDTO getDataUser(String enterpriseId, String tcBusinessId, String storeId) {
        if (StringUtils.isEmpty(tcBusinessId)){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByBusinessId(enterpriseId, tcBusinessId, storeId);
        if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
            return new SwStoreWorkReturnDTO();
        }
        Map<Integer, List<SwStoreWorkDataTableDO>> listMap = swStoreWorkDataTableDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableDO::getGroupNum));
        SwStoreWorkReturnDTO swStoreWorkReturnDTO = new SwStoreWorkReturnDTO() ;
        swStoreWorkReturnDTO.setWorkCycle(swStoreWorkDataTableDOS.get(0).getWorkCycle());
        List<SwStoreWorkHandleUserDTO> swStoreWorkHandleUserDTOS = new ArrayList<>();
        for (Map.Entry<Integer, List<SwStoreWorkDataTableDO>> entry : listMap.entrySet()) {
            List<SwStoreWorkDataTableDO> value = entry.getValue();
            List<SwStoreWorkDataUserDTO> swStoreWorkDataUserDTOS = new ArrayList<>();
            SwStoreWorkHandleUserDTO swStoreWorkHandleUserDTO = new SwStoreWorkHandleUserDTO();
            for (SwStoreWorkDataTableDO swStoreWorkDataTableDO : value) {
                SwStoreWorkDataUserDTO swStoreWorkDataUserDTO = new SwStoreWorkDataUserDTO();
                swStoreWorkDataUserDTO.setId(swStoreWorkDataTableDO.getId());
                swStoreWorkDataUserDTO.setTableName(swStoreWorkDataTableDO.getTableName());
                swStoreWorkDataUserDTO.setBeginTime(swStoreWorkDataTableDO.getBeginTime());
                swStoreWorkDataUserDTO.setEndTime(swStoreWorkDataTableDO.getEndTime());
                swStoreWorkDataUserDTOS.add(swStoreWorkDataUserDTO);
            }
            SwStoreWorkDataTableDO swStoreWorkDataTableDO = value.get(0);
            String handleUserIds = swStoreWorkDataTableDO.getHandleUserIds();
            if (StringUtils.isNotEmpty(handleUserIds)) {
                List<String> handleUserIdList = Arrays.asList(handleUserIds.split(Constants.COMMA));
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, handleUserIdList);
                swStoreWorkHandleUserDTO.setHandleUserIdList(enterpriseUserSingleDTOS);
            }
            swStoreWorkHandleUserDTO.setSwStoreWorkDataUserDTOS(swStoreWorkDataUserDTOS);
            String commentUserIds = value.get(0).getCommentUserIds();
            if (StringUtils.isNotEmpty(commentUserIds)){
                List<String> commentUserIdList = Arrays.asList(commentUserIds.split(Constants.COMMA));
                List<EnterpriseUserSingleDTO> enterpriseUserSingleDTOS = enterpriseUserDao.usersByUserIdList(enterpriseId, commentUserIdList);
                swStoreWorkHandleUserDTO.setCommentUserIdList(enterpriseUserSingleDTOS);
            }else {
                swStoreWorkHandleUserDTO.setCommentUserIdList(new ArrayList<>());
            }
            swStoreWorkHandleUserDTOS.add(swStoreWorkHandleUserDTO);
            swStoreWorkReturnDTO.setSwStoreWorkHandleUserDTOS(swStoreWorkHandleUserDTOS);
        }

        return swStoreWorkReturnDTO;
    }

    @Override
    public List<StoreWorkDataTableStatisticsVO> storeWorkTableStatisticsList(String enterpriseId, StoreWorkDataListRequest request, CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIdList());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return Collections.emptyList();
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        request.setRegionPathList(regionPathList);
        List<StoreWorkDataTableStatisticsVO> storeWorkDataTableStatisticsVOList = swStoreWorkDataTableDao.storeWorkTableStatisticsList(enterpriseId,request);
        List<SwStoreWorkTableMappingDO> allStoreWorkTableMapping = swStoreWorkTableMappingDao.selectListWithDelByStoreWorkIds(enterpriseId, Collections.singletonList(request.getStoreWorkId()));
        Map<Long, SwStoreWorkTableMappingDO> allStoreWorkTableMappingMap = allStoreWorkTableMapping.stream().collect(Collectors.toMap(data -> data.getId(), data -> data,(a, b)->a));
        for (StoreWorkDataTableStatisticsVO storeWorkDataTableStatisticsVO : storeWorkDataTableStatisticsVOList) {
            String handlePersonInfo = allStoreWorkTableMappingMap.get(storeWorkDataTableStatisticsVO.getTableMappingId()).getHandlePersonInfo();
            List<StoreWorkCommonDTO> handlePersonDTOS = JSONObject.parseArray(handlePersonInfo, StoreWorkCommonDTO.class);
            storeWorkDataTableStatisticsVO.setHandlePersonInfo(handlePersonDTOS);
        }
        return storeWorkDataTableStatisticsVOList;
    }

    @Override
    public List<StoreWorkTableListVO> getStoreWorkTableList(String enterpriseId, String businessId) {
        //查询全部的时候检查表数据表
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.selectSwStoreWorkDataTableByBusinessId(enterpriseId, businessId,null);
        List<Long> tableMappingIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIds);
        Map<Long, String> tableInfoMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));
        List<StoreWorkTableListVO> result = new ArrayList<>();
        swStoreWorkDataTableDOS.forEach(x->{
            StoreWorkTableListVO storeWorkTableListVO = new StoreWorkTableListVO();
            storeWorkTableListVO.setTableName(x.getTableName());
            storeWorkTableListVO.setBeginTime(x.getBeginTime());
            storeWorkTableListVO.setEndTime(x.getEndTime());
            storeWorkTableListVO.setTableInfo(tableInfoMap.getOrDefault(x.getTableMappingId(),""));
            storeWorkTableListVO.setId(x.getId());
            storeWorkTableListVO.setWorkCycle(x.getWorkCycle());
            result.add(storeWorkTableListVO);
        });
        return result;
    }

    @Override
    public Boolean commentScore(String enterpriseId, CurrentUser user, List<CommentScoreRequest> requestList) {
        List<SwStoreWorkDataTableColumnDO> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(requestList)){
            return Boolean.TRUE;
        }
        //校验
        SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumnDO = swStoreWorkDataTableColumnDao.selectByPrimaryKey(requestList.get(0).getId(), enterpriseId);
        if (swStoreWorkDataTableColumnDO==null){
            throw new ServiceException(ErrorCodeEnum.STORE_WORK_DATA_TABLE_COLUMN_IS_NOT_EXIST);
        }
        //校验是否点评过
        // AI点评过后，实际点评人为空，这时点评人能够进行点评
        SwStoreWorkDataTableDO swStoreWorkDataTable = swStoreWorkDataTableDao.selectByPrimaryKey(swStoreWorkDataTableColumnDO.getDataTableId(), enterpriseId);
        if (swStoreWorkDataTable!=null&&Constants.INDEX_ONE.equals(swStoreWorkDataTable.getCommentStatus())){
            // 不需要AI点评或者点评人已点评
            boolean isAiComment = Constants.INDEX_ZERO.equals(swStoreWorkDataTable.getIsAiProcess()) || (swStoreWorkDataTable.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_COMMENTED) == Constants.STORE_WORK_AI.AI_STATUS_COMMENTED;
            if (isAiComment) {
                throw new ServiceException(ErrorCodeEnum.COMMENT);
            }
        }

        requestList.forEach(x->{
            SwStoreWorkDataTableColumnDO swStoreWorkDataTableColumn = new SwStoreWorkDataTableColumnDO();
            swStoreWorkDataTableColumn.setActualCommentUserId(user.getUserId());
            swStoreWorkDataTableColumn.setCommentContent(x.getCommentContent());
            swStoreWorkDataTableColumn.setCheckPics(x.getCheckPics());
            swStoreWorkDataTableColumn.setCheckResult(x.getCheckResult());
            swStoreWorkDataTableColumn.setCheckScore(x.getCheckScore());
            swStoreWorkDataTableColumn.setScoreTimes(x.getScoreTimes()==null?new BigDecimal(Constants.INDEX_ONE):x.getScoreTimes());
            swStoreWorkDataTableColumn.setCheckResultId(x.getCheckResultId());
            swStoreWorkDataTableColumn.setCheckResultName(x.getCheckResultName());
            swStoreWorkDataTableColumn.setAwardTimes(x.getAwardTimes()==null?new BigDecimal(Constants.INDEX_ONE):x.getAwardTimes());
            swStoreWorkDataTableColumn.setId(x.getId());
            result.add(swStoreWorkDataTableColumn);
        });
        Boolean aBoolean = swStoreWorkDataTableColumnDao.batchUpdate(enterpriseId, result);

        //企业配置数据
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        // 不合格检查项list
        List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOS
                = swStoreWorkDataTableColumnDao.selectFailColumnByDataTableId(enterpriseId, swStoreWorkDataTableColumnDO.getDataTableId());
        // 过滤出未发起过工单的检查项
        swStoreWorkDataTableColumnDOS = swStoreWorkDataTableColumnDOS.stream().filter(v -> Objects.isNull(v.getTaskQuestionId()) || v.getTaskQuestionId() == 0L).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(swStoreWorkDataTableColumnDOS) && storeWorkSetting.getAutoSendProblem()) {
            // 自动发起问题工单
            Set<Long> failedMetaStaColumnIds =
                    swStoreWorkDataTableColumnDOS.stream().map(SwStoreWorkDataTableColumnDO::getMetaColumnId).collect(Collectors.toSet());
            List<TbMetaStaTableColumnDO> metaStaColumnList =
                    tbMetaStaTableColumnMapper.selectByIds(enterpriseId, new ArrayList<>(failedMetaStaColumnIds));
            Map<Long, TbMetaStaTableColumnDO> idMetaStaColumnMap = metaStaColumnList.stream()
                    .collect(Collectors.toMap(TbMetaStaTableColumnDO::getId, Function.identity(), (a, b) -> a));
            List<StaColumnDTO> failStaColumnList = swStoreWorkDataTableColumnDOS.stream()
                    .map(a -> StaColumnDTO.builder().swStoreWorkDataTableColumnDODO(a)
                            .tbMetaStaTableColumnDO(idMetaStaColumnMap.get(a.getMetaColumnId())).build())
                    .collect(Collectors.toList());

            log.info("failStaColumnList :{}", JSONUtil.toJsonStr(failStaColumnList));
            //自动发起工单
            this.autoQuestionOrder(enterpriseId, failStaColumnList, swStoreWorkDataTableColumnDO.getDataTableId(), user.getUserId());
        }

        // 数据检查表id  按检查表点评
        StoreWorkSubmitCommentMsgData storeWorkSubmitCommentMsgData = new StoreWorkSubmitCommentMsgData();
        storeWorkSubmitCommentMsgData.setEnterpriseId(enterpriseId);
        storeWorkSubmitCommentMsgData.setType(StoreWorkConstant.MsgType.COMMENT);
        storeWorkSubmitCommentMsgData.setDataTableId(swStoreWorkDataTableColumnDO.getDataTableId());
        storeWorkSubmitCommentMsgData.setActualCommentUserId(user.getUserId());
        simpleMessageService.send(JSONObject.toJSONString(storeWorkSubmitCommentMsgData), RocketMqTagEnum.STOREWORK_COMMENT_DATA_QUEUE);

        String tcBusinessId = swStoreWorkDataTableColumnDO.getTcBusinessId();

        return aBoolean;
    }


    @Override
    public StoreWorkTableAndRecordStatusInfo checkStoreWorkStatusAuth(String enterpriseId, CurrentUser user, String businessId, Long dataTableId) {
        StoreWorkTableAndRecordStatusInfo storeWorkTableAndRecordStatusInfo = new StoreWorkTableAndRecordStatusInfo();
        if(dataTableId != null){
            SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
            if (swStoreWorkDataTableDO == null || swStoreWorkDataTableDO.getDeleted()) {
                throw new ServiceException(ErrorCodeEnum.STORE_WORK_RECORD_TABLE_IS_NOT_EXIST);
            }
            storeWorkTableAndRecordStatusInfo.setCompleteStatus(swStoreWorkDataTableDO.getCompleteStatus());
            storeWorkTableAndRecordStatusInfo.setCommentStatus(swStoreWorkDataTableDO.getCommentStatus());

            // 需要AI点评的情况下点评人已点评，则无法再次点评
            boolean aiCommentCanRecheck = Constants.INDEX_ONE.equals(swStoreWorkDataTableDO.getIsAiProcess()) && (swStoreWorkDataTableDO.getAiStatus() & Constants.STORE_WORK_AI.AI_STATUS_COMMENTED) == 0;
            Boolean commentTabDisplayFlag = getCommentTabDisplayFlag(swStoreWorkDataTableDO.getCommentStatus(),swStoreWorkDataTableDO.getCompleteStatus(),swStoreWorkDataTableDO.getEndTime(),swStoreWorkDataTableDO.getCommentUserIds(),user, aiCommentCanRecheck);
            storeWorkTableAndRecordStatusInfo.setCommentTabDisplayFlag(commentTabDisplayFlag);

            SwStoreWorkDO storeWorkDO = swStoreWorkDao.selectByPrimaryKey(swStoreWorkDataTableDO.getStoreWorkId(), enterpriseId);
            Boolean handleFlag = getHandleFlag(storeWorkDO.getOverdueContinue(),swStoreWorkDataTableDO.getCompleteStatus(),swStoreWorkDataTableDO.getEndTime(),swStoreWorkDataTableDO.getHandleUserIds(),user);
            storeWorkTableAndRecordStatusInfo.setHandleFlag(handleFlag);

        }else if(StringUtils.isNotBlank(businessId)){
            SwStoreWorkRecordDO swStoreWorkRecordDO = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, businessId);
            if (swStoreWorkRecordDO == null || swStoreWorkRecordDO.getDeleted()) {
                throw new ServiceException(ErrorCodeEnum.STORE_WORK_RECORD_IS_NOT_EXIST);
            }
            storeWorkTableAndRecordStatusInfo.setCompleteStatus(swStoreWorkRecordDO.getCompleteStatus());
            storeWorkTableAndRecordStatusInfo.setCommentStatus(swStoreWorkRecordDO.getCommentStatus());
        }
        return storeWorkTableAndRecordStatusInfo;
    }

    @Override
    public PageInfo<StoreWorkPictureListVO> getPictureCenterDataTableList(String enterpriseId, PictureCenterRequest request,CurrentUser user) {
        boolean isAdmin = sysRoleService.checkIsAdmin(enterpriseId, user.getUserId());
        List<RegionPathDTO> regionPathDTOList = getAuthRegionList(enterpriseId, isAdmin, user.getUserId(), request.getRegionIds());
        //非管理员且没有管辖区域
        if(!isAdmin && CollectionUtils.isEmpty(regionPathDTOList)){
            return new PageInfo<>(new ArrayList<>());
        }
        List<String> regionPathList = ListUtils.emptyIfNull(regionPathDTOList)
                .stream()
                .map(RegionPathDTO::getRegionPath)
                .collect(Collectors.toList());
        request.setRegionPathList(regionPathList);
        //校验参数
        if (request.getBeginTime()==null||request.getEndTime()==null||request.getWorkCycle()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        if (request.getPageNumber()==null||request.getPageSize()==null){
            request.setPageSize(10);
            request.setPageNumber(1);
        }
        //参数校验
        PageHelper.startPage(request.getPageNumber(), request.getPageSize());
        String startTime = "";
        if(request.getBeginTime() != null){
            startTime = DateUtils.convertTimeToString(request.getBeginTime().getTime(), DateUtils.DATE_FORMAT_DAY);
        }
        String endTime = "";
        if(request.getEndTime() != null){
            endTime = DateUtils.convertTimeToString(request.getEndTime().getTime(), DateUtils.DATE_FORMAT_DAY);
        }
        List<SwStoreWorkDataTableDO> swStoreWorkDataTableDOS = swStoreWorkDataTableDao.listDataTable(enterpriseId, request, startTime, endTime, request.getRegionPathList());
        PageInfo<SwStoreWorkDataTableDO> swStoreWorkDataTableDOPageInfo = new PageInfo<>(swStoreWorkDataTableDOS);
        if (CollectionUtils.isEmpty(swStoreWorkDataTableDOS)){
            return new PageInfo<>();
        }
        List<Long> dataTableIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getId).collect(Collectors.toList());
        List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOS = swStoreWorkDataTableColumnDao.selectColumnByDataTableId(enterpriseId, dataTableIds, null, null, null);
        Map<Long, List<SwStoreWorkDataTableColumnDO>> dataTableColumnMap = swStoreWorkDataTableColumnDOS.stream().collect(Collectors.groupingBy(SwStoreWorkDataTableColumnDO::getDataTableId));

        List<Long> tableMappingIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getTableMappingId).collect(Collectors.toList());
        List<SwStoreWorkTableMappingDO> swStoreWorkTableMappingDOS = swStoreWorkTableMappingDao.selectListByIds(enterpriseId, tableMappingIds);
        Map<Long, String> tableInfoMap = swStoreWorkTableMappingDOS.stream().collect(Collectors.toMap(SwStoreWorkTableMappingDO::getId, SwStoreWorkTableMappingDO::getTableInfo));

        List<Long> storeWorkIds = swStoreWorkDataTableDOS.stream().map(SwStoreWorkDataTableDO::getStoreWorkId).collect(Collectors.toList());
        List<SwStoreWorkDO> swStoreWorkDOS = swStoreWorkDao.listBystoreWorkIds(enterpriseId, storeWorkIds);
        Map<Long, String> workNameMap = swStoreWorkDOS.stream().collect(Collectors.toMap(SwStoreWorkDO::getId, SwStoreWorkDO::getWorkName));
        List<StoreWorkPictureListVO> result = new ArrayList<>();
        swStoreWorkDataTableDOS.stream().forEach(x->{
            StoreWorkPictureListVO storeWorkPictureListVO = new StoreWorkPictureListVO();
            storeWorkPictureListVO.setStoreWorkData(x.getStoreWorkDate());
            storeWorkPictureListVO.setWeekOfTheYear(DateUtils.getWeekOfYear(x.getStoreWorkDate()));
            storeWorkPictureListVO.setMonthOfTheYear(DateUtils.getMonthOfYear(x.getStoreWorkDate()));
            storeWorkPictureListVO.setTableName(x.getTableName());
            storeWorkPictureListVO.setBeginTime(x.getBeginTime());
            storeWorkPictureListVO.setEndTime(x.getEndTime());
            storeWorkPictureListVO.setStoreId(x.getStoreId());
            storeWorkPictureListVO.setStoreName(x.getStoreName());
            storeWorkPictureListVO.setWorkName(workNameMap.getOrDefault(x.getStoreWorkId(),""));
            storeWorkPictureListVO.setTableInfo(tableInfoMap.getOrDefault(x.getTableMappingId(),""));
            List<SwStoreWorkDataTableColumnDO> swStoreWorkDataTableColumnDOList = dataTableColumnMap.getOrDefault(x.getId(), Collections.emptyList());
            List<StoreWorkPictureCenterColumnVO> storeWorkPictureCenterColumnVOS = new ArrayList<>();
            swStoreWorkDataTableColumnDOList.stream().sorted(Comparator.comparing(SwStoreWorkDataTableColumnDO::getSubmitTime)).forEach(data->{
                StoreWorkPictureCenterColumnVO columnVO = new StoreWorkPictureCenterColumnVO();
                columnVO.setMetaColumnId(data.getMetaColumnId());
                columnVO.setMetaColumnName(data.getMetaColumnName());
                columnVO.setId(data.getId());
                columnVO.setPictureUrl(data.getCheckPics());
                columnVO.setVideoUrl(data.getCheckVideo());
                columnVO.setCommentContent(data.getCommentContent());
                if (StringUtils.isNotBlank(data.getActualCommentUserId())){
                    EnterpriseUserDO enterpriseUserDO = enterpriseUserDao.selectByUserId(enterpriseId, data.getActualCommentUserId());
                    columnVO.setCommentUser(enterpriseUserDO != null ? enterpriseUserDO.getName(): "");
                }
                storeWorkPictureCenterColumnVOS.add(columnVO);
            });
            if (CollectionUtils.isNotEmpty(request.getMetaColumnIds())){
                List<StoreWorkPictureCenterColumnVO> collect = storeWorkPictureCenterColumnVOS.stream().filter(item -> request.getMetaColumnIds().contains(item.getMetaColumnId())).collect(Collectors.toList());
                storeWorkPictureListVO.setStoreWorkPictureCenterColumnVOS(collect);
            }else {
                storeWorkPictureListVO.setStoreWorkPictureCenterColumnVOS(storeWorkPictureCenterColumnVOS);
            }
            result.add(storeWorkPictureListVO);
        });
        PageInfo<StoreWorkPictureListVO> pageInfo = new PageInfo<>(result);
        pageInfo.setTotal(swStoreWorkDataTableDOPageInfo.getTotal());
        return pageInfo;
    }

    /**
     * 门店区域统一处理
     * @param enterpriseId
     * @param request
     * @return
     */
    public List<String> queryFullRegionPath(String enterpriseId, PictureCenterRequest request){
        List<String> regionIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(request.getStoreIds())){
            List<RegionDO> regionDOS = regionService.listRegionByStoreIds(enterpriseId, request.getStoreIds());
            for (RegionDO regionDO : regionDOS) {
                regionIds.add(String.valueOf(regionDO.getId()));
            }
        }
        List<String> list = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(request.getRegionIds())||CollectionUtils.isNotEmpty(regionIds)) {
            if (CollectionUtils.isNotEmpty(request.getRegionIds())) {
                regionIds.addAll(request.getRegionIds());
            }
            List<RegionPathDTO> regionPathList = regionService.getRegionPathByList(enterpriseId, regionIds);
            if (CollectionUtils.isNotEmpty(regionPathList)) {
                list = regionPathList.stream().map(RegionPathDTO::getRegionPath).collect(Collectors.toList());
            }
        }
        return list;
    }

    /**
     * 自动发起工单
     * @param enterpriseId
     * @param failStaColumnList
     * @param dataTableId
     * @param userId
     */
    @Override
    public void autoQuestionOrder(String enterpriseId, List<StaColumnDTO> failStaColumnList, Long dataTableId, String userId) {
        SwStoreWorkDataTableDO swStoreWorkDataTableDO = swStoreWorkDataTableDao.selectByPrimaryKey(dataTableId, enterpriseId);
        if (swStoreWorkDataTableDO==null){
            return;
        }
        SwStoreWorkRecordDO sw = swStoreWorkRecordDao.getByTcBusinessId(enterpriseId, swStoreWorkDataTableDO.getTcBusinessId());
        BuildQuestionRequest buildQuestionRequest = new BuildQuestionRequest();
        buildQuestionRequest.setQuestionType(QuestionTypeEnum.STORE_WORK.getCode());
        long createTime = System.currentTimeMillis();
        EnterpriseUserDO createUser = enterpriseUserDao.selectByUserId(enterpriseId, userId);

        List<BuildQuestionDTO> taskList = new ArrayList<>();
        String questionName = swStoreWorkDataTableDO.getStoreName() + swStoreWorkDataTableDO.getTableName() + "的工单";
        if(questionName.length() > Constants.COLUMN_NAME_MAX_LENGTH){
            questionName = questionName.substring(0, Constants.COLUMN_NAME_MAX_LENGTH - 1);
        }
        buildQuestionRequest.setTaskName(questionName);
        EnterpriseQuestionSettingsDTO questionSettingsDTO = enterpriseSettingRpcService.getQuestionSetting(enterpriseId);
        EnterpriseStoreWorkSettingsDTO storeWorkSetting = enterpriseSettingRpcService.getStoreWorkSetting(enterpriseId);
        Boolean autoQuestionStudyFirst = questionSettingsDTO.getAutoQuestionStudyFirst();
        failStaColumnList.forEach(item->{
            SwStoreWorkDataTableColumnDO data = item.getSwStoreWorkDataTableColumnDODO();
            TbMetaStaTableColumnDO column =item.getTbMetaStaTableColumnDO();
            BuildQuestionDTO task = new BuildQuestionDTO();
            task.setTaskDesc(data.getCommentContent());
            if (StoreWorkCycleEnum.DAY.getCode().equals(swStoreWorkDataTableDO.getWorkCycle())){
                task.setEndTime(org.apache.commons.lang3.time.DateUtils.addHours(new Date(createTime), storeWorkSetting.getAutoQuestionTaskValidity()));
            }else if(StoreWorkCycleEnum.WEEK.getCode().equals(swStoreWorkDataTableDO.getWorkCycle())){
                task.setEndTime(org.apache.commons.lang3.time.DateUtils.addHours(new Date(createTime), storeWorkSetting.getWeeklyClearanceOrderValidity()));
            }else if(StoreWorkCycleEnum.MONTH.getCode().equals(swStoreWorkDataTableDO.getWorkCycle())){
                task.setEndTime(org.apache.commons.lang3.time.DateUtils.addHours(new Date(createTime), storeWorkSetting.getMonthlyClearanceOrderValidity()));
            }else {
                //循环类型如果不是日、周、月，则设一个默认值;
                task.setEndTime(org.apache.commons.lang3.time.DateUtils.addHours(new Date(createTime), storeWorkSetting.getAutoQuestionTaskValidity()));
            }
            task.setTaskName(column.getColumnName());
            task.setStoreId(swStoreWorkDataTableDO.getStoreId());
            List<TaskProcessDTO> process = Lists.newArrayList();
            //整改人
            TaskProcessDTO handPerson = new TaskProcessDTO();
            handPerson.setNodeNo(UnifyNodeEnum.FIRST_NODE.getCode());
            List<GeneralDTO> handUserList = Lists.newArrayList();
            String handleId = column.getQuestionHandlerId();
            String handleType = column.getQuestionHandlerType();
            dealUsers(userId, process, handPerson, handUserList, handleId, handleType, enterpriseId);
            //审核人
            TaskProcessDTO checkPerson = new TaskProcessDTO();
            checkPerson.setNodeNo(UnifyNodeEnum.SECOND_NODE.getCode());
            List<GeneralDTO> checkUserList = Lists.newArrayList();
            String reCheckId = column.getQuestionRecheckerId();
            if(StringUtils.isNotBlank(reCheckId)){
                String reCheckType = column.getQuestionRecheckerType();
                dealUsers(userId, process, checkPerson, checkUserList, reCheckId, reCheckType, enterpriseId);
            }
            if(column.getCreateUserApprove() != null && column.getCreateUserApprove()){
                checkUserList.add(new GeneralDTO(UnifyTaskConstant.PersonType.PERSON, userId, createUser != null ? createUser.getName() : ""));
                if (StringUtils.isBlank(reCheckId)) {
                    checkPerson.setUser(checkUserList);
                    checkPerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                    process.add(checkPerson);
                }
            }
            //二级、以及三级审核人处理
            if(StringUtils.isNotBlank(column.getQuestionApproveUser())){
                JSONArray jsonArray = JSONUtil.parseArray(column.getQuestionApproveUser());
                List<PersonPositionListDTO> questionApproveUserList = JSONUtil.toList(jsonArray, PersonPositionListDTO.class);
                if(CollectionUtils.isNotEmpty(questionApproveUserList)){
                    int i = 3;
                    for(PersonPositionListDTO personPositionListDTO : questionApproveUserList){
                        TaskProcessDTO approvePerson = new TaskProcessDTO();
                        approvePerson.setNodeNo(String.valueOf(i));
                        List<GeneralDTO> appUserList = Lists.newArrayList();
                        for(PersonPositionDTO ccUser : personPositionListDTO.getPeopleList()){
                            appUserList.add(new GeneralDTO(ccUser.getType().replace("user","person"), ccUser.getId(), ccUser.getName()));
                        }
                        if(personPositionListDTO.getCreateUserApprove() != null && personPositionListDTO.getCreateUserApprove()){
                            appUserList.add(new GeneralDTO(UnifyTaskConstant.PersonType.PERSON, userId, createUser != null ? createUser.getName() : ""));
                        }
                        if(CollectionUtils.isNotEmpty(appUserList)){
                            approvePerson.setUser(appUserList);
                            approvePerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                            process.add(approvePerson);
                        }
                        i++;
                    }
                }
            }

            //抄送人
            String questionCcId = column.getQuestionCcId();
            if(StringUtils.isNotBlank(questionCcId)){
                cn.hutool.json.JSONArray jsonArray = JSONUtil.parseArray(questionCcId);
                List<PersonPositionDTO> ccIdList = JSONUtil.toList(jsonArray, PersonPositionDTO.class);

                TaskProcessDTO ccPerson = new TaskProcessDTO();
                ccPerson.setNodeNo(UnifyNodeEnum.CC.getCode());
                List<GeneralDTO> ccUserList = Lists.newArrayList();
                for(PersonPositionDTO ccUser : ccIdList){
                    ccUserList.add(new GeneralDTO(ccUser.getType(), ccUser.getId(), ccUser.getName()));
                }
                if(CollectionUtils.isNotEmpty(ccUserList)){
                    ccPerson.setUser(ccUserList);
                    ccPerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
                    process.add(ccPerson);
                }
            }
            log.info("questionCcId :{}", questionCcId);

            task.setProcess(process);

            log.info("task :{}", JSONUtil.toJsonStr(task));
            QuestionTaskInfoDTO info = new QuestionTaskInfoDTO();
            // checkPicList
            info.setPhotos(getFinalUrls(data.getCheckPics()));
            info.setVideos(checkVideoHandel(data));
            info.setDataColumnId(data.getId());
            info.setBusinessId(sw.getId());
            info.setMetaColumnId(data.getMetaColumnId());
            info.setMetaColumnName(data.getMetaColumnName());
            info.setContentLearnFirst(autoQuestionStudyFirst);
            info.setSoundRecordingList(checkSoundLostHandel(data.getCheckVideo()));
            info.setCreateType(QuestionCreateTypeEnum.AUTOMATIC.getCode());
            List<CoolCourseVO> courseList = new ArrayList<>();
            log.info("into :{}", JSONUtil.toJsonStr(info));
            if(StringUtils.isNotBlank(column.getCoolCourse())){
                CoolCourseVO coolCourseVO = JSONObject.parseObject(column.getCoolCourse(), CoolCourseVO.class);
                coolCourseVO.setCourseType(1);
                courseList.add(coolCourseVO);
            }
            if(StringUtils.isNotBlank(column.getFreeCourse())){
                CoolCourseVO coolCourseVO = JSONObject.parseObject(column.getFreeCourse(), CoolCourseVO.class);
                coolCourseVO.setCourseType(3);
                courseList.add(coolCourseVO);;
            }
            if(column.getSopId() != null && column.getSopId() > 0){
                TaskSopVO taskSopVO = taskSopService.getSopById(enterpriseId, column.getSopId());
                if(taskSopVO != null){
                    info.setAttachUrl(JSONObject.toJSONString(taskSopVO));
                }
            }
            info.setCourseList(courseList);
            task.setTaskInfo(info);
            taskList.add(task);
        });
        buildQuestionRequest.setQuestionList(taskList);
        questionParentInfoService.buildQuestion(enterpriseId, buildQuestionRequest, userId, Boolean.TRUE,null);
    }

    /**
     * 审批的时候
     * 修改时间 20230322 如果没有编辑图片，将执行人图片带到工单中
     * @param checkPics
     * @return
     */
    public List<String> getFinalUrls(String checkPics) {
        List<String> handleUrlList = Lists.newArrayList();
        if(StrUtil.isNotEmpty(checkPics)){
            com.alibaba.fastjson.JSONArray jsonArray = com.alibaba.fastjson.JSONArray.parseArray(checkPics);
            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                String finalUrl = jsonObject.getString("final");
                if (StringUtils.isNotEmpty(finalUrl)){
                    handleUrlList.add(finalUrl);
                }else {
                    String handleUrl = jsonObject.getString("handle");
                    handleUrlList.add(handleUrl);
                }
            }
            return handleUrlList;
        }
        return handleUrlList;
    }
    /**
     * 处理音频
     * @param checkVideo 音频
     * @return
     */
    public List<String> checkSoundLostHandel(String checkVideo){
        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(checkVideo, SmallVideoInfoDTO.class);
        return smallVideoInfo == null || CollectionUtils.isEmpty(smallVideoInfo.getSoundRecordingList())?
                new ArrayList<>(): smallVideoInfo.getSoundRecordingList();

    }

    private void dealUsers(String userId, List<TaskProcessDTO> process, TaskProcessDTO checkPerson, List<GeneralDTO> checkUserList, String reCheckId, String reCheckType, String enterpriseId) {
        if(StringUtils.isBlank(reCheckId)){
            reCheckId= userId;
            reCheckType = UnifyTaskConstant.PersonType.PERSON;
        }
        String recheckType = reCheckType;
        String[] reCheckIdArray = reCheckId.split(",");
        Map<String, String> nameMap = changName(enterpriseId, reCheckIdArray, recheckType);

        Arrays.stream(reCheckIdArray).forEach(id -> {
            checkUserList.add(new GeneralDTO(recheckType, id, nameMap.get(id)));
        });
        checkPerson.setUser(checkUserList);
        checkPerson.setApproveType(UnifyTaskConstant.ApproveType.ANY);
        process.add(checkPerson);
    }

    /**
     * 如果状态为转码完成，直接修改，否则从redis获取转码的视频信息
     * @author chenyupeng
     * @date 2021/10/14
     * @param request
     * @return void
     */
    public String checkVideoHandel(SwStoreWorkDataTableColumnDO request){

        SmallVideoInfoDTO smallVideoInfo = JSONObject.parseObject(request.getCheckVideo(), SmallVideoInfoDTO.class);
        if(smallVideoInfo != null && CollectionUtils.isNotEmpty(smallVideoInfo.getVideoList())){
            String callbackCache;
            SmallVideoDTO smallVideoCache;
            for (SmallVideoDTO smallVideo : smallVideoInfo.getVideoList()) {
                //如果转码完成就不处理，直接修改
                if(smallVideo.getStatus() != null && smallVideo.getStatus() >= ResourceStatusEnum.TRANSCODE_FINISH.getValue()){
                    return JSONObject.toJSONString(smallVideoInfo);
                }
                callbackCache = redisUtil.getString(RedisConstant.VIDEO_CALLBACK_CACHE + smallVideo.getVideoId());
                if(StringUtils.isNotBlank(callbackCache)){
                    smallVideoCache = JSONObject.parseObject(callbackCache,SmallVideoDTO.class);
                    if(smallVideoCache !=null && smallVideoCache.getStatus() !=null && smallVideoCache.getStatus() >=3){
                        BeanUtils.copyProperties(smallVideoCache,smallVideo);
                    }
                }
            }
        }
        return smallVideoInfo == null ? "{\"videoList\":[]}" : JSONObject.toJSONString(smallVideoInfo);

    }


    /**
     * 门店概况 表数据统计
     * @param swStoreWorkDataTableDO
     * @return
     */
    public StoreWorkOverviewVO handleStoreWorkOverviewTableVO(String eid ,SwStoreWorkDataTableDO swStoreWorkDataTableDO){
        BigDecimal oneHundred = new BigDecimal(100);
        ///参与计算的总项数
        int num = swStoreWorkDataTableDO.getTotalCalColumnNum();
        BigDecimal passRate = new BigDecimal(Constants.STR_ZERO);
        if (num!=Constants.INDEX_ZERO){
            passRate = new BigDecimal(swStoreWorkDataTableDO.getPassColumnNum()).multiply(oneHundred).divide(new BigDecimal(num),2,BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal failRate = new BigDecimal(Constants.STR_ZERO);
        if (num!=Constants.INDEX_ZERO){
            failRate = new BigDecimal(swStoreWorkDataTableDO.getFailColumnNum()).multiply(oneHundred).divide(new BigDecimal(num),2,BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal inapplicableRate = new BigDecimal(Constants.STR_ZERO);
        if (num!=Constants.INDEX_ZERO){
            inapplicableRate = new BigDecimal(swStoreWorkDataTableDO.getInapplicableColumnNum()).multiply(oneHundred).divide(new BigDecimal(num),2,BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal avgScore = new BigDecimal(Constants.STR_ZERO);
        if (num!=Constants.INDEX_ZERO){
            avgScore = swStoreWorkDataTableDO.getScore().divide(new BigDecimal(num),2,BigDecimal.ROUND_HALF_UP);
        }

        BigDecimal scoreRate = new BigDecimal(Constants.STR_ZERO);
        if (swStoreWorkDataTableDO.getTotalScore().compareTo(new BigDecimal(Constants.INDEX_ZERO))!=0){
            scoreRate = swStoreWorkDataTableDO.getScore().multiply(oneHundred).divide(swStoreWorkDataTableDO.getTotalScore(),2,BigDecimal.ROUND_HALF_UP);
        }


        StoreWorkOverviewVO storeWorkOverviewVO = new StoreWorkOverviewVO();
        storeWorkOverviewVO.setTableProperty(swStoreWorkDataTableDO.getTableProperty());
        storeWorkOverviewVO.setPassColumnNum(swStoreWorkDataTableDO.getPassColumnNum());
        storeWorkOverviewVO.setTotalColumnNum(swStoreWorkDataTableDO.getTotalColumnNum());
        storeWorkOverviewVO.setFailColumnNum(swStoreWorkDataTableDO.getFailColumnNum());
        storeWorkOverviewVO.setCollectColumnNum(swStoreWorkDataTableDO.getCollectColumnNum());
        storeWorkOverviewVO.setInapplicableColumnNum(swStoreWorkDataTableDO.getInapplicableColumnNum());
        storeWorkOverviewVO.setPassRate(String.format("%s%%", passRate));
        storeWorkOverviewVO.setScore(swStoreWorkDataTableDO.getScore());
        storeWorkOverviewVO.setFailRate(String.format("%s%%", failRate));
        storeWorkOverviewVO.setInapplicableRate(String.format("%s%%", inapplicableRate));
        storeWorkOverviewVO.setAvgScore(avgScore);
        storeWorkOverviewVO.setScoreRate(String.format("%s%%", scoreRate));
        storeWorkOverviewVO.setAvgScoreRate(String.format("%s%%", scoreRate));
        storeWorkOverviewVO.setCheckResultLevel(swStoreWorkDataTableDO.getCheckResultLevel());
        storeWorkOverviewVO.setMetaTableName(swStoreWorkDataTableDO.getTableName());
        storeWorkOverviewVO.setBeginHandleTime(swStoreWorkDataTableDO.getBeginHandleTime());
        storeWorkOverviewVO.setEndHandleTime(swStoreWorkDataTableDO.getEndHandleTime());
        storeWorkOverviewVO.setBeginTime(swStoreWorkDataTableDO.getBeginTime());
        storeWorkOverviewVO.setEndTime(swStoreWorkDataTableDO.getEndTime());
        storeWorkOverviewVO.setCommentStatus(swStoreWorkDataTableDO.getCommentStatus());
        storeWorkOverviewVO.setCompleteStatus(swStoreWorkDataTableDO.getCompleteStatus());
        storeWorkOverviewVO.setBusinessId(swStoreWorkDataTableDO.getTcBusinessId());
        storeWorkOverviewVO.setDataTableId(swStoreWorkDataTableDO.getId());
        storeWorkOverviewVO.setWorkCycle(swStoreWorkDataTableDO.getWorkCycle());
        storeWorkOverviewVO.setIsAiProcess(swStoreWorkDataTableDO.getIsAiProcess());
        storeWorkOverviewVO.setAiStatus(swStoreWorkDataTableDO.getAiStatus());
        return storeWorkOverviewVO;
    }

    private List<RegionPathDTO> getAuthRegionList(String enterpriseId, Boolean isAdmin, String userId, List<String> regionIdList){
        if (!isAdmin && CollectionUtils.isEmpty(regionIdList)) {
            List<UserAuthMappingDO> userAuthMappingList = userAuthMappingService.listUserAuthMappingByUserId(enterpriseId, userId);
            if (CollectionUtils.isNotEmpty(userAuthMappingList)) {
                regionIdList = userAuthMappingList.stream().map(UserAuthMappingDO::getMappingId).collect(Collectors.toList());
            }
        }
        List<RegionPathDTO> regionPathList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(regionIdList)){
            regionPathList = regionService.getRegionPathByList(enterpriseId, regionIdList);
        }
        return regionPathList;
    }

    /**
     * 取消待办
     * @param enterpriseId
     * @param dingCorpId
     * @param appType
     * @param dataTableId
     * @param operate
     * @param userIdList
     */
    public void cancelUpcoming(String enterpriseId, String dingCorpId,String appType, Long dataTableId, String operate , List<String> userIdList) {
        log.info("开始删除用户待办");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("enterpriseId", enterpriseId);
        jsonObject.put("corpId", dingCorpId);
        jsonObject.put("taskKey", DingMsgEnum.STOREWORK.getDesc() + "_" + operate + "_" + dataTableId);
        jsonObject.put("appType", appType);
        jsonObject.put("userIds", userIdList);
        simpleMessageService.send(jsonObject.toJSONString(), RocketMqTagEnum.UPCOMING_FINISH);
    }

    private Map<String, String> changName(String enterpriseId, String[] reCheckIdArray, String recheckType) {
        Map<String, String> nameMap = new HashMap<>();
        try {
            //写入名称
            if (StringUtils.isNotBlank(recheckType) && reCheckIdArray.length > 0) {
                switch (recheckType) {
                    case UnifyTaskConstant.PersonType.PERSON:
                        nameMap = enterpriseUserDao.getUserNameMap(enterpriseId, Arrays.asList(reCheckIdArray));
                        break;
                    case UnifyTaskConstant.PersonType.POSITION:
                        List<String> roleIdStrList = Arrays.asList(reCheckIdArray);
                        List<Long> roleIdList = roleIdStrList.stream().map(Long::parseLong).collect(Collectors.toList());
                        List<SysRoleDO> roleList = sysRoleMapper.getRoleByRoleIds(enterpriseId, roleIdList);
                        nameMap = roleList.stream()
                                .filter(a -> a.getId() != null && a.getRoleName() != null)
                                .collect(Collectors.toMap(data -> String.valueOf(data.getId()), SysRoleDO::getRoleName, (a, b) -> a));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            log.error("question#changName,eid:{},recheckType:{},reCheckIdArray:{}", enterpriseId, recheckType, reCheckIdArray, e);
        }
        return nameMap;
    }
}
