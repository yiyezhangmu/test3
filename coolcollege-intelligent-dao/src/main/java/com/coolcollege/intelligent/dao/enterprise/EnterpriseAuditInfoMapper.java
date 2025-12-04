package com.coolcollege.intelligent.dao.enterprise;

import com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseAuditVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 企业审核表
 * 
 * @author xugangkun
 * @email 670809626@qq.com
 * @date 2021-07-19 16:27:52
 */
@Mapper
public interface EnterpriseAuditInfoMapper {
    /**
     * 主键查询
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    EnterpriseAuditInfoDO selectById(@Param("id") Long id);

    /**
     * 企业审核列表
     * @param enterpriseName 企业名称
     * @param auditStatus 审核状态
     * @author: xugangkun
     * @date: 2021/7/20 10:15
     */
    List<EnterpriseAuditVO> enterpriseAuditList(@Param("enterpriseName")String enterpriseName, @Param("auditStatus")Integer auditStatus);

    /**
     * 记录数量统计
     * @return: int
     * @Author: xugangkun
     */
    int count();

    /**
     * 保存
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    void save(@Param("entity") EnterpriseAuditInfoDO entity);

    /**
     * 根据主键更新
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void updateById(@Param("entity") EnterpriseAuditInfoDO entity);

    /**
     * 审核企业
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    void auditEnterprise(@Param("entity") EnterpriseAuditInfoDO entity);
    /**
     * 根据主键删除
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    void deleteById(Long id);
    /**
     * 根据主键批量删除
     * @Param:
     * @param ids id列表
     * @return: void
     * @Author: xugangkun
     */
    void deleteBatchByIds(List<Long> ids);

}
