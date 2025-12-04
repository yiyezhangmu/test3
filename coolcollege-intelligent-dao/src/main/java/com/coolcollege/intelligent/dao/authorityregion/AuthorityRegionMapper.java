package com.coolcollege.intelligent.dao.authorityregion;

import com.coolcollege.intelligent.model.authorityregion.AuthorityRegionDO;
import com.coolcollege.intelligent.model.authorityregion.request.AuthorityRegionPageRequest;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @Author: huhu
* @Date: 2024/11/25 16:04
* @Description: 
*/
public interface AuthorityRegionMapper {

    /**
     * 删除
     * @param id 主键
     * @param enterpriseId 企业id
     * @return 删除结果
     */
    int deleteByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 新增
     * @param record 授权区域信息
     * @param enterpriseId 企业id
     * @return 新增结果
     */
    int insertSelective(@Param("record") AuthorityRegionDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据主键查询详情
     * @param id 主键
     * @param enterpriseId 企业id
     * @return 详情信息
     */
    AuthorityRegionDO selectByPrimaryKey(@Param("id") Long id, @Param("enterpriseId") String enterpriseId);

    /**
     * 更新
     * @param record 授权区域信息
     * @param enterpriseId 企业id
     * @return 更新结果
     */
    int updateByPrimaryKeySelective(@Param("record") AuthorityRegionDO record, @Param("enterpriseId") String enterpriseId);

    /**
     * 分页查询
     * @param param 查询参数
     * @param enterpriseId 企业id
     * @return 授权区域列表
     */
    Page<AuthorityRegionDO> getAuthorityRegionPage(@Param("record") AuthorityRegionPageRequest param, @Param("enterpriseId") String enterpriseId);

    /**
     * 根据用户id获取授权区域
     * @param userId 用户id
     * @param enterpriseId 企业id
     * @return 授权区域列表
     */
    List<String> getNameByUserId(@Param("userId") String userId, @Param("enterpriseId") String enterpriseId);
}