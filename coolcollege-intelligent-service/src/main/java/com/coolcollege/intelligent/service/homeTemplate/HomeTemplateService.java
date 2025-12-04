package com.coolcollege.intelligent.service.homeTemplate;

import com.coolcollege.intelligent.model.homeTemplate.DTO.CommonFunctionsDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateDTO;
import com.coolcollege.intelligent.model.homeTemplate.DTO.HomeTemplateRoleMappingDTO;
import com.coolcollege.intelligent.model.homeTemplate.VO.HomeTemplateVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.github.pagehelper.PageInfo;
import io.swagger.models.auth.In;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/6/23 15:50
 * @Version 1.0
 */
public interface HomeTemplateService {


    /**
     * 发布模板
     * @param enterpriseId
     * @param homeTemplateDTO
     * @return
     */
    Boolean publishHomeTemplate(String enterpriseId, HomeTemplateDTO homeTemplateDTO, CurrentUser user);


    /**
     * 根据KEy查询模板详情
     * @param enterpriseId
     * @param key
     * @param checkType
     * @return
     */
    List<HomeTemplateVO> selectByKey(String enterpriseId, String key,String checkType,CurrentUser currentUser);


    /**
     * 模板列表
     * @param enterpriseId
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageInfo<HomeTemplateVO> listHomeTemplateVO(String enterpriseId, Integer pageSize, Integer pageNum, Long roleId, String templateName);

    /**
     * 删除模板
     * @param enterpriseId
     * @param id
     * @param user
     * @return
     */
    Boolean deletedById(String enterpriseId, Integer id,CurrentUser user);


    /**
     * 模板预览
     * @param enterpriseId
     * @param homeTemplateDTO
     * @param user
     * @return
     */
    String previewHomeTemplate(String enterpriseId,HomeTemplateDTO homeTemplateDTO, CurrentUser user);


    /**
     * 根据ID查询模板详情
     * @param enterpriseId
     * @param id
     * @return
     */
    HomeTemplateVO selectById(String enterpriseId, Integer id);

    /**
     * 模板立即使用
     * @param enterpriseId
     * @param homeTemplateRoleMappingDTO
     * @param user
     * @return
     */
    Boolean useImmediately(String enterpriseId, HomeTemplateRoleMappingDTO homeTemplateRoleMappingDTO, CurrentUser user);


    /**
     * 获取当前人员使用的模板 查优先级最高的角色
     * @param enterpriseId
     * @param user
     * @return
     */
    HomeTemplateVO getCurrentUserHomeTemplate(String enterpriseId,CurrentUser user);

    /**
     * 保存当前用户 常用模块
     * @param enterpriseId
     * @param commonFunctionsDTO
     * @param user
     * @return
     */
    Boolean saveCurrentUserCommonFunctions(String enterpriseId, CommonFunctionsDTO commonFunctionsDTO, CurrentUser user);

    /**
     * 获取当前人员 常用模块
     * @param enterpriseId
     * @param user
     */
    CommonFunctionsDTO getCurrentUserCommonFunctions(String enterpriseId, CurrentUser user);
}
