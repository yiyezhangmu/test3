package com.coolcollege.intelligent.model.store;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 
 * @author   zhangchenbiao
 * @date   2025-09-29 05:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeoAddressInfoDO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;

    @ApiModelProperty("经纬度")
    private String longitudeLatitude;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("城市名称")
    private String city;

    @ApiModelProperty("城市编码")
    private String cityCode;

    @ApiModelProperty("所在区/县")
    private String county;

    @ApiModelProperty("行政区编码")
    private String adcode;

    @ApiModelProperty("坐标点所在乡镇/街道")
    private String township;

    @ApiModelProperty("乡镇街道编码")
    private String towncode;

    @ApiModelProperty("详细地址")
    private String formattedAddress;

    @ApiModelProperty("其他信息 街道商圈")
    private String otherInfo;


    public static GeoAddressInfoDO convert(JSONObject geocodes){
        GeoAddressInfoDO result = new GeoAddressInfoDO();
        String formattedAddress = geocodes.getString("formatted_address");
        result.setFormattedAddress(formattedAddress);
        JSONObject addressComponent = geocodes.getJSONObject("addressComponent");
        if(Objects.isNull(addressComponent)){
            return result;
        }
        String province = addressComponent.getString("province");
        String city = addressComponent.getString("city");
        String district = addressComponent.getString("district");
        if(StringUtils.isBlank(city) || city.equals("[]")){
            city = province.contains("市") ? province : district;
        }
        result.setProvince(province);
        result.setCity(city);
        result.setCityCode(addressComponent.getString("citycode"));
        result.setCounty(district);
        result.setAdcode(addressComponent.getString("adcode"));
        result.setTownship(addressComponent.getString("township"));
        result.setTowncode(addressComponent.getString("towncode"));
        JSONObject otherInfo = new JSONObject();
        JSONObject streetNumber = addressComponent.getJSONObject("streetNumber");
        if(Objects.nonNull(streetNumber)){
            streetNumber.remove("location");
            streetNumber.remove("direction");
            streetNumber.remove("distance");
            otherInfo.put("streetNumber", streetNumber);
        }
        JSONArray businessAreas = addressComponent.getJSONArray("businessAreas");
        if(Objects.nonNull(businessAreas) && !businessAreas.isEmpty()){
            List<String> businessAreasName = businessAreas.stream().filter(o ->o instanceof JSONObject).map(s -> (JSONObject) s).map(s -> s.getString("name")).collect(Collectors.toList());
            otherInfo.put("businessAreas", businessAreasName);
        }
        result.setOtherInfo(JSONObject.toJSONString(otherInfo));
        return result;
    }
}