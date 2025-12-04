package com.coolcollege.intelligent.service.enterprise.setting.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.myj.MyjEnterpriseEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.http.HttpRequest;
import com.coolcollege.intelligent.common.util.DateUtils;
import com.coolcollege.intelligent.dao.enterprise.EnterpriseSettingMapper;
import com.coolcollege.intelligent.dao.menu.SysMenuExtendMapper;
import com.coolcollege.intelligent.dto.EnterpriseThemeColorSettingsAddRpcDTO;
import com.coolcollege.intelligent.model.enterprise.EnterpriseSettingDO;
import com.coolcollege.intelligent.model.enterprise.param.DingDingSyncSettingUpdParam;
import com.coolcollege.intelligent.model.enterprise.request.EnterpriseThemeColorSettingsDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseSettingVO;
import com.coolcollege.intelligent.model.enums.ScheduleCallBackEnum;
import com.coolcollege.intelligent.model.menu.SysMenuExtendDO;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleCallBackRequest;
import com.coolcollege.intelligent.model.scheduler.request.ScheduleFixedRequest;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.rpc.config.EnterpriseSettingRpcService;
import com.coolcollege.intelligent.service.dingSync.DingDeptSyncService;
import com.coolcollege.intelligent.service.enterprise.setting.EnterpriseSettingService;
import com.coolcollege.intelligent.service.fileUpload.OssClientService;
import com.coolcollege.intelligent.util.ScheduleCallBackUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * describe:
 *
 * @author wxp
 * @date 2021/3/25
 */
@Service(value = "enterpriseSettingService")
@Slf4j
public class EnterpriseSettingServiceImpl implements EnterpriseSettingService {

    @Resource
    private EnterpriseSettingMapper enterpriseSettingMapper;
    @Resource
    private SysMenuExtendMapper sysMenuExtendMapper;
    @Autowired
    @Lazy
    private DingDeptSyncService dingDeptSyncService;

    @Value("${scheduler.callback.task.url}")
    private String schedulerCallbackTaskUrl;

    @Value("${scheduler.api.url}")
    private String schedulerApiUrl;
    @Resource
    private OssClientService ossClientService;

    @Resource
    private EnterpriseSettingRpcService enterpriseSettingRpcService;

    private static final String SCHEDULE_DELETE_CODE="deleted";
    private   static final  Integer RANDOM_MINUTE= 59;
    private   static final  Integer RANDOM_MIN_HOUR= 2;
    private   static final  Integer RANDOM_MAX_HOUR= 7;



    @Override
    public EnterpriseSettingDO selectByEnterpriseId(String enterpriseId) {
        return enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
    }

    @Override
    public Boolean saveOrUpdateEnterpriseDingDingSyncSetting(String eid, DingDingSyncSettingUpdParam param, Long bossUserId, String username) {
        // 校验
        this.verify(eid, param);
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(eid);
        if(enterpriseSettingDO != null){
            BeanUtils.copyProperties(param,enterpriseSettingDO);
            if(MyjEnterpriseEnum.myjCompany(eid)){
                enterpriseSettingDO.setEnableDingSync(Constants.ENABLE_DING_SYNC_THIRD);
            }
            enterpriseSettingMapper.updateDingDingSyncSettingByEnterpriseId(enterpriseSettingDO);
        }else {
            enterpriseSettingDO = new EnterpriseSettingDO();
            BeanUtils.copyProperties(param,enterpriseSettingDO);
            if(MyjEnterpriseEnum.myjCompany(eid)){
                enterpriseSettingDO.setEnableDingSync(Constants.ENABLE_DING_SYNC_THIRD);
            }
            if(enterpriseSettingDO.getSyncDirectSuperior() == null){
                enterpriseSettingDO.setSyncDirectSuperior(false);
            }
            enterpriseSettingMapper.insert(enterpriseSettingDO);
        }
        if(param.getEnableDingSync() != Constants.ENABLE_DING_SYNC_NOT_OPEN){
            dingDeptSyncService.setDingSyncScheduler(eid, bossUserId.toString(), username);
        }
        return true;
    }

    @Override
    public EnterpriseSettingVO getEnterpriseSettingVOByEid(String enterpriseId) {
        EnterpriseSettingDO enterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(enterpriseId);
        if(enterpriseSettingDO == null){
            return null;
        }
        EnterpriseSettingVO enterpriseSettingVO = new EnterpriseSettingVO();
        BeanUtils.copyProperties(enterpriseSettingDO,enterpriseSettingVO);
        return enterpriseSettingVO;
    }

    private void verify(String eid, DingDingSyncSettingUpdParam param) {
        if(Objects.equals(param.getEnableDingSync(), Constants.ENABLE_DING_SYNC_OPEN)){
            /*EnterpriseOperateLogDO enterpriseOperateLogDO = enterpriseOperateLogMapper.getLatestLogByEnterpriseIdAndOptType(eid, SyncConfig.ENTERPRISE_OPERATE_LOG_SYNC);
            if(enterpriseOperateLogDO != null){
                throw new ServiceException(ServiceErrorCodeEnum.PARAM_MISS.getValue(), "关闭后无法再次打开，请联系开发人员！");
            }*/
            if(StrUtil.isEmpty(param.getDingSyncOrgScope())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "组织架构同步范围不能为空！");
            }else if(StrUtil.isEmpty(param.getDingSyncStoreRule())){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "门店同步规则配置不能为空！");
            }else if(param.getDingSyncRoleRule() == null){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "职位同步规则不能为空！");
            }else if(param.getDingSyncUserRegionStoreAuthRule() == null){
                throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED.getCode(), "用户区域门店同步规则不能为空！");
            }
        }
    }

    @Override
    public Boolean syncRolePermissions(String enterpriseId) {
        EnterpriseSettingVO vo = getEnterpriseSettingVOByEid(enterpriseId);
        return vo.getDingSyncRoleRule().equals(1) || vo.getDingSyncRoleRule().equals(3);
    }

    @Override
    public Boolean syncPositionPermissions(String enterpriseId) {
        EnterpriseSettingVO vo = getEnterpriseSettingVOByEid(enterpriseId);
        return vo.getDingSyncRoleRule().equals(2) || vo.getDingSyncRoleRule().equals(3);
    }

    @Override
    public int updateAccessCoolCollegeByEnterpriseId(EnterpriseSettingDO record) {
        return enterpriseSettingMapper.updateAccessCoolCollegeByEnterpriseId(record);
    }

    @Override
    public Boolean updateSyncPassengerByEid(String eid ,Boolean syncPassenger) {

        //新建或者删除schedule任务，保存配置。
        if(syncPassenger==null){
            return true;
        }
        EnterpriseSettingDO oldEnterpriseSettingDO = enterpriseSettingMapper.selectByEnterpriseId(eid);
        EnterpriseSettingDO enterpriseSettingDO =new EnterpriseSettingDO();
        enterpriseSettingDO.setSyncPassenger(syncPassenger);
        enterpriseSettingDO.setEnterpriseId(eid);
        //关闭schedule
        if(oldEnterpriseSettingDO.getSyncPassenger()&&!syncPassenger){
            String startTime = DateUtils.convertTimeToString(System.currentTimeMillis(), DateUtils.DATE_FORMAT_MINUTE);
            List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
            jobs.add(ScheduleCallBackUtil.getCallBack(
                    schedulerCallbackTaskUrl + "/v3/enterprises/"+eid+"/passenger/callback",
                    ScheduleCallBackEnum.api.getValue()));
            ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime, jobs);
            //一天执行一次
            fixedRequest.setInterval(24 * 60 * 60);
            log.info("同步客流数据的调度器，关闭调用定时器，开始调用***************参数{}" , JSON.toJSONString(fixedRequest));
            String result = HttpRequest.sendDelete(schedulerApiUrl + "/v2/45f92210375346858b6b6694967f44de/schedulers/"+oldEnterpriseSettingDO.getSyncPassengerScheduleId(),
                    JSON.toJSONString(fixedRequest), ScheduleCallBackUtil.buildHeaderMap());
            log.info("同步客流数据的调度器，关闭调用定时器，结果***************参数{}" ,JSON.toJSONString(result));
            JSONObject jsonObjectSchedule = JSONObject.parseObject(result);
            if(jsonObjectSchedule!=null&&jsonObjectSchedule.getBoolean(SCHEDULE_DELETE_CODE)){
                enterpriseSettingDO.setSyncPassengerScheduleId(null);
                enterpriseSettingMapper.updateSyncPassengerByEnterpriseId(enterpriseSettingDO);
            }
        }
        //开启schedule
        if(!oldEnterpriseSettingDO.getSyncPassenger()&&syncPassenger){

            Random random = new Random();
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            int minute = random.nextInt(RANDOM_MINUTE);
            int second = random.nextInt(RANDOM_MINUTE);
            int hour = RandomUtil.randomInt(RANDOM_MIN_HOUR, RANDOM_MAX_HOUR);
            LocalDateTime localDateTime = tomorrow.atTime(hour, minute, second);
            DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtils.DATE_FORMAT_MINUTE);
            String startTime = df.format(localDateTime);
            List<ScheduleCallBackRequest> jobs = Lists.newArrayList();
            jobs.add(ScheduleCallBackUtil.getCallBack(
                    schedulerCallbackTaskUrl + "/v3/enterprises/"+eid+"/passenger/callback",
                    ScheduleCallBackEnum.api.getValue()));
            ScheduleFixedRequest fixedRequest = new ScheduleFixedRequest(startTime, jobs);
            //一天执行一次
            fixedRequest.setInterval(24 * 60 * 60);
            log.info("同步客流数据的调度器，开始调用定时器，开始调用***************参数{}" , JSON.toJSONString(fixedRequest));
            String result = HttpRequest.sendPost(schedulerApiUrl + "/v2/45f92210375346858b6b6694967f44de/schedulers",
                    JSON.toJSONString(fixedRequest), ScheduleCallBackUtil.buildHeaderMap());
            log.info("同步客流数据的调度器，开始调用定时器，结果***************参数{}" ,JSON.toJSONString(result));
            JSONObject jsonObjectSchedule = JSONObject.parseObject(result);

            if(jsonObjectSchedule!=null){
                String schedulerId = jsonObjectSchedule.getString("scheduler_id");
                if(StringUtils.isBlank(schedulerId)){
                    throw new ServiceException(ErrorCodeEnum.PF_CANT_SCHEDULE);
                }
                enterpriseSettingDO.setSyncPassengerScheduleId(schedulerId);
                enterpriseSettingMapper.updateSyncPassengerByEnterpriseId(enterpriseSettingDO);

            }
        }
        return true;
    }

    @Override
    public Boolean deleteAppHomePagePic(String eid) {
        String dingCorpId = UserHolder.getUser().getDingCorpId();
        //将oss主页图片覆盖为默认图片
        boolean b = ossClientService.copyObject("home-pic/homepic.jpg","home-pic/" + dingCorpId + ".jpg");
        log.info("删除oss主页图片结果：{}",b);
        return b;
    }

    @Override
    public Boolean updateThemeColorSetting(String enterpriseId, EnterpriseThemeColorSettingsDTO param) {
        EnterpriseThemeColorSettingsAddRpcDTO themeColorSettingsAddRpcDTO = new EnterpriseThemeColorSettingsAddRpcDTO();
        themeColorSettingsAddRpcDTO.setEnterpriseId(enterpriseId);
        themeColorSettingsAddRpcDTO.setThemeColor(param.getThemeColor());
        themeColorSettingsAddRpcDTO.setMobileIcon(param.getMobileIcon());
        themeColorSettingsAddRpcDTO.setManageIcon(param.getManageIcon());
        enterpriseSettingRpcService.updateThemeColorSetting(enterpriseId, themeColorSettingsAddRpcDTO);
        if (CollectionUtils.isNotEmpty(param.getMenuExtendInfoList())) {
            List<Long> menuIdList = ListUtils.emptyIfNull(param.getMenuExtendInfoList()).stream().map(EnterpriseThemeColorSettingsDTO.SysMenuExtendInfo::getMenuId).collect(Collectors.toList());
            sysMenuExtendMapper.deleteByMenuIdList(enterpriseId, menuIdList);
            List<SysMenuExtendDO> sysMenuExtendDOList = param.getMenuExtendInfoList().stream().map(e -> {
                SysMenuExtendDO sysMenuExtendDO = new SysMenuExtendDO();
                sysMenuExtendDO.setMenuId(e.getMenuId());
                sysMenuExtendDO.setDefineName(e.getDefineName());
                sysMenuExtendDO.setMenuPic(e.getMenuPic());
                sysMenuExtendDO.setPlatform(e.getPlatform());
                return sysMenuExtendDO;
            }).collect(Collectors.toList());
            sysMenuExtendMapper.batchInsertMenuExtend(enterpriseId, sysMenuExtendDOList);
        }
        return true;
    }
}
