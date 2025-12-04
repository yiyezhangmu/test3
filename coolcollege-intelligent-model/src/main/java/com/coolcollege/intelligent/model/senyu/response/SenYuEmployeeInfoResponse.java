package com.coolcollege.intelligent.model.senyu.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工信息
 */
@NoArgsConstructor
@Data
public class SenYuEmployeeInfoResponse {

    private String kehbm;//员工编码
    private String kehmc ;//员工名称
    private String shux;//岗位编码
    private String shuxms;//岗位描述
    private String diygdhh ;//手机号码
    private String yingyzzbm;//身份证号码
    private String yewhbdkhh1;//上级编码
    private String zhongxjzdj;//冻结标识
    private String kehzhz;//促销员标识
    private String shangjsfz;//上级身份证
}
