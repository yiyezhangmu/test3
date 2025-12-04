package com.coolcollege.intelligent.facade.dto.openApi.achieve.qyy;

import com.coolcollege.intelligent.common.util.DateUtil;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangchenbiao
 * @FileName: BigOrderBoardDTO
 * @Description:大单播报
 * @date 2023-03-30 16:14
 */
@Data
public class BigOrderBoardDTO {

    /**
     * 组织id
     */
    private String dingDeptId;

    /**
     * 组织名称
     */
    private String deptName;

    /**
     * 组织类型 STORE、COMP、HQ
     */
    private String deptType;

    /**
     * 推送时间
     */
    private Long etlTm;

    /**
     * 大单用户列表
     */
    private List<BigOrderBoard> topUserList;


    @Data
    public static class BigOrderBoard{

        /**
         * 门店id
         */
        private Long storeId;

        /**
         * 门店名称
         */
        private String storeName;

        /**
         * 公司id
         */
        private Long compId;

        /**
         * 公司名称
         */
        private String compName;

        /**
         * 用户id
         */
        private String userId;

        /**
         * 用户名称
         */
        private String userName;

        /**
         * 用户头像
         */
        private String userImage;

        /**
         * 大单金额
         */
        private BigDecimal salesAmt;

        /**
         * 订单时间
         */
        private Date salesTm;

    }

}
