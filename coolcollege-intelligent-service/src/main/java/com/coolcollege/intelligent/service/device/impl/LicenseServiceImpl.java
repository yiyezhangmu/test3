package com.coolcollege.intelligent.service.device.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.dao.device.DeviceChannelLicenseInfoMapper;
import com.coolcollege.intelligent.dao.device.DeviceLicenseInfoMapper;
import com.coolcollege.intelligent.model.device.DeviceChannelLicenseInfoDO;
import com.coolcollege.intelligent.model.device.DeviceLicenseInfoDO;
import com.coolcollege.intelligent.model.device.dto.DeviceGBLicenseApplyDTO;
import com.coolcollege.intelligent.model.device.gb28181.Channel;
import com.coolcollege.intelligent.model.device.request.DeviceChannelLicenseApply;
import com.coolcollege.intelligent.model.device.vo.DeviceChannelLicenseVO;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseDetailVO;
import com.coolcollege.intelligent.model.device.vo.DeviceLicenseVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.device.DeviceService;
import com.coolcollege.intelligent.service.device.LicenseService;
import com.coolcollege.intelligent.service.device.gb28181.GB28181Service;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AccountTypeEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LicenseServiceImpl implements LicenseService {

    @Resource(name = "yingShiGbOpenServiceImpl")
    private GB28181Service gb28181Service;
    @Resource
    private DeviceLicenseInfoMapper deviceLicenseInfoMapper;
    @Resource
    private DeviceChannelLicenseInfoMapper channelLicenseInfoMapper;
    @Resource
    private DeviceService deviceService;
    @Resource(name = "generalThreadPool")
    private ThreadPoolTaskExecutor executor;

    @Value("${yingshi.sip.id}")
    private String sipId;
    @Value("${yingshi.sip.area}")
    private String sipArea;
    @Value("${yingshi.sip.domain}")
    private String sipDomain;
    @Value("${yingshi.sip.port}")
    private String sipPort;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";


    @Override
    public void apply(String enterpriseId, Integer ipc, Integer nvr, AccountTypeEnum accountTypeEnum) {
        log.info("开始申请国标设备license,enterpriseId:{},ipc:{},nvr:{}", enterpriseId, ipc, nvr);
        CurrentUser user = UserHolder.getUser();
        String requestId = MDC.get(Constants.REQUEST_ID);
        executor.execute(() -> {
            MDC.put(Constants.REQUEST_ID, requestId);
            DataSourceHelper.changeToSpecificDataSource(user.getDbName());
            List<DeviceGBLicenseApplyDTO> result = new ArrayList<>();
            for (int i = 0; i < ipc + nvr; i++) {
                String deviceCategory = i < ipc ? "IPC" : "NVR";
                try {
                    DeviceGBLicenseApplyDTO dto = new DeviceGBLicenseApplyDTO(deviceCategory, Constants.X_STORE, generatePassword(), null);
                    String deviceSerial = gb28181Service.license(enterpriseId, accountTypeEnum, dto);
                    dto.setDeviceSerial(deviceSerial);
                    result.add(dto);
                } catch (Exception e) {
                    log.info("申请{}license失败, error:{}", deviceCategory, e.getMessage());
                }
            }
            if (CollectionUtils.isNotEmpty(result)) {
                log.info("申请license成功：{}，开始入库...", JSONObject.toJSONString(result));
                List<DeviceLicenseInfoDO> licenseInfos = CollStreamUtil.toList(result, v -> buildNewDeviceLicenseInfo(v, user));
                deviceLicenseInfoMapper.batchInsert(enterpriseId, licenseInfos);
                log.info("license，入库结束...");
            }
        });
    }

    @Override
    public PageInfo<DeviceLicenseVO> page(String enterpriseId, Integer type, String deviceSerial, String name, Integer status, Integer useByNew, Integer pageSize, Integer pageNum) {
        DataSourceHelper.changeToMy();
        PageHelper.startPage(pageNum, pageSize);
        List<DeviceLicenseVO> list = deviceLicenseInfoMapper.selectList(enterpriseId, type, deviceSerial, name, status, useByNew);
        list.forEach(a -> {
            String channelIds = a.getChannelIds();
            if (StringUtils.isNotBlank(channelIds)) {
                a.setChannelNum(channelIds.split(",").length);
            } else {
                a.setChannelNum(0);
            }
        });
        return new PageInfo<>(list);
    }

    @Override
    public DeviceLicenseDetailVO detail(String enterpriseId, String id) {
        DeviceLicenseDetailVO res = new DeviceLicenseDetailVO();

        DeviceLicenseInfoDO licenseInfo = deviceLicenseInfoMapper.selectOne(enterpriseId, id);
        BeanUtils.copyProperties(licenseInfo, res);
        res.setPassword(licenseInfo.getLicense());
        res.setType(licenseInfo.getType() == 1 ? "IPC" : "NVR");
        res.setSipUserName(licenseInfo.getDeviceId());
        res.setSipUserAuthId(licenseInfo.getDeviceId());

        res.setSipId(sipId);
        res.setSipArea(sipArea);
        res.setSipDomain(sipDomain);
        res.setSipPort(sipPort);
        try {
            res.setSipIp(InetAddress.getByName(sipDomain).getHostAddress());
        } catch (UnknownHostException e) {
            log.info("获取sip，服务器ip失败...", e);
        }

        //ipc 没有通道license 通道号固定1 通道编号为 deviceCode
        String channelIds = licenseInfo.getChannelIds();
        if (StringUtils.isNotBlank(channelIds)) {
            res.setChannelNum(channelIds.split(",").length);
            List<DeviceChannelLicenseInfoDO> channelLicenseInfos = channelLicenseInfoMapper.selectList(enterpriseId, id);
            List<Channel> channels = gb28181Service.channelStatus(enterpriseId, AccountTypeEnum.PLATFORM, licenseInfo.getDeviceSerial());
            if (CollectionUtils.isEmpty(channels)) {
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7149999);
            }
            Map<String, Channel> channelMap = channels.stream().collect(Collectors.toMap(Channel::getChannelNo, Function.identity()));
            List<DeviceChannelLicenseVO> channelLicenseVOS = new ArrayList<>();
            for (DeviceChannelLicenseInfoDO deviceChannelLicenseInfo : channelLicenseInfos) {
                DeviceChannelLicenseVO deviceChannelLicenseVO = new DeviceChannelLicenseVO();
                BeanUtils.copyProperties(deviceChannelLicenseInfo, deviceChannelLicenseVO);

                deviceChannelLicenseVO.setStatus(channelMap.get(deviceChannelLicenseInfo.getChannelNo()).getStatus().equals(1) ? "在线" : "离线");
                deviceChannelLicenseVO.setChannelNo(Integer.valueOf(deviceChannelLicenseInfo.getChannelNo()));
                deviceChannelLicenseVO.setId(String.valueOf(deviceChannelLicenseInfo.getId()));
                channelLicenseVOS.add(deviceChannelLicenseVO);
            }
            res.setChannelLicenseVOS(channelLicenseVOS);
        } else {
            res.setChannelNum(0);
        }
        Integer i = gb28181Service.deviceStatus(enterpriseId, AccountTypeEnum.PLATFORM, licenseInfo.getDeviceSerial(), licenseInfo.getType());
        res.setDeviceStatus(i.equals(1) ? "在线" : "离线");
        return res;
    }

    @Override
    public void editLicense(String enterpriseId, String name, String remark, String id) {
        deviceLicenseInfoMapper.update(enterpriseId, name, remark, id);
    }


    @Override
    public void deleteLicense(String enterpriseId, String id) {
        DeviceLicenseInfoDO licenseInfo = deviceLicenseInfoMapper.selectOne(enterpriseId, id);
        if (Objects.nonNull(licenseInfo)) {
            deviceLicenseInfoMapper.delete(enterpriseId, id);
            List<DeviceChannelLicenseInfoDO> channelLicenseInfos = channelLicenseInfoMapper.selectList(enterpriseId, id);
            if (StringUtils.isNotBlank(licenseInfo.getChannelIds()) && CollectionUtils.isNotEmpty(channelLicenseInfos)) {
                channelLicenseInfoMapper.delete(enterpriseId, Arrays.asList(licenseInfo.getChannelIds().split(",")));
            }
            gb28181Service.deleteGBDevice(enterpriseId, AccountTypeEnum.PLATFORM, licenseInfo.getDeviceSerial());
            //删除本系数据库设备记录
            try {
                deviceService.deleteDevice(enterpriseId, Collections.singletonList(licenseInfo.getDeviceSerial()), false);
            } catch (Exception e) {
                log.info("删除设备国标License时，本系统设备删除--{}", e.getMessage());
            }
        }
    }

    @Override
    public void applyChannel(String enterpriseId, DeviceChannelLicenseApply deviceChannelLicenseApply) {
        if (Integer.parseInt(deviceChannelLicenseApply.getChannelNo()) > 256) {
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000003);
        }

        DeviceLicenseInfoDO licenseInfo = deviceLicenseInfoMapper.selectOne(enterpriseId, deviceChannelLicenseApply.getLicenseId());
        if (Objects.isNull(licenseInfo)) {
            throw new ServiceException(ErrorCodeEnum.DATA_ERROR);
        }
        if (licenseInfo.getType() == 1) {
            throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000004);
        }
        //校验名称和通道号
        List<DeviceChannelLicenseInfoDO> channelLicenseInfos = channelLicenseInfoMapper.selectByLicenseIds(enterpriseId, Collections.singletonList(deviceChannelLicenseApply.getLicenseId()));
        if (CollectionUtils.isNotEmpty(channelLicenseInfos)) {
            Map<String, DeviceChannelLicenseInfoDO> noMap = channelLicenseInfos.stream().collect(Collectors.toMap(DeviceChannelLicenseInfoDO::getChannelNo, Function.identity()));
            Map<String, DeviceChannelLicenseInfoDO> nameMap = channelLicenseInfos.stream().collect(Collectors.toMap(DeviceChannelLicenseInfoDO::getChannelName, Function.identity()));
            if (Objects.nonNull(noMap.get(deviceChannelLicenseApply.getChannelNo()))) {
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000005);
            }
            if (Objects.nonNull(nameMap.get(deviceChannelLicenseApply.getChannelName()))) {
                throw new ServiceException(ErrorCodeEnum.YS_DEVICE_7000006);
            }
        }
        Channel channel = new Channel();
        channel.setChannelNo(deviceChannelLicenseApply.getChannelNo());
        channel.setChannelName(deviceChannelLicenseApply.getChannelName());
        channel.setRandomDeviceCode("true");
        channel.setBelongToDeviceCode(deviceChannelLicenseApply.getDeviceCode());
        gb28181Service.channel(enterpriseId, AccountTypeEnum.PLATFORM, Collections.singletonList(channel));

        DeviceChannelLicenseInfoDO channelLicenseInfo = new DeviceChannelLicenseInfoDO();
        channelLicenseInfo.setChannelSerial(channel.getChannelSerial());
        channelLicenseInfo.setChannelName(channel.getChannelName());
        channelLicenseInfo.setChannelNo(channel.getChannelNo());
        channelLicenseInfo.setDeviceCode(deviceChannelLicenseApply.getDeviceCode());
        channelLicenseInfo.setLicenseId(Long.valueOf(deviceChannelLicenseApply.getLicenseId()));
        channelLicenseInfo.setCreateTime(new Date());
        channelLicenseInfo.setCreateName(UserHolder.getUser().getUserId());
        channelLicenseInfo.setUpdateName(UserHolder.getUser().getUserId());
        channelLicenseInfo.setUpdateTime(new Date());
        channelLicenseInfo.setBindingTime(new Date());
        channelLicenseInfoMapper.insertSelective(channelLicenseInfo, enterpriseId);
        //更新设备license 通道数
        String channelIds = licenseInfo.getChannelIds();
        if (StringUtils.isNotBlank(channelIds)) {
            channelIds = channelIds + "," + channelLicenseInfo.getId();
        } else {
            channelIds = String.valueOf(channelLicenseInfo.getId());
        }
        licenseInfo.setChannelIds(channelIds);
        deviceLicenseInfoMapper.updateChannelIds(enterpriseId, licenseInfo.getId(), licenseInfo.getChannelIds());
    }

    @Override
    public void editChannel(String enterpriseId, String channelName, String id) {
        channelLicenseInfoMapper.update(enterpriseId, channelName, id);
    }

    @Override
    public void batchChannelLicense(String enterpriseId, Integer count, List<String> licenseIds) {
        List<DeviceLicenseInfoDO> licenseInfos = deviceLicenseInfoMapper.selectListByIds(enterpriseId, licenseIds);
        if (CollectionUtils.isEmpty(licenseInfos) && licenseInfos.size() != licenseIds.size()) {
            throw new ServiceException(ErrorCodeEnum.PARAMS_REQUIRED);
        }
        List<DeviceChannelLicenseInfoDO> channelLicenseInfos = channelLicenseInfoMapper.selectByLicenseIds(enterpriseId, licenseIds);
        Map<Long, List<DeviceChannelLicenseInfoDO>> licenseMap = CollStreamUtil.groupByKey(channelLicenseInfos, DeviceChannelLicenseInfoDO::getLicenseId);
        Map<Long, DeviceLicenseInfoDO> map = CollStreamUtil.toMap(licenseInfos, DeviceLicenseInfoDO::getId, v -> v);
        for (String licenseId : licenseIds) {
            Integer maxChannelNo = 1;
            List<DeviceChannelLicenseInfoDO> channelLicenseInfoList = licenseMap.get(Long.valueOf(licenseId));
            if (CollectionUtils.isNotEmpty(channelLicenseInfoList)) {
                DeviceChannelLicenseInfoDO channelLicenseInfo = channelLicenseInfoList.stream().max(Comparator.comparingInt(a -> Integer.parseInt(a.getChannelNo()))).get();
                maxChannelNo = Integer.parseInt(channelLicenseInfo.getChannelNo());
            }
            for (int i = 0; i < count; i++) {
                DeviceChannelLicenseApply channelLicenseApply = new DeviceChannelLicenseApply();
                channelLicenseApply.setLicenseId(licenseId);
                channelLicenseApply.setChannelNo(String.valueOf(maxChannelNo));
                channelLicenseApply.setChannelName(String.valueOf(maxChannelNo));
                channelLicenseApply.setDeviceCode(map.get(Long.valueOf(licenseId)).getDeviceCode());
                applyChannel(enterpriseId, channelLicenseApply);
                maxChannelNo += 1;
            }
        }
    }


    private static DeviceLicenseInfoDO buildNewDeviceLicenseInfo(DeviceGBLicenseApplyDTO dto, CurrentUser user) {
        String[] deviceSerialInfo = dto.getDeviceSerial().split(":");
        String sipId = deviceSerialInfo[0];
        String deviceCode = deviceSerialInfo[1];
        DeviceLicenseInfoDO licenseInfo = new DeviceLicenseInfoDO();
        licenseInfo.setDeviceCode(deviceCode);
        licenseInfo.setDeviceId(deviceCode);
        licenseInfo.setLicense(dto.getPassword());
        licenseInfo.setType(Objects.equals("IPC", dto.getDeviceCategory()) ? 1 : 2);

        licenseInfo.setStatus(0);
        licenseInfo.setApplyTime(new Date());
        licenseInfo.setUseByNew(0);
        licenseInfo.setApplyUser(user.getUserId());
        licenseInfo.setRegisterValidity(3600);
        licenseInfo.setHeartbeatCycle(30);
        licenseInfo.setHeartbeatTimeout(3);

        licenseInfo.setCreateTime(new Date());
        licenseInfo.setCreateName(user.getName());
        licenseInfo.setUpdateName(user.getName());
        licenseInfo.setUpdateTime(new Date());

        licenseInfo.setDeviceSerial(dto.getDeviceSerial());
        return licenseInfo;
    }

    private static String generatePassword() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
