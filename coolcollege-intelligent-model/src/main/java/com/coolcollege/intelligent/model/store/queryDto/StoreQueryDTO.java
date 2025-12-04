package com.coolcollege.intelligent.model.store.queryDto;

import com.coolcollege.intelligent.model.page.PageBase;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @ClassName StoreQueryDTO
 * @Description 用一句话描述什么
 */
@Data
public class StoreQueryDTO extends PageBase {
    private String keyword;
    private String store_area;
    private String store_name;
    private String operator_name;
    private Long start_time;
    private Long end_time;
    private String is_lock;
    private String is_delete = "effective";
    private String department_id;
    private Boolean only_see_me;
    private String longitude_latitude;
    private String user_id;
    private Boolean is_admin;
    // 仅用于查询是否可以B1巡店
    private Boolean can_sign_in;
    private Integer distance;
    private String store_address;
    /**
     * 经度
     */
    private String  longitude;
    /**
     * 维度
     */
    private String  latitude;

    private List<String> storeStatusList;
    //距离范围
    private Long  range;
    /**
     * 是否收藏
     */
    private Boolean isCollect;
    /**
     * 查看区域门店id
     */
    private List<String> regionIds;
    /**
     * 查询类型（精确查询，递归查询）
     */
    private String regionId;
    /**
     * 门店来源
     */
    private String source;
    /**
     * 查询类型（true:表示根据用户id筛选）
     */
    private Boolean type;
    /**
     * 是否递归查询
     */
    private Boolean recursion = false;
    /**
     * 筛选的门店id列表
     */
    private List<String> storeIds;
    /**
     * 筛选门店是否忽略
     */
    private Integer  isValid = 1;
    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 门店分组
     */
    private String storeGroupId;


    private  List<String> regionPathList;


    /**
     * 排序列
     */
    private String orderBy;

    /**
     * 升序还是降序 asc 升序  desc降序
     */
    private String orderRule = "asc";

    public Long getEnd_time() {
        if (Objects.nonNull(end_time)) {
            return end_time + 86400000;
        }
        return end_time;
    }

    public void setEnd_time(Long end_time) {
        this.end_time = end_time;
    }
}
