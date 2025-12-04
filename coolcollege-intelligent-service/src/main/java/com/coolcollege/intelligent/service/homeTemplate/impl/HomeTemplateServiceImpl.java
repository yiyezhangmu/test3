package com.coolcollege.intelligent.service.homeTemplate.impl;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.sync.conf.Role;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dao.system.SysRoleMapper;
import com.coolcollege.intelligent.dao.system.dao.SysRoleDao;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateDAO;
import com.coolcollege.intelligent.mapper.homeTemplate.HomeTemplateRoleMappingDAO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.CommonFunctionsDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.ComponentsJsonDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateRoleMappingDTO;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateDO;
import com.coolcollege.intelligent.model.homeTemplate.HomeTemplateRoleMappingDO;
import com.coolcollege.intelligent.model.homeTemplate.VO.HomeTemplateVO;
import com.coolcollege.intelligent.model.system.SysRoleDO;
import com.coolcollege.intelligent.model.system.VO.SysRoleBaseVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateRoleMappingService;
import com.coolcollege.intelligent.service.homeTemplate.HomeTemplateService;
import com.coolcollege.intelligent.util.RedisUtilPool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 15:56
 * @Version 1.0
 */
@Service
public class HomeTemplateServiceImpl implements HomeTemplateService {

    private final static Integer TEMPLATE_NAME_LIMIT_LENGTH = 30;

    private final static Integer TEMPLATE_DESCRIPTION_LIMIT_LENGTH = 128;

    /**
     * common_functions_enterpriseId_id/key
     */
    private final static String TEMPLATE_SAVE_KEY = "home_template_%s_%s";

    private final static String CHECK_TYPE_ID = "id";

    private final static String CHECK_TYPE_KEY = "key";

    /**
     * 组件JSON
     * common_functions_enterpriseId_userId
     */
    private final static String COMMON_FUNCTIONS = "common_functions_%s_%s";

    @Autowired
    HomeTemplateDAO homeTemplateDAO;
    @Autowired
    HomeTemplateRoleMappingDAO homeTemplateRoleMappingDAO;
    @Autowired
    SysRoleDao sysRoleDao;
    @Autowired
    SysRoleMapper sysRoleMapper;
    @Autowired
    private RedisUtilPool redisUtilPool;
    @Autowired
    HomeTemplateRoleMappingService homeTemplateRoleMappingService;

    @Override
    public Boolean publishHomeTemplate(String enterpriseId, HomeTemplateDTO homeTemplateDTO, CurrentUser user) {
        //校验模板信息
        checkHomeTemplate(homeTemplateDTO);
        HomeTemplateDO homeTemplateDO = convertHomeTemplateDTO(homeTemplateDTO, user);
        //发布的时候可能是编辑，可能是新增的模板
        if (homeTemplateDTO.getId()!=null){
            homeTemplateDO.setId(homeTemplateDTO.getId());
            homeTemplateDAO.updateById(enterpriseId,homeTemplateDO);
            //删除保存状态的模板信息
            redisUtilPool.delKey(String.format(TEMPLATE_SAVE_KEY,enterpriseId,homeTemplateDTO.getId()));
        }else {
            homeTemplateDAO.insert(enterpriseId, homeTemplateDO);
        }
        //删除角色已有的模板绑定 针对
        homeTemplateRoleMappingDAO.deletedByTemplateId(enterpriseId, homeTemplateDTO.getId());
        homeTemplateRoleMappingDAO.deletedByRoleIds(enterpriseId,homeTemplateDTO.getRoleIds());
        //处理角色首页模板映射
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = handleHomeTemplateRoleMapping(homeTemplateDTO.getRoleIds(), homeTemplateDO.getId(), user);
        //批量插入
        homeTemplateRoleMappingDAO.batchInsert(enterpriseId,homeTemplateRoleMappingDOS);
        return Boolean.TRUE;
    }

    @Override
    public List<HomeTemplateVO> selectByKey(String enterpriseId, String key,String checkType,CurrentUser currentUser) {
        ArrayList<HomeTemplateVO> homeTemplateVOS = new ArrayList<>();
        if (CHECK_TYPE_ID.equals(checkType)){
            HomeTemplateDO homeTemplateDO = homeTemplateDAO.selectById(enterpriseId, Integer.valueOf(key));
            HomeTemplateVO homeTemplateVO = convertHomeTemplateDO(homeTemplateDO);
            //查询该模板有哪些角色应用
            List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByHomeTemplateId(enterpriseId, Arrays.asList(Integer.valueOf(key)));
            List<Long> roleIds = new ArrayList<>();
            List<SysRoleBaseVO> sysRoleBaseVOS = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(homeTemplateRoleMappingDOS)){
                roleIds = homeTemplateRoleMappingDOS.stream().map(HomeTemplateRoleMappingDO::getRoleId).collect(Collectors.toList());
                List<SysRoleDO> sysRoleDOS = sysRoleDao.selectRoleByRoleIds(enterpriseId, roleIds);
                sysRoleBaseVOS = convertRoleDO(sysRoleDOS);
            }
            homeTemplateVO.setSysRoleBaseVOS(sysRoleBaseVOS);
            homeTemplateVO.setCheckType(CHECK_TYPE_ID);
            homeTemplateVOS.add(homeTemplateVO);
        }
        String string = redisUtilPool.getString(String.format(TEMPLATE_SAVE_KEY,enterpriseId, key));
        if (StringUtils.isNotEmpty(string)){
            HomeTemplateDTO homeTemplateDTO = JSONObject.parseObject(string, HomeTemplateDTO.class);
            HomeTemplateDO homeTemplateDO = convertHomeTemplateDTO(homeTemplateDTO, currentUser);
            HomeTemplateVO homeTemplateVO = convertHomeTemplateDO(homeTemplateDO);
            List<Long> roleIds = homeTemplateDTO.getRoleIds();
            if (CollectionUtils.isNotEmpty(roleIds)){
                List<SysRoleDO> sysRoleDOS = sysRoleDao.selectRoleByRoleIds(enterpriseId, roleIds);
                List<SysRoleBaseVO> sysRoleBaseVOS = convertRoleDO(sysRoleDOS);
                homeTemplateVO.setSysRoleBaseVOS(sysRoleBaseVOS);
            }
            homeTemplateVO.setCheckType(CHECK_TYPE_KEY);

            homeTemplateVOS.add(homeTemplateVO);
        }
        return homeTemplateVOS;
    }

    @Override
    public PageInfo<HomeTemplateVO> listHomeTemplateVO(String enterpriseId, Integer pageSize, Integer pageNum, Long roleId, String templateName) {
        //分页查询首页模板
        PageHelper.startPage(pageNum,pageSize);
        List<HomeTemplateDO> homeTemplateDOS = homeTemplateDAO.selectAllData(enterpriseId,templateName);
        if (CollectionUtils.isEmpty(homeTemplateDOS)){
            new PageInfo<>();
        }
        List<Integer> listHomeTempLateId = homeTemplateDOS.stream().map(HomeTemplateDO::getId).collect(Collectors.toList());

        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByHomeTemplateId(enterpriseId, listHomeTempLateId);

        List<Long> roleIds = homeTemplateRoleMappingDOS.stream().map(HomeTemplateRoleMappingDO::getRoleId).distinct().collect(Collectors.toList());
        //根据模板ID分组
        Map<Integer, List<HomeTemplateRoleMappingDO>> homeMap = homeTemplateRoleMappingDOS
                .stream().collect(Collectors.groupingBy(HomeTemplateRoleMappingDO::getTemplateId));
        List<SysRoleDO> sysRoleDOS = sysRoleDao.selectRoleByRoleIds(enterpriseId, roleIds);

        Map<Long, SysRoleDO> SysRoleMap = sysRoleDOS.stream().collect(Collectors.toMap(SysRoleDO::getId, data -> data));

        PageInfo pageInfo = new PageInfo<HomeTemplateDO>(homeTemplateDOS);
        List<HomeTemplateVO> result = new ArrayList<>();
        for (HomeTemplateDO homeTemplateDO:homeTemplateDOS) {
            HomeTemplateVO homeTemplateVO = convertHomeTemplateDO(homeTemplateDO);
            List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingList = homeMap.getOrDefault(homeTemplateDO.getId(),new ArrayList<>());
            List<SysRoleDO> SysRoleDOS = new ArrayList<>();
            Boolean currentHomeTemplateStatus = Boolean.FALSE;
            for (HomeTemplateRoleMappingDO homeTemplateRoleMappingDO : homeTemplateRoleMappingList) {
                if (roleId!=null&&homeTemplateRoleMappingDO.getRoleId().equals(roleId)){
                    currentHomeTemplateStatus = Boolean.TRUE;
                }
                SysRoleDO sysRoleDO = SysRoleMap.get(homeTemplateRoleMappingDO.getRoleId());
                if (sysRoleDO!=null){
                    SysRoleDOS.add(sysRoleDO);
                }
            }
            homeTemplateVO.setCurrentHomeTemplateStatus(currentHomeTemplateStatus);
            List<SysRoleBaseVO> sysRoleBaseVOS = convertRoleDO(SysRoleDOS);

            homeTemplateVO.setSysRoleBaseVOS(sysRoleBaseVOS);
            result.add(homeTemplateVO);
        }
        pageInfo.setList(result);
        return pageInfo;
    }

    @Override
    public Boolean deletedById(String enterpriseId, Integer id,CurrentUser user) {
        //根据模板ID查询该模板是否有绑定的角色，有绑定的角色不能删除
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByHomeTemplateId(enterpriseId, Arrays.asList(id));
        if (CollectionUtils.isNotEmpty(homeTemplateRoleMappingDOS)){
            throw new ServiceException(ErrorCodeEnum.HOME_TEMPLATE_ROLE_MAPPING_IS_NOT_NULL);
        }
        HomeTemplateDO homeTemplateDO = new HomeTemplateDO();
        homeTemplateDO.setDeleted(Constants.INDEX_ONE);
        homeTemplateDO.setId(id);
        homeTemplateDO.setUpdateId(user.getUserId());
        homeTemplateDAO.updateById(enterpriseId,homeTemplateDO);
        return null;
    }

    @Override
    public String previewHomeTemplate(String enterpriseId, HomeTemplateDTO homeTemplateDTO, CurrentUser user) {
        //判断是发布的模板预览还是新创建的模板预览 不为null表示发布的模板编辑之后预览
        String key = "";
        if (homeTemplateDTO.getId()!=null){
            //存储再redis,发布的时候删除
            redisUtilPool.setString(String.format(TEMPLATE_SAVE_KEY,enterpriseId,homeTemplateDTO.getId()),JSONObject.toJSONString(homeTemplateDTO));
            key = String.valueOf(homeTemplateDTO.getId());
        }else {
            key = UUIDUtils.get8UUID();
            redisUtilPool.setString(String.format(TEMPLATE_SAVE_KEY,enterpriseId,key) ,JSONObject.toJSONString(homeTemplateDTO),60*60*4);
        }
        return key;
    }

    @Override
    public HomeTemplateVO selectById(String enterpriseId, Integer id) {
        HomeTemplateDO homeTemplateDO = homeTemplateDAO.selectById(enterpriseId, id);
        HomeTemplateVO homeTemplateVO = convertHomeTemplateDO(homeTemplateDO);
        //查询该模板有哪些角色应用
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByHomeTemplateId(enterpriseId, Arrays.asList(id));
        List<Long> roleIds = new ArrayList<>();
        List<SysRoleBaseVO> sysRoleBaseVOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(homeTemplateRoleMappingDOS)){
            roleIds = homeTemplateRoleMappingDOS.stream().map(HomeTemplateRoleMappingDO::getRoleId).collect(Collectors.toList());
            List<SysRoleDO> sysRoleDOS = sysRoleDao.selectRoleByRoleIds(enterpriseId, roleIds);
            sysRoleBaseVOS = convertRoleDO(sysRoleDOS);
        }
        homeTemplateVO.setSysRoleBaseVOS(sysRoleBaseVOS);
        return homeTemplateVO;
    }

    @Override
    public Boolean useImmediately(String enterpriseId, HomeTemplateRoleMappingDTO homeTemplateRoleMappingDTO, CurrentUser user) {
        if (homeTemplateRoleMappingDTO.getTemplateId()==null||homeTemplateRoleMappingDTO.getRoleId()==null){
            throw new ServiceException(ErrorCodeEnum.PARAM_MISSING);
        }
        //删除该角色已有的模板
        homeTemplateRoleMappingDAO.deletedByRoleIds(enterpriseId, Arrays.asList(homeTemplateRoleMappingDTO.getRoleId()));
        HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = new HomeTemplateRoleMappingDO();
        homeTemplateRoleMappingDO.setTemplateId(homeTemplateRoleMappingDTO.getTemplateId());
        homeTemplateRoleMappingDO.setRoleId(homeTemplateRoleMappingDTO.getRoleId());
        homeTemplateRoleMappingDO.setCreateId(user.getUserId());
        homeTemplateRoleMappingDO.setCreateTime(new Date());
        homeTemplateRoleMappingDO.setUpdateTime(new Date());
        homeTemplateRoleMappingDO.setUpdateId(user.getUserId());
        homeTemplateRoleMappingDAO.batchInsert(enterpriseId,Arrays.asList(homeTemplateRoleMappingDO));
        return Boolean.TRUE;
    }

    @Override
    public HomeTemplateVO getCurrentUserHomeTemplate(String enterpriseId, CurrentUser user) {
        SysRoleDO sysRoleDo = sysRoleMapper.getHighestPrioritySysRoleDoByUserId(enterpriseId, user.getUserId());
        if(sysRoleDo == null){
            // 如果没有最高优先级的，给未分配的角色
            sysRoleDo = sysRoleMapper.getRoleByRoleEnum(enterpriseId, Role.EMPLOYEE.getRoleEnum());
        }
        if (sysRoleDo!=null){
            List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = homeTemplateRoleMappingDAO.selectByRoleIds(enterpriseId, Arrays.asList(sysRoleDo.getId()));
            HomeTemplateRoleMappingDO homeTemplateRoleMappingDO ;
            if (CollectionUtils.isNotEmpty(homeTemplateRoleMappingDOS)){
                homeTemplateRoleMappingDO =  homeTemplateRoleMappingDOS.get(Constants.INDEX_ZERO);
            }else {
                //如果该人员最高角色没有模板，添加一个模板映射
                homeTemplateRoleMappingDO =  homeTemplateRoleMappingService.initHomeTempRoleMapping(sysRoleDo.getId(), sysRoleDo.getPositionType());
                homeTemplateRoleMappingDAO.batchInsert(enterpriseId,Arrays.asList(homeTemplateRoleMappingDO));
            }
            HomeTemplateDO homeTemplateDO = homeTemplateDAO.selectById(enterpriseId, homeTemplateRoleMappingDO.getTemplateId());
            return convertHomeTemplateDO(homeTemplateDO);

        }
        return new HomeTemplateVO();
    }

    @Override
    public Boolean saveCurrentUserCommonFunctions(String enterpriseId, CommonFunctionsDTO commonFunctionsDTO, CurrentUser user) {
        if (StringUtils.isNotEmpty(commonFunctionsDTO.getCommonFunctions())){
            redisUtilPool.setString(String.format(COMMON_FUNCTIONS,enterpriseId,user.getUserId()),commonFunctionsDTO.getCommonFunctions());
        }
        return Boolean.TRUE;
    }

    @Override
    public CommonFunctionsDTO getCurrentUserCommonFunctions(String enterpriseId, CurrentUser user) {
        String string = redisUtilPool.getString(String.format(COMMON_FUNCTIONS, enterpriseId, user.getUserId()));
        CommonFunctionsDTO commonFunctionsDTO = new CommonFunctionsDTO();
        if (StringUtils.isNotEmpty(string)){
            commonFunctionsDTO.setCommonFunctions(string);
        }
        return commonFunctionsDTO;
    }

    /**
     * HomeTemplateDTO->HomeTemplateDO
     * @param homeTemplateDTO
     * @param user
     * @return
     */
    public HomeTemplateDO convertHomeTemplateDTO(HomeTemplateDTO homeTemplateDTO,CurrentUser user){
        HomeTemplateDO homeTemplateDO = new HomeTemplateDO();
        homeTemplateDO.setTemplateName(homeTemplateDTO.getTemplateName());
        homeTemplateDO.setTemplateDescription(homeTemplateDTO.getTemplateDescription());
        homeTemplateDO.setIsDefault(Constants.INDEX_ZERO);
        homeTemplateDO.setDeleted(Constants.INDEX_ZERO);
        homeTemplateDO.setCreateId(user.getUserId());
        homeTemplateDO.setCreateTime(new Date());
        homeTemplateDO.setUpdateId(user.getUserId());
        homeTemplateDO.setUpdateTime(new Date());

        if (homeTemplateDTO.getPcComponentsJson()!=null&&homeTemplateDTO.getPcComponentsJson().size()>0){
            ComponentsJsonDTO pcComponentsJsonDTO = new ComponentsJsonDTO();
            pcComponentsJsonDTO.setComponentsJson(homeTemplateDTO.getPcComponentsJson());
            homeTemplateDO.setPcComponentsJson(JSONObject.toJSONString(pcComponentsJsonDTO));
        }

        if (homeTemplateDTO.getPcComponentsJson()!=null&&homeTemplateDTO.getAppComponentsJson().size()>0){
            ComponentsJsonDTO appComponentsJsonDTO = new ComponentsJsonDTO();
            appComponentsJsonDTO.setComponentsJson(homeTemplateDTO.getAppComponentsJson());
            homeTemplateDO.setAppComponentsJson(JSONObject.toJSONString(appComponentsJsonDTO));
        }


        return homeTemplateDO;
    }


    /**
     * HomeTemplateDO->HomeTemplateVO
     * @param homeTemplateDO
     * @return
     */
    public HomeTemplateVO convertHomeTemplateDO(HomeTemplateDO homeTemplateDO){
        HomeTemplateVO HomeTemplateVO = new HomeTemplateVO();
        HomeTemplateVO.setTemplateName(homeTemplateDO.getTemplateName());
        HomeTemplateVO.setTemplateDescription(homeTemplateDO.getTemplateDescription());
        HomeTemplateVO.setIsDefault(homeTemplateDO.getIsDefault());
        HomeTemplateVO.setDeleted(homeTemplateDO.getDeleted());
        HomeTemplateVO.setCreateId(homeTemplateDO.getCreateId());
        HomeTemplateVO.setCreateTime(homeTemplateDO.getCreateTime());
        HomeTemplateVO.setUpdateId(homeTemplateDO.getUpdateId());
        HomeTemplateVO.setUpdateTime(homeTemplateDO.getUpdateTime());
        HomeTemplateVO.setId(homeTemplateDO.getId());
        if (StringUtils.isNotEmpty(homeTemplateDO.getAppComponentsJson())){
            HomeTemplateVO.setAppComponentsJson((JSONObject)JSONObject.parseObject(homeTemplateDO.getAppComponentsJson()).get("componentsJson"));
        }
        if (StringUtils.isNotEmpty(homeTemplateDO.getPcComponentsJson())){
            HomeTemplateVO.setPcComponentsJson((JSONObject)JSONObject.parseObject(homeTemplateDO.getPcComponentsJson()).get("componentsJson"));
        }

        return HomeTemplateVO;
    }


    /**
     * 首页模板角色关系处理
     * @param ids
     * @param homeTemplateId
     * @param user
     * @return
     */
    public List<HomeTemplateRoleMappingDO> handleHomeTemplateRoleMapping(List<Long> ids,Integer homeTemplateId,CurrentUser user){
        List<HomeTemplateRoleMappingDO> homeTemplateRoleMappingDOS = new ArrayList<>();
        if (CollectionUtils.isEmpty(ids)){
            return homeTemplateRoleMappingDOS;
        }
        for (Long roleId:ids) {
            HomeTemplateRoleMappingDO homeTemplateRoleMappingDO = new HomeTemplateRoleMappingDO();
            homeTemplateRoleMappingDO.setTemplateId(homeTemplateId);
            homeTemplateRoleMappingDO.setRoleId(roleId);
            homeTemplateRoleMappingDO.setCreateId(user.getUserId());
            homeTemplateRoleMappingDO.setCreateTime(new Date());
            homeTemplateRoleMappingDO.setUpdateId(user.getUserId());
            homeTemplateRoleMappingDO.setUpdateTime(new Date());
            homeTemplateRoleMappingDOS.add(homeTemplateRoleMappingDO);
        }
        return homeTemplateRoleMappingDOS;
    }

    /**
     * convertRoleDO->convertRoleVO
     * @param sysRoleDOS
     * @return
     */
    public List<SysRoleBaseVO> convertRoleDO( List<SysRoleDO> sysRoleDOS){
        List<SysRoleBaseVO> sysRoleBaseVOS = new ArrayList<>();
        for (SysRoleDO roleDO:sysRoleDOS) {
            SysRoleBaseVO sysRoleBaseVO = new SysRoleBaseVO();
            sysRoleBaseVO.setRoleName(roleDO.getRoleName());
            sysRoleBaseVO.setId(roleDO.getId());
            sysRoleBaseVOS.add(sysRoleBaseVO);
        }
        return sysRoleBaseVOS;
    }

    /**
     * 校验
     * @param homeTemplateDTO
     */
    public void checkHomeTemplate(HomeTemplateDTO homeTemplateDTO){
        if (StringUtils.isEmpty(homeTemplateDTO.getTemplateName())){
            throw new ServiceException(ErrorCodeEnum.HOME_TEMPLATE_NAME_IS_NOT_NULL);
        }
        if (homeTemplateDTO.getTemplateName().length()>TEMPLATE_NAME_LIMIT_LENGTH){
            throw new ServiceException(ErrorCodeEnum.TEMPLATE_NAME_LIMIT_LENGTH);
        }
        if (StringUtils.isNotEmpty(homeTemplateDTO.getTemplateName())&&homeTemplateDTO.getTemplateName().length()>TEMPLATE_DESCRIPTION_LIMIT_LENGTH){
            throw new ServiceException(ErrorCodeEnum.TEMPLATE_DESCRIPTION_LIMIT_LENGTH);
        }
    }
}
