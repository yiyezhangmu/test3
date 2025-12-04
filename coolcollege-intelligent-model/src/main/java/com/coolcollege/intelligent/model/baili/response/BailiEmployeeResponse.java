package com.coolcollege.intelligent.model.baili.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/08/05
 */
@NoArgsConstructor
@Data
public class BailiEmployeeResponse {
    private String zoneName;
    private String provinceName;
    private String mangerCity;
    private String bizCity;
    private String costCenter;
    private Object companyName;
    private Integer unitId;
    private String unitName;
    private String unitCode;
    private String unitTypeName;
    private Object storeCode;
    private Integer employeeId;
    private String employeeCode;
    private String employeeName;
    private Integer sex;
    private String jobName;
    private String jobCode;
    private String jobTypeName;
    private String positionName;
    private String positionCode;
    private String blocJobName;
    private String blocPositionName;
    private String uperEmployeeId;
    private String uperEmployeeCode;
    private String uperEmployeeName;
    private String uperPositionName;
    private String ectocystDate;
    private String entryDate;
    private String confirmationTime;
    private Integer regularType;
    private Object phoneNo;
    private String workMail;
    private String belleStarttime;
    private String incumbencySeniority;
    private String socialSeniority;
    private String startWordTtime;
    private String preparationTypeName;
    private String employeePropertyName;
    private String nationality;
    private String birthday;
    private String cardType;
    private Object cardId;
    private Integer employeeAge;
    private Object qualifications;
    private Object school;
    private Object profession;
    private String whetherMarray;
    private Integer whetherBirth;
    private Object bankName;
    private Object bankAccountNum;
    private Integer fromType;
    private String city;
    private Integer oaSecurity;
    private String leaveDate;
    private Integer employeeStatus;
    private Integer leaveType;
    private Integer effectStatus;
    private String userNo;
    private Object password;
    private String updateTime;
    private Object classOfPositions;
    private Object trailEndTime;
    /**
     * zoneName
     * 大区
     * String
     * 否
     *
     * provinceName
     * 省区
     * String
     * 否
     *
     * mangerCity
     * 管理城市
     * String
     * 否
     *
     * bizCity
     * 经营城市
     * String
     * 否
     *
     * costCenter
     * 成本中心
     * String
     * 否
     *
     * companyName
     * 签约公司
     * String
     * 是
     *
     * unitId
     * 组织id
     * Integer
     * 是
     *
     * unitName
     * 组织名称
     * String
     * 是
     *
     * unitCode
     * 组织编码
     * String
     * 是
     *
     * unitTypeName
     * 组织类型
     * String
     * 是
     *
     * storeCode
     * 零售代码
     * String
     * 否
     *
     * employeeId
     * 员工id
     * Integer
     * 是
     *
     * employeeCode
     * 工号
     * String
     * 是
     *
     * employeeName
     * 姓名
     * String
     * 是
     *
     * sex
     * 性别
     * Integer
     * 是
     *
     * jobName
     * 职务
     * String
     * 是
     *
     * jobCode
     * 职务编码
     * String
     * 是
     *
     * jobTypeName
     * 职务类型
     * String
     * 是
     *
     * positionName
     * 岗位
     * String
     * 是
     *
     * positionCode
     * 岗位编码
     * int
     * 是
     *
     * blocJobName
     * 集团职务
     * String
     * 是
     *
     * blocPositionName
     * 集团岗位
     * String
     * 是
     *
     * uperEmployeeId
     * 直接上级id
     * String
     * 否
     *
     * uperEmployeeCode
     * 直接上级code
     * String
     * 否
     *
     * uperEmployeeName
     * 直接上级
     * String
     * 否
     *
     * uperPositionName
     * 直接上级岗位
     * String
     * 否
     *
     * ectocystDate
     * 工龄起算日
     * date
     * 是
     *
     * entryDate
     * 入职日期
     * date
     * 是
     *
     * confirmationTime
     * 转正日期
     * date
     * 否
     *
     * regularType
     * 转正类型
     * int
     * 否
     *
     * phoneNo
     * 移动电话
     * String
     * 是
     *
     * workMail
     * 工作邮箱
     * String
     * 是
     *
     * belleStarttime
     * 百丽工龄起算日
     * date
     * 否
     *
     * incumbencySeniority
     * 百丽在职工龄
     * String
     * 否
     *
     * socialSeniority
     * 社会工龄
     * String
     * 否
     *
     * startWordTtime
     * 从事本岗日期
     * date
     * 否
     *
     * preparationTypeName
     * 编制类型
     * String
     * 否
     *
     * employeePropertyName
     * 员工性质
     * String
     * 否
     *
     * nationality
     * 国籍
     * String
     * 是
     *
     * birthday
     * 出生日期
     * date
     * 是
     *
     * cardType
     * 证件类型
     * String
     * 否
     *
     * cardId
     * 证件号码
     * String
     * 否
     *
     * employeeAge
     * 年龄
     * int
     * 否
     *
     * qualifications
     * 学历
     * int
     * 否
     *
     * school
     * 学校
     * String
     * 否
     *
     * profession
     * 专业
     * String
     * 否
     *
     * whetherMarray
     * 婚姻状况
     * int
     * 否
     *
     * whetherBirth
     * 是否生育
     * int
     * 否
     *
     * bankName
     * 工资卡银行
     * String
     * 否
     *
     * bankAccountNum
     * 工资卡号
     * String
     * 否
     *
     * fromType
     * 来源类型
     * int
     * 是
     *
     * city
     * 工作地点
     * String
     * 否
     *
     * oaSecurity
     * OA安全级别
     * int
     * 否
     *
     * leaveDate
     * 离职日期
     * date
     * 否
     *
     * employeeStatus
     * 在职状态
     * int
     * 是
     *
     * leaveType
     * 离职类型
     * int
     * 否
     *
     * effectStatus
     * 生效状态
     * int
     * 是
     *
     * userNo
     * 登录账户
     * String
     * 是
     *
     * password
     * 密码
     * String
     * 是
     */
}
