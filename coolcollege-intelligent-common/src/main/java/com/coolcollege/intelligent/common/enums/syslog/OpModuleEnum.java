package com.coolcollege.intelligent.common.enums.syslog;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * describe: 功能模块
 *
 * @author wangff
 * @date 2025/1/20
 */
@Getter
@AllArgsConstructor
public enum OpModuleEnum {

    /**
     * 菜单
     */
    DEFAULT(null),

    LOGIN("登录"),

    STORE_FILE("门店-门店列表-门店档案"),

    STORE_GROUP("门店-门店列表-门店分组"),

    STORE_WORK("店务-门店日/周/月清"),

    SW_PICTURE_CENTER("店务-图片库"),

    SOP_FILE("巡店-巡店SOP-运营手册"),

    CHECK_TABLE("巡店-巡店SOP-SOP检查表"),

    SOP_COLUMN("巡店-巡店SOP-SOP检查项"),

    ENTERPRISE_USER("设置-组织架构-成员管理"),
    // ....

	SOP_ARCHIVES("巡店-巡店SOP-档案库"),

	SETTING_DEVICE_LIST("设置-设备管理-设备列表"),

	UNIFY_TASK("任务"),

	QUESTION("工单-工单管理"),

	PATROL_STORE_FORM("巡店-表单巡店"),

	SETTING_REGION_STORE("设置-组织架构-区域门店"),

	SETTING_POSITION("设置-职位管理"),

	SETTING_DEVICE_INTEGRATION("设置-设备管理-设备集成"),

    AI_ANALYSIS_RULE("AI分析规则"),

    // 导入导出单独作为一个模块
    IMPORT_EXPORT(null),

    ;

    private final String menus;

    /**
     * 筛选过滤集合
     */
    private static final Set<OpModuleEnum> fillterModuleSet = new HashSet<>(Lists.newArrayList(
            STORE_WORK
    ));
    /**
     * 特殊处理的模块
     */
    private static final Set<String> extraModuleSet = new HashSet<>(Lists.newArrayList(
            "日清",
            "周清",
            "月清"
    ));

    public static List<String> allModule() {
        List<String> result = Arrays.stream(values())
                .filter(v -> StringUtils.isNotBlank(v.getMenus()) && !fillterModuleSet.contains(v))
                .map(v -> {
                    String[] menus = v.getMenus().split("-");
                    return menus[menus.length - 1];
                })
                .collect(Collectors.toList());
        result.addAll(extraModuleSet);
        return result;
    }

}
