package com.coolcollege.intelligent.model.store.queryDto;

import com.coolcollege.intelligent.model.region.dto.BaseAuthRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * describe:附近的门店请求参数
 *
 * @author zhouyiping
 * @date 2021/08/02
 */
@Data
public class NearbyStoreRequest extends BaseAuthRequest {

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
     * 定位距离
     */
    private Double queryDistance=5D;

    private String storeName;

    private List<String> storeStatusList;


}
