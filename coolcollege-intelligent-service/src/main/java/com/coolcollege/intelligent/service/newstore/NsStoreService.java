package com.coolcollege.intelligent.service.newstore;

import com.coolcollege.intelligent.model.export.request.NsStoreExportStatisticsRequest;
import com.coolcollege.intelligent.model.newstore.request.NsBatchHandoverRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreGetStatisticsRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreAddOrUpdateRequest;
import com.coolcollege.intelligent.model.newstore.request.NsStoreListRequest;
import com.coolcollege.intelligent.model.newstore.vo.NsHandoverHistoryVO;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreGetStatisticsVO;
import com.coolcollege.intelligent.model.newstore.vo.NsStoreVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author zhangnan
 * @description: 新店管理Service
 * @date 2022/3/6 9:53 PM
 */
public interface NsStoreService {
    /**
     * 批量转交
     * @param enterpriseId 企业id
     * @param request NsBatchHandOverRequest
     * @param user CurrentUser
     */
    void batchHandOver(String enterpriseId, NsBatchHandoverRequest request, CurrentUser user);

    /**
     * 获取新店交接记录
     * @param enterpriseId 企业id
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return PageInfo<NsHandoverHistoryVO>
     */
    PageInfo<NsHandoverHistoryVO> getHandOverHistoryList(String enterpriseId, Integer pageNum, Integer pageSize);

    /**
     * 查询新店分析表
     * @param enterpriseId 企业id
     * @param request NsStoreGetStatisticsRequest
     * @return List<NsStoreGetStatisticsVO>
     */
    List<NsStoreGetStatisticsVO> getStatistics(String enterpriseId, NsStoreGetStatisticsRequest request);

    PageInfo<NsStoreVO> getNsStoreList(String enterpriseId, NsStoreListRequest request);
    /**
     * 新店 新增
     * @param enterpriseId
     * @param nsStoreAddOrUpdateRequest
     * @return
     */
    Long addNsStore(String enterpriseId, NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest, CurrentUser user);
    /**
     * 新店编辑
     * @param enterpriseId
     * @param nsStoreAddOrUpdateRequest
     * @return
     */
    Boolean updateNsStore(String enterpriseId, NsStoreAddOrUpdateRequest nsStoreAddOrUpdateRequest, CurrentUser user);

    /**
     * 新店删除
     * @param enterpriseId
     * @param id
     */
    void deleteNsStoreById(String enterpriseId, Long id);

    /**
     * 新店详情
     * @param enterpriseId
     * @param id
     * @return
     */
    NsStoreVO getNsStoreDetailById(String enterpriseId, Long id);

    /**
     * 查询新店数量
     * @param enterpriseId 企业id
     * @param request NsStoreExportStatisticsRequest
     * @return
     */
    Long getNsStoreCount(String enterpriseId, NsStoreExportStatisticsRequest request);

}
