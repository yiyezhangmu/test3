package com.coolcollege.intelligent.service.importexcel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataOperation;
import com.coolcollege.intelligent.common.enums.coolcollege.ChangeDataType;
import com.coolcollege.intelligent.common.enums.importexcel.ImportTaskStatusEnum;
import com.coolcollege.intelligent.common.enums.region.FixedRegionEnum;
import com.coolcollege.intelligent.common.exception.BaseException;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.importexcel.ImportTaskMapper;
import com.coolcollege.intelligent.dao.region.RegionMapper;
import com.coolcollege.intelligent.model.coolcollege.CoolStoreDataChangeDTO;
import com.coolcollege.intelligent.model.impoetexcel.ImportConstants;
import com.coolcollege.intelligent.model.impoetexcel.ImportTaskDO;
import com.coolcollege.intelligent.model.impoetexcel.dto.ExternalRegionImportDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.RegionImportDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.RegionNodeDTO;
import com.coolcollege.intelligent.model.impoetexcel.dto.UserImportDTO;
import com.coolcollege.intelligent.model.region.RegionDO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.producer.SimpleMessageService;
import com.coolcollege.intelligent.service.coolcollege.CoolCollegeIntegrationApiService;
import com.coolcollege.intelligent.service.region.RegionService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.RocketMqTagEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 邵凌志
 * @date 2020/12/8 10:01
 */
@Slf4j
@Service
public class RegionImportService extends ImportBaseService{

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private ImportTaskMapper importTaskMapper;

    @Autowired
    private GenerateOssFileService generateOssFileService;

    @Autowired
    @Lazy
    private RegionImportService regionImportService;

    @Autowired
    private RegionService regionService;

    @Autowired
    private CoolCollegeIntegrationApiService coolCollegeIntegrationApiService;

    @Resource
    private SimpleMessageService simpleMessageService;

    private static final int NAME_MAX_LENGTH = 40;

    private static final String ROOT_NAME = "一级区域必须是[%s]";

    private static final String REGION_LEVEL_ERROR = "[%s]已经存在于系统第[%d]级，您不能修改区域级别";

    private static final String NO_PARENT_REGION = "[%s]上级区域不能为空";

    private static final String REGION_NAME_BLANK = "第[%s]级区域不能为空";

    private static final String NAME_TOO_LANG = "第[%d]级区域名称长度超过40";

    private static final String REGION_NAME_SAME = "同一节点下区域名称重复";

    private static final String SAME_REGION_ERROR = "[%s]出现在同一行第[%d]和第[%d]层级，区域名称重复";

    private static final String SAME_REGION_ROW_ERROR = "[%s]出现在其他行第[%d]层级，区域名称重复";

    private static final String SAME_REGION_DIFF_SIRE_NAME = "[%s]已经是[%s]区域的子区域，区域名称重复";

    private static final String REGION_TITLE = "注：" +
            "\n1、区域根节点为企业名称，共支持10级组织架构" +
            "\n2、区域名称最多支持40个字" +
            "\n3、上级区域为必填，例如：填写了二级区域，则一定填写了一级区域" +
            "\n4、区域名称要唯一，不允许出现重复的区域" +
            "\n5、请从第3行开始填写要导入的数据，切勿改动表头内容及表格样式，否则会导入失败";

    @Async("importExportThreadPool")
    public void importData(String eid, CurrentUser user, Future<List<RegionImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            boolean lock = lock(eid, ImportConstants.REGION_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<RegionImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            log.info("总条数：{}", importList.size());
            importRegion1(eid, user, importList, contentType, task);
        } catch (BaseException e) {
            log.error("区域文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("区域文件上传失败："+eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR+e.getMessage());
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.REGION_KEY);
        }
    }

    @Async("importExportThreadPool")
    public void importExternalData(String eid, CurrentUser user, Future<List<ExternalRegionImportDTO>> importTask, String contentType, ImportTaskDO task) {
        DataSourceHelper.changeToSpecificDataSource(user.getDbName());
        try {
            boolean lock = lock(eid, ImportConstants.REGION_KEY);
            if (!lock) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EXIST_TASK);
                importTaskMapper.update(eid, task);
                return;
            }
            List<ExternalRegionImportDTO> importList = importTask.get();
            if (CollUtil.isEmpty(importList)) {
                task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
                task.setRemark(EMPTY_FILE);
                importTaskMapper.update(eid, task);
                return;
            }
            log.info("总条数：{}", importList.size());
            dealImportExternalRegion(eid, user, importList, contentType, task);
        } catch (BaseException e) {
            log.error("区域文件上传失败：{}"+ eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR + e.getResponseCodeEnum().getMessage());
            importTaskMapper.update(eid, task);
        }catch (Exception e) {
            log.error("区域文件上传失败："+eid, e);
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            task.setRemark(SYSTEM_ERROR+e.getMessage());
            importTaskMapper.update(eid, task);
        } finally {
            unlock(eid, ImportConstants.REGION_KEY);
        }
    }


    public void dealImportExternalRegion(String eid, CurrentUser user, List<ExternalRegionImportDTO> importList, String contentType, ImportTaskDO task) {
        // 需要导入的门店
        List<RegionDO> addList = new ArrayList<>();
        // 有错误的列表
        List<ExternalRegionImportDTO> errorList = new ArrayList<>();
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        // 根节点
        RegionDO root = allRegion.stream().filter(f -> f.getId() == 1L).collect(Collectors.toList()).get(0);
        String externalRegionName = allRegion.stream().filter(f -> FixedRegionEnum.EXTERNAL_USER.getId().equals(f.getId())).map(RegionDO::getName).findFirst().orElse(FixedRegionEnum.EXTERNAL_USER.getName());
        String rootName = Optional.ofNullable(root).map(RegionDO::getName).orElse("");
        Map<String, RegionDO> regionNameAndParentMap = new HashMap<>();
        for (RegionDO regionDO : allRegion) {
            String key = regionDO.getName() + "_";
            String suffix = Objects.nonNull(regionDO.getParentId()) ? regionDO.getParentId() : "0";
            key = key + suffix;
            regionNameAndParentMap.put(key, regionDO);
        }

        // 预留1000给用户页面操作的id  导入新增时id手动插入 方便做关联关系
        long id = regionMapper.selectMaxRegionId(eid) + 11000;
        for (ExternalRegionImportDTO region : importList) {
            if(StringUtils.isBlank(region.getRegion1())){
                region.setDec("一级部门不能为空");
                errorList.add(region);
                continue;
            }
            RegionNodeDTO regionNodeDto = region.getRegionNodeDto(rootName, externalRegionName);
            if(!root.getName().equals(regionNodeDto.getName())){
                region.setDec(String.format(ROOT_NAME, root.getName()));
                errorList.add(region);
                continue;
            }
            boolean isContinue = true;
            regionNodeDto.setId("1");
            RegionNodeDTO subNode = regionNodeDto.getSubNode();
            while (Objects.nonNull(subNode) && isContinue){
                String parentId = regionNodeDto.getId();
                isContinue = checkExternalNode(region, subNode, id++, parentId, addList, errorList, regionNameAndParentMap, user.getName());
                regionNodeDto = regionNodeDto.getSubNode();
                subNode = regionNodeDto.getSubNode();
            }
        }
        regionImportService.updateExternalRegionData(eid, importList.size(), addList, errorList, contentType, task);
    }

    public boolean checkExternalNode(ExternalRegionImportDTO region, RegionNodeDTO nodeDTO, Long id, String parentId, List<RegionDO> addList, List<ExternalRegionImportDTO> errorList, Map<String, RegionDO> regionNameAndParentMap, String userName){
        String name = nodeDTO.getName();
        if(StringUtils.isBlank(name)){
            region.setDec(String.format(REGION_NAME_BLANK, nodeDTO.getLevel()-2));
            errorList.add(region);
            return false;
        }
        // 长度超过40
        if (nodeDTO.getName().length() > NAME_MAX_LENGTH) {
            region.setDec(String.format(NAME_TOO_LANG, nodeDTO.getLevel()-2));
            errorList.add(region);
            return false;
        }
        String keySuffix = StringUtils.isNotBlank(parentId) ? parentId : "0";
        String key = name + "_" + keySuffix;
        RegionDO dbRegion = regionNameAndParentMap.get(key);
        if(Objects.nonNull(dbRegion)){
            if(Objects.isNull(nodeDTO.getSubNode())){
                //导入的名称 和db中的名称重复了 需要判断重复的是不是同一个父节点下
                region.setDec(String.format(REGION_NAME_SAME));
                errorList.add(region);
                return false;
            }
            nodeDTO.setId(dbRegion.getId().toString());
            return true;
        }
        RegionDO addDO = new RegionDO(id, name, parentId, UUIDUtils.get32UUID(), System.currentTimeMillis(), userName);
        addDO.setIsExternalNode(Boolean.TRUE);
        addList.add(addDO);
        regionNameAndParentMap.put(key, addDO);
        nodeDTO.setId(id.toString());
        return true;
    }

    public void importRegion1(String eid, CurrentUser user, List<RegionImportDTO> importList, String contentType, ImportTaskDO task) {
        // 需要导入的门店
        List<RegionDO> addList = new ArrayList<>();
        // 有错误的列表
        List<RegionImportDTO> errorList = new ArrayList<>();
        List<RegionDO> allRegion = regionMapper.getAllRegion(eid);
        Map<String, RegionDO> regionNameAndParentMap = new HashMap<>();
        for (RegionDO regionDO : allRegion) {
            String key = regionDO.getName() + "_";
            String suffix = Objects.nonNull(regionDO.getParentId()) ? regionDO.getParentId() : "0";
            key = key + suffix;
            regionNameAndParentMap.put(key, regionDO);
        }
        // 根节点
        RegionDO root = allRegion.stream().filter(f -> f.getId() == 1L).collect(Collectors.toList()).get(0);
        // 预留1000给用户页面操作的id  导入新增时id手动插入 方便做关联关系
        long id = regionMapper.selectMaxRegionId(eid) + 11000;
        for (RegionImportDTO region : importList) {
            RegionNodeDTO regionNodeDto = region.getRegionNodeDto();
            if(!root.getName().equals(regionNodeDto.getName())){
                region.setDec(String.format(ROOT_NAME, root.getName()));
                errorList.add(region);
                continue;
            }
            boolean isContinue = true;
            regionNodeDto.setId("1");
            RegionNodeDTO subNode = regionNodeDto.getSubNode();
            while (Objects.nonNull(subNode) && isContinue){
                String parentId = regionNodeDto.getId();
                isContinue = check(region, subNode, id++, parentId, addList, errorList, regionNameAndParentMap, user.getName());
                regionNodeDto = regionNodeDto.getSubNode();
                subNode = regionNodeDto.getSubNode();
            }
        }
        regionImportService.updateData(eid, importList.size(), addList, errorList, contentType, task);
    }

    public boolean check(RegionImportDTO region, RegionNodeDTO nodeDTO, Long id, String parentId, List<RegionDO> addList, List<RegionImportDTO> errorList, Map<String, RegionDO> regionNameAndParentMap, String userName){
        String name = nodeDTO.getName();
        if(StringUtils.isBlank(name)){
            region.setDec(String.format(REGION_NAME_BLANK, nodeDTO.getLevel()));
            errorList.add(region);
            return false;
        }
        // 长度超过40
        if (nodeDTO.getName().length() > NAME_MAX_LENGTH) {
            region.setDec(String.format(NAME_TOO_LANG, nodeDTO.getLevel()));
            errorList.add(region);
            return false;
        }
        String keySuffix = StringUtils.isNotBlank(parentId) ? parentId : "0";
        String key = name + "_" + keySuffix;
        RegionDO dbRegion = regionNameAndParentMap.get(key);
        if(Objects.nonNull(dbRegion)){
            if(Objects.isNull(nodeDTO.getSubNode())){
                //导入的名称 和db中的名称重复了 需要判断重复的是不是同一个父节点下
                region.setDec(String.format(REGION_NAME_SAME));
                errorList.add(region);
                return false;
            }
            nodeDTO.setId(dbRegion.getId().toString());
            return true;
        }
        RegionDO addDO = new RegionDO(id, name, parentId, UUIDUtils.get32UUID(), System.currentTimeMillis(), userName);
        addDO.setIsExternalNode(Boolean.FALSE);
        addList.add(addDO);
        regionNameAndParentMap.put(key, addDO);
        nodeDTO.setId(id.toString());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateData(String eid, int totalNum, List<RegionDO> regions, List<RegionImportDTO> errorList, String contentType, ImportTaskDO task) {
        int successNum = totalNum - errorList.size();
        if (CollUtil.isEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        } else {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            String url = generateOssFileService.generateOssExcel(errorList, eid, REGION_TITLE, "出错区域列表", contentType, RegionImportDTO.class);
            task.setFileUrl(url);
        }
        task.setSuccessNum(successNum);
        task.setTotalNum(totalNum);
        if (CollUtil.isNotEmpty(regions)) {
            Lists.partition(regions, 1000).forEach(f -> regionMapper.batchInsertRegion(eid, f));
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            List<String> regionIds = regions.stream()
                    .map(m -> String.valueOf(m.getId()))
                    .collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, regionIds, ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        }
        regionService.updateRegionPathAll(eid,1L);
        importTaskMapper.update(eid, task);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateExternalRegionData(String eid, int totalNum, List<RegionDO> regions, List<ExternalRegionImportDTO> errorList, String contentType, ImportTaskDO task) {
        int successNum = totalNum - errorList.size();
        if (CollUtil.isEmpty(errorList)) {
            task.setStatus(ImportTaskStatusEnum.SUCCESS.getCode());
        } else {
            task.setStatus(ImportTaskStatusEnum.ERROR.getCode());
            String url = generateOssFileService.generateOssExcel(errorList, eid, REGION_TITLE, "出错区域列表", contentType, ExternalRegionImportDTO.class);
            task.setFileUrl(url);
        }
        task.setSuccessNum(successNum);
        task.setTotalNum(totalNum);
        if (CollUtil.isNotEmpty(regions)) {
            Lists.partition(regions, 1000).forEach(f -> regionMapper.batchInsertRegion(eid, f));
            //区域即部门数据修改，推送酷学院，发送mq消息，异步操作
            List<String> regionIds = regions.stream()
                    .map(m -> String.valueOf(m.getId()))
                    .collect(Collectors.toList());
            coolCollegeIntegrationApiService.sendDataChangeMsg(eid, regionIds, ChangeDataOperation.ADD.getCode(), ChangeDataType.REGION.getCode());
        }
        regionService.updateRegionPathAll(eid,1L);
        importTaskMapper.update(eid, task);
    }

}
