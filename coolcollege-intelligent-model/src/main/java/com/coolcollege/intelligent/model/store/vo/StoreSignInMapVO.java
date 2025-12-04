package com.coolcollege.intelligent.model.store.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author 邵凌志
 * @date 2020/10/16 11:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class StoreSignInMapVO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 是否是全门店权限
     */
    private Boolean isAllStore;

    /**
     * 门店id列表
     */
    private List<String> storeIds;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 当前人定位经度
     */
    @NotBlank(message = "经度不能为空")
    private String longitude;

    /**
     * 当前人定位维度
     */
    @NotBlank(message = "纬度不能为空")
    private String latitude;

    /**
     * 相似经度集合
     */
    private List<String> lngList;

    /**
     * 相似维度集合
     */
    private List<String> latList;
    /**
     * 巡店计划子任务id
     */
    private Long subTaskId;

    /**
     * 门店状态 open：营业；closed：闭店；not_open：未开业
     */
    private List<String> storeStatusList;
}
