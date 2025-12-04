package com.coolcollege.intelligent.util.patrolStore;
import com.coolcollege.intelligent.common.enums.meta.MetaTablePropertyEnum;
import org.apache.commons.lang3.StringUtils;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.TableTypeConstant.STANDARD;

/**
 * @Author suzhuhong
 * @Date 2022/4/18 9:50
 * @Version 1.0
 */
public class TableTypeUtil {

    /**
     * 是否是自定义表
     * @return
     */
    public static Boolean isUserDefinedTable(Integer tableProperty, String tableType){
        if (StringUtils.isBlank(tableType)||tableProperty==null){
            return Boolean.FALSE;
        }
        if (STANDARD.equals(tableType)&& MetaTablePropertyEnum.USER_DEFINED_TABLE.getCode().equals(tableProperty)){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
