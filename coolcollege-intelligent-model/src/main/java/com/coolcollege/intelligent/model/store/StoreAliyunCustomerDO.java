package com.coolcollege.intelligent.model.store;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author 邵凌志
*/
@Data
public class StoreAliyunCustomerDO {

    /**
    * 主键
    */
    private Long id;

    /**
     * 门店id
     */
    private String storeId;

    /**
    * 阿里云人脸图像id
    */
    private String faceId;

    /**
    * 阿里云人脸图像地址
    */
    private String picUrl;

    /**
    * 顾客姓名
    */
    private String name;

    /**
     * 首次进店时间
     */
    private Long firstAppearTime;

    /**
    * 是否vip，0：否，1：是
    */
    private Integer isVip;

    /**
    * 顾客年龄
    */
    private Integer age;

    /**
    * 顾客电话
    */
    private String phone;

    /**
    * 顾客生日
    */
    private String birthday;

    /**
    * 创建时间
    */
    private Long createTime;

    /**
    * 创建人
    */
    private String createName;

    /**
    * 修改时间
    * isNullAble:1
    */
    private Long updateTime;

    /**
    * 修改人
    * isNullAble:1
    */
    private String updateName;
}
