package com.coolcollege.intelligent.service.metatable;

import com.coolcollege.intelligent.model.metatable.request.ColumnCategoryRequest;
import com.coolcollege.intelligent.model.metatable.vo.TbMetaColumnCategoryVO;

import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/4/2 10:53
 * @Version 1.0
 */
public interface ColumnCategoryService {


    /**
     * 检查项分类列表
     * @param enterpriseId
     * @param categoryName
     * @return
     */
    List<TbMetaColumnCategoryVO> getMetaColumnCategoryList(String enterpriseId,String categoryName);

    /**
     * 新增分类
     * @param enterpriseId
     * @param param
     * @return
     */
    Long addMetaColumnCategory(String enterpriseId, ColumnCategoryRequest param);

    /**
     * 编辑分类
     * @param enterpriseId
     * @param param
     * @return
     */
    Boolean updateMetaColumnCategory(String enterpriseId, ColumnCategoryRequest param);

    /**
     * 删除分类
     * @param enterpriseId
     * @param id
     * @return
     */
    Boolean deletedMetaColumnCategory(String enterpriseId, Long id);

    /**
     * 项分类排序
     * @param enterpriseId
     * @param requestList
     * @return
     */
    Boolean metaColumnCategorySort(String enterpriseId, List<Long> ids);
}
