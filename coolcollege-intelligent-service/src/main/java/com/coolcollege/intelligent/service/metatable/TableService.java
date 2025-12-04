package com.coolcollege.intelligent.service.metatable;

import com.coolcollege.intelligent.model.metatable.request.CheckTableMoveSortRequest;
import com.coolcollege.intelligent.model.metatable.request.MoveSortRequest;
import com.coolcollege.intelligent.model.metatable.request.TbMetaTableRequest;
import com.coolcollege.intelligent.model.metatable.vo.MetaTableTypeVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/6 10:34
 * @Version 1.0
 */
public interface TableService {

    /**
     * 获取检查表分类
     * @return
     * @see com.coolcollege.intelligent.model.metatable.vo.MetaTableTypeVO
     */
    List<MetaTableTypeVO> getMetaTablePropertyList(String enterpriseId, String appType);

    /**
     * 检查表 置顶/取消置顶
     * @param enterpriseId
     * @param tbMetaTableRequest
     * @return
     */
    Boolean tableTop(String enterpriseId, TbMetaTableRequest tbMetaTableRequest);

    /**
     * 检查表 归档/取消归档
     * @param enterpriseId
     * @param tbMetaTableRequest
     * @return
     */
    Boolean pigeonhole(String enterpriseId, TbMetaTableRequest tbMetaTableRequest);


    boolean pigeonholeMany(String enterpriseId, List<Long> id);


    /**
     * 检查表中的项 冻结/取消冻结
     * @param enterpriseId
     * @param tbMetaTableRequest
     * @return
     */
    Boolean columnInCheckTableFreeze(String enterpriseId, TbMetaTableRequest tbMetaTableRequest);



    /**
     * 检查表排序
     * @param enterpriseId
     * @param checkTableMoveSortRequest
     * @return
     */
    Boolean moveSort(String enterpriseId, CheckTableMoveSortRequest  checkTableMoveSortRequest, CurrentUser user);

    /**
     * 检查表排序
     * @param moveSortRequest
     * @return
     */
    Boolean moveSortCheckTable(String enterpriseId,MoveSortRequest moveSortRequest);

}
