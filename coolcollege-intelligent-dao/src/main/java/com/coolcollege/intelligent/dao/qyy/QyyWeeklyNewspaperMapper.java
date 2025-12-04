package com.coolcollege.intelligent.dao.qyy;

import com.coolcollege.intelligent.model.achievement.qyy.dto.StoreNewsPaperDTO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.QyyNewspaperExportVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperDetailVO;
import com.coolcollege.intelligent.model.achievement.qyy.vo.WeeklyNewspaperPageVO;
import com.coolcollege.intelligent.model.qyy.QyyWeeklyNewspaperDO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchenbiao
 * @date 2023-04-12 03:46
 */
public interface QyyWeeklyNewspaperMapper {
    /**
     *
     * 默认插入方法，只会给有值的字段赋值
	 * 会对传进来的字段做判空处理，如果字段为空，则使用数据库默认字段或者null
     * dateTime:2023-04-12 03:46
     */
    int insertSelective(@Param("record") QyyWeeklyNewspaperDO record,
                        @Param("enterpriseId") String enterpriseId);

    /**
     *
     * 默认更新方法，根据主键更新，不会把null值更新到数据库，避免覆盖之前有值的
     * dateTime:2023-04-12 03:46
     */
    int updateByPrimaryKeySelective(@Param("record") QyyWeeklyNewspaperDO record, @Param("enterpriseId") String enterpriseId);


    /**
     * 获取周报分页
     * @param enterpriseId
     * @param mondayOfWeek
     * @param userId
     * @return
     */
    Page<QyyWeeklyNewspaperDO> getWeeklyNewspaperPage(@Param("enterpriseId") String enterpriseId,
                                                      @Param("mondayOfWeeks") List<String> mondayOfWeeks,
                                                      @Param("userId") String userId,
                                                      @Param("conversationId")String conversationId,
                                                      @Param("regionId") List<String> regionId,
                                                      @Param("storeId") List<String> storeId,
                                                      @Param("pageNum") Integer pageNum,
                                                      @Param("pageSize") Integer pageSize);


    /**
     * 获取周报详情
     * @param enterpriseId
     * @param id
     * @return
     */
    QyyWeeklyNewspaperDO getWeeklyNewspaperDetail(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);
    QyyWeeklyNewspaperDO getWeeklyNewspaperDetailByUp(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);
    QyyWeeklyNewspaperDO getWeeklyNewspaperDetailByDown(@Param("enterpriseId") String enterpriseId, @Param("id") Long id);

    WeeklyNewspaperDetailVO getWeeklyNewspaper(@Param("enterpriseId")String enterpriseId,
                                               @Param("mondayOfWeek")String mondayOfWeek,
                                               @Param("userId")String userId,
                                               @Param("storeId")String storeId);

    boolean deleteWeeklyNewspaper(@Param("enterpriseId") String enterpriseId,
                                  @Param("id") Long id);

    List<QyyWeeklyNewspaperDO> storeWeeklyNewsPaperByPage(@Param("enterpriseId") String enterpriseId,
                                                          @Param("paperDTO") StoreNewsPaperDTO paperDTO,
                                                          @Param("storeId")List<String> storeId,
                                                          @Param("regionId") List<String> regionId);

    Integer queryNum(@Param("enterpriseId") String enterpriseId);

    PageInfo<QyyNewspaperExportVO> getWeeklyNewsPaperAll(@Param("enterpriseId") String enterpriseId,
                                                         @Param("pageNum") int pageNum,
                                                         @Param("pageSize") int pageSize);

    List<String> getStoreIdByWeeklyNewspaper(@Param("enterpriseId") String enterpriseId,
                                             @Param("monday") LocalDate monday);

    QyyWeeklyNewspaperDO selectById(@Param("enterpriseId") String enterpriseId,
                                    @Param("businessId") Long businessId);

    List<QyyWeeklyNewspaperDO> storeWeeklyNewsPaperByPageNoParam(@Param("enterpriseId")String enterpriseId);

    Long countTotalPaper(@Param("enterpriseId") String enterpriseId);

    List<QyyWeeklyNewspaperDO> getWeeklyNewspaperList(@Param("enterpriseId") String eId,
                                                       @Param("createDate") String createDate);
}