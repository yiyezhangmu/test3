package com.coolcollege.intelligent.model.operationboard.dto;

import com.coolcollege.intelligent.model.system.SysRoleDO;
import lombok.Data;
import lombok.ToString;

import java.text.NumberFormat;
import java.util.List;

/**
 * @author shuchang.wei
 * @date 2021/1/8 17:46
 */
@Data
@ToString
public class UserDetailStatisticsDTO {
    //管理门店数
    private Integer manageStoreNum;
    //创建问题数
    private Integer createQuestionNum;
    //门店覆盖率
    private String storeCoverPercent;
    //巡店次数
    private Integer patrolNum;
    //解决问题数
    private Integer finishQuestionNum;
    //人员id
    private String userId;

    //人员名称
    private String userName;

    //巡店数量
    private Integer patrolStoreNum;

    private List<SysRoleDO> defaultRoleList;

    //所属部门
    private String departmentName;

    //职位名称
    private String roleName;

    public String getStoreCoverPercent(){
        if(manageStoreNum <= 0){
            return "-";
        }
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format((patrolStoreNum*1d)/manageStoreNum);
    }

}
