package com.coolcollege.intelligent.model.senyu.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 门店信息
 */
@NoArgsConstructor
@Data
public class SenYuStoreResponse {

    private String kehbm;//客户编码
    private String kehmc ;//客户名称
    private String yewhbdkhh1;//员工编码
    private String shux;//岗位编码
    private String shuxms;//岗位描述
    private String kehhgysdcyf;//渠道
    private String xiaosbm;//销售部编码
    private String miaos;//销售部描述
    private String zhongxjzdj;//冻结标识
    private String yingyzzbm;//身份证号
    private String liuxmc;//流向名

}
