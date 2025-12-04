package com.coolcollege.intelligent.facade.open.api.display;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.OpenApiParamCheckUtils;
import com.coolcollege.intelligent.constants.ConfigConstants;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseUserMapper;
import com.coolcollege.intelligent.dao.enterprise.dao.EnterpriseConfigDao;
import com.coolcollege.intelligent.facade.dto.openApi.DisplayDTO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayBaseDTO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTableDTO;
import com.coolcollege.intelligent.facade.dto.openApi.display.DisplayTaskDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.*;
import com.coolcollege.intelligent.model.enterprise.EnterpriseConfigDO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseUserDO;
import com.coolcollege.intelligent.model.metatable.TbMetaTableDO;
import com.coolcollege.intelligent.model.rpc.RpcLocalHolder;
import com.coolcollege.intelligent.model.sop.TaskSopDO;
import com.coolcollege.intelligent.service.metatable.TbMetaTableService;
import com.coolcollege.intelligent.service.sop.TaskSopService;
import com.coolcollege.intelligent.service.tbdisplay.TbDisplayTableRecordService;
import com.coolcollege.intelligent.service.unifytask.UnifyTaskStoreService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.response.rpc.OpenApiResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shenyu.client.sofa.common.annotation.ShenyuSofaClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/7/11 16:25
 * @Version 1.0
 */
@Slf4j
@ConditionalOnProperty(name = "shenyu.register.registerType")
@SofaService(interfaceType = DisplayApi.class,bindings = {@SofaServiceBinding(bindingType = ConfigConstants.SOFA_BINDING_TYPE)})
@Service
public class DisplayApiImpl implements DisplayApi{

    @Resource
    private EnterpriseConfigDao enterpriseConfigDao;
    @Resource
    private TbDisplayTableRecordService tbDisplayTableRecordService;
    @Resource
    private TbMetaTableService tbMetaTableService;
    @Resource
    private TaskSopService taskSopService;
    @Resource
    private UnifyTaskStoreService taskStoreService;
    @Resource
    private EnterpriseUserMapper enterpriseUserMapper;

    @Override
    @ShenyuSofaClient(path = "/display/list")
    public OpenApiResponseVO displayList(DisplayDTO displayDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        displayDTO.setEnterpriseId(enterpriseId);
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(displayDTO.getEnterpriseId());
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(tbDisplayTableRecordService.displayList(displayDTO.getEnterpriseId(),displayDTO));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }

    @Override
    @ShenyuSofaClient(path = "/display/detail")
    public OpenApiResponseVO displayDetail(DisplayDTO displayDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        displayDTO.setEnterpriseId(enterpriseId);
        try {
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(displayDTO.getEnterpriseId());
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());
            return OpenApiResponseVO.success(tbDisplayTableRecordService.displayRecordDetail(displayDTO.getEnterpriseId(),displayDTO.getRecordId()));
        }catch (ServiceException e){
            return OpenApiResponseVO.fail(e.getErrorCode(),e.getErrorMessage());
        }
    }

    @Override
    @ShenyuSofaClient(path = "/display/table/list")
    public OpenApiResponseVO<DisplayUnifyVO<DisplayTableVO>> getDisplayTableList(DisplayTableDTO reqDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(reqDTO.getJobnumber());
            verifyParams(reqDTO);

            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            // 传入的是工号，转换成用户id
            EnterpriseUserDO user = enterpriseUserMapper.selectByJobnumber(enterpriseId, reqDTO.getJobnumber());
            if (ObjectUtil.isNull(user)) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }

            List<TbMetaTableDO> tableList = tbMetaTableService.getDisplayTableAndUsedUserContainUserId(
                    enterpriseId, user.getUserId(), reqDTO.getName(), reqDTO.getStartTime(), reqDTO.getEndTime()
            );
            List<DisplayTableVO> list = CollStreamUtil.toList(tableList, v -> new DisplayTableVO(v.getId(), v.getTableName(), v.getTableProperty()));
            DisplayUnifyVO<DisplayTableVO> result = new DisplayUnifyVO<>();
            result.setJobnumber(reqDTO.getJobnumber());
            result.setList(list);
            return OpenApiResponseVO.success(result);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#display/table/list,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/display/sop/list")
    public OpenApiResponseVO<DisplayUnifyVO<DisplaySopVO>> getDisplaySopList(DisplayTableDTO reqDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(reqDTO.getJobnumber(), reqDTO.getStartTime(), reqDTO.getEndTime());
            verifyParams(reqDTO);

            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            // 传入的是工号，转换成用户id
            EnterpriseUserDO user = enterpriseUserMapper.selectByJobnumber(enterpriseId, reqDTO.getJobnumber());
            if (ObjectUtil.isNull(user)) {
                throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
            }

            List<TaskSopDO> sopList = taskSopService.getDisplaySopAndUsedUserContainUserId(
                    enterpriseId, user.getUserId(), reqDTO.getName(), reqDTO.getStartTime(), reqDTO.getEndTime()
            );
            List<DisplaySopVO> list = CollStreamUtil.toList(sopList, v -> new DisplaySopVO(v.getId(), v.getFileName()));
            DisplayUnifyVO<DisplaySopVO> result = new DisplayUnifyVO<>();
            result.setJobnumber(reqDTO.getJobnumber());
            result.setList(list);
            return OpenApiResponseVO.success(result);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#display/sop/list,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    @Override
    @ShenyuSofaClient(path = "/display/task/list")
    public OpenApiResponseVO<DisplayUnifyVO<DisplayTaskVO>> getDisplayTaskList(DisplayTaskDTO reqDTO) {
        String enterpriseId = RpcLocalHolder.getEnterpriseId();
        try {
            OpenApiParamCheckUtils.checkNecessaryParam(reqDTO.getJobnumber(), reqDTO.getStartTime(), reqDTO.getEndTime(), reqDTO.getReturnLimit());
            verifyParams(reqDTO);

            DataSourceHelper.reset();
            EnterpriseConfigDO enterpriseConfig = enterpriseConfigDao.getEnterpriseConfig(enterpriseId);
            DataSourceHelper.changeToSpecificDataSource(enterpriseConfig.getDbName());

            List<DisplayTaskVO> list = taskStoreService.getDisplayStoreTaskList(enterpriseId, reqDTO);
            DisplayUnifyVO<DisplayTaskVO> result = new DisplayUnifyVO<>();
            result.setJobnumber(reqDTO.getJobnumber());
            result.setList(list);
            return OpenApiResponseVO.success(result);
        } catch (ServiceException e) {
            return OpenApiResponseVO.fail(e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("openApi#display/task/list,Exception", e);
            return OpenApiResponseVO.fail();
        }
    }

    /**
     * 参数校验
     * @param reqDTO 请求DTO对象
     */
    private void verifyParams(DisplayBaseDTO reqDTO) {
        // 开始日期默认为当前时间往前推90天
        if (StringUtils.isBlank(reqDTO.getStartTime())) {
            reqDTO.setStartTime(LocalDate.now().minusDays(90L).toString());
        }
        // 结束日期默认为当前时间
        if (StringUtils.isBlank(reqDTO.getEndTime())) {
            reqDTO.setEndTime(LocalDate.now().toString());
        }
        LocalDate startTime = LocalDate.parse(reqDTO.getStartTime());
        LocalDate endTime = LocalDate.parse(reqDTO.getEndTime());
        if (endTime.isBefore(startTime)) {
            throw new ServiceException(ErrorCodeEnum.END_TIME_BEFORE_START_TIME);
        }
        if (ChronoUnit.DAYS.between(startTime, endTime) >= 100) {
            throw new ServiceException(ErrorCodeEnum.LIMIT_QUERY_TIME_LENGTH_100);
        }
        // 把结束时间往后一天，用于时间左闭右开的筛选
        reqDTO.setEndTime(LocalDate.parse(reqDTO.getEndTime()).plusDays(1L).toString());
        if (reqDTO instanceof DisplayTaskDTO) {
            DisplayTaskDTO taskDTO = (DisplayTaskDTO) reqDTO;
            if (Constants.ONE_HUNDRED.compareTo(taskDTO.getReturnLimit()) < 0) {
                throw new ServiceException(ErrorCodeEnum.PAGE_SIZE_MAX);
            }
        }
    }
}
