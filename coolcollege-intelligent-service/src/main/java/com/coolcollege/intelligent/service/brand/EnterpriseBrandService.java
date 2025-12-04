package com.coolcollege.intelligent.service.brand;

import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandQueryRequest;
import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandUpdateRequest;
import com.coolcollege.intelligent.model.brand.vo.EnterpriseBrandVO;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 品牌
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
public interface EnterpriseBrandService {

    /**
     * 新增
     * @param enterpriseId 企业id
     * @param request 品牌更新请求类
     * @return id
     */
    Long insert(String enterpriseId, EnterpriseBrandUpdateRequest request);

    /**
     * 修改
     * @param enterpriseId 企业id
     * @param request 品牌更新请求类
     * @return id
     */
    Long update(String enterpriseId, EnterpriseBrandUpdateRequest request);

    /**
     * 批量删除
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return 是否成功
     */
    boolean removeBatch(String enterpriseId, List<Long> ids);

    /**
     * 根据id查询VO对象
     * @param enterpriseId 企业id
     * @param id id
     * @return 品牌VO
     */
    EnterpriseBrandVO getVOById(String enterpriseId, Long id);

    /**
     * 列表查询
     * @param enterpriseId 企业id
     * @param request 品牌查询request
     * @return 品牌VO列表
     */
    List<EnterpriseBrandVO> getVOList(String enterpriseId, EnterpriseBrandQueryRequest request);

    /**
     * 分页查询
     * @param enterpriseId 企业id
     * @param request 品牌查询request
     * @return 分页对象
     */
    PageInfo<EnterpriseBrandVO> getVOPage(String enterpriseId, EnterpriseBrandQueryRequest request);

    /**
     * 根据id获取品牌名称映射
     * @param enterpriseId 企业id
     * @param ids id列表
     * @return 名称映射
     */
    Map<Long, String> getNameMapByIds(String enterpriseId, List<Long> ids);
}
