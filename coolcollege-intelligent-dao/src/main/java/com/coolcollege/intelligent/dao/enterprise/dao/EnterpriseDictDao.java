package com.coolcollege.intelligent.dao.enterprise.dao;

import com.coolcollege.intelligent.dao.enterprise.EnterpriseDictMapper;
import com.coolcollege.intelligent.model.enterprise.EnterpriseDictDO;
import com.coolcollege.intelligent.model.user.vo.UserPersonnelStatusVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author ：xugangkun
 * @description：TODO
 * @date ：2022/3/2 10:43
 */
@Service
public class EnterpriseDictDao {

    @Resource
    private EnterpriseDictMapper enterpriseDictMapper;

    /**
     * 主键查询
     * @param id
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    public EnterpriseDictDO selectById(String eid, Long id) {
        return enterpriseDictMapper.selectById(eid, id);
    }

    /**
     * 根据业务类型和业务详情查询
     * @param eid
     * @param businessType
     * @param businessValue
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    public EnterpriseDictDO selectByTypeAndValue(String eid, String businessType, String businessValue) {
        return enterpriseDictMapper.selectByTypeAndValue(eid, businessType, businessValue);
    }

    /**
     * 获得所有状态
     * @param eid
     * @return: com.coolcollege.intelligent.model.enterprise.EnterpriseCustomizeMenuDO
     * @Author: xugangkun
     * @Date: 2021/3/20 14:32
     */
    public List<UserPersonnelStatusVO> selectAllByType(String eid, String businessType) {
        return enterpriseDictMapper.selectAllByType(eid, businessType);
    }

    /**
     * 保存
     * @param entity
     * @return: void
     * @Author: xugangkun
     */
    public void save(String eid, EnterpriseDictDO entity) {
        enterpriseDictMapper.save(eid, entity);
    }

    /**
     * 根据主键更新
     * @param entity 实体
     * @return: void
     * @Author: xugangkun
     */
    public void updateById(String eid, EnterpriseDictDO entity) {
        enterpriseDictMapper.updateById(eid, entity);
    }
    /**
     * 根据主键删除
     * @param id
     * @return: void
     * @Author: xugangkun
     */
    public void deleteById(String eid, Long id) {
        enterpriseDictMapper.deleteById(eid, id);
    }
    /**
     * 根据主键批量删除
     * @param ids id列表
     * @return: void
     * @Author: xugangkun
     */
    public void deleteBatchByIds(String eid, List<Long> ids) {
        enterpriseDictMapper.deleteBatchByIds(eid, ids);
    }

    public List<EnterpriseDictDO> selectByType(String eid, String businessType) {
        return enterpriseDictMapper.selectByType(eid, businessType);
    }

}
