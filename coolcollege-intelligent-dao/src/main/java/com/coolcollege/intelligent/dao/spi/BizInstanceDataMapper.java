package com.coolcollege.intelligent.dao.spi;

import com.coolcollege.intelligent.model.spi.BizInstanceDataDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/08/11
 */
@Mapper
public interface BizInstanceDataMapper {
    void insertOrUpdate(BizInstanceDataDO bizInstanceData);


    /**
     * 续费实例
     *
     * @param bizInstanceData
     */
    void renewInstance(BizInstanceDataDO bizInstanceData);

    /**
     * 商品升级
     *
     * @param bizInstanceData
     */
    void upgradeInstance(BizInstanceDataDO bizInstanceData);
}
