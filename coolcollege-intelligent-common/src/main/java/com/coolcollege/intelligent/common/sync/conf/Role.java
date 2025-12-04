package com.coolcollege.intelligent.common.sync.conf;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Role {

    /**
     * 管理员
     */
    MASTER("20000000", "管理员", 1,"master"),
    /**
     * 子管理员
     */
    SUB_MASTER("80000000", "子管理员", 2,"sub_master"),
    /**
     * 普通员工
     */
    EMPLOYEE("30000000", "未分配", 99999999,"employee"),
    /**
     * 部门负责人
     */
    DEPT_LEADER("40000000", "部门负责人", 10,"dept_leader"),
    /**
     * 店长
     */
    SHOPOWNER("50000000", "店长", 3,"shopowner"),
    /**
     * 运营
     */
    OPERATOR("60000000", "运营", 4,"operator"),
    /**
     * 店员
     */
    CLERK("70000000", "店员", 5,"clerk"),

    MYSTERIOUS_GUEST("90000000", "神秘访客", 6,"mysterious_guest");


    private static final Map<String, Role> MAP = Arrays.stream(values()).collect(
            Collectors.toMap(Role::getRoleEnum, Function.identity()));

    private static final Map<String, Role> ROLE_ID_MAP = Arrays.stream(values()).collect(
            Collectors.toMap(Role::getId, Function.identity()));

    private String id;
    private String name;
    @Getter
    @Setter
    private Integer priority;

    private String roleEnum;


    Role(String id, String name, Integer priority,String roleEnum) {
        this.id = id;
        this.name = name;
        this.priority = priority;
        this.roleEnum=roleEnum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoleEnum() {
        return roleEnum;
    }

    public static Role getByCode(String code) {
        return MAP.get(code);
    }

    /**
     * 是否是管理员 及 子管理员
     * @param code
     * @return
     */
    public static boolean isAdmin(String code){
        if(MASTER.getRoleEnum().equals(code) || SUB_MASTER.getRoleEnum().equals(code)){
            return true;
        }
        return false;
    }

    /**
     * 根据id 判断 是否是管理员 及 子管理员
     * @param id
     * @return
     */
    public static boolean isAdminById(String id){
        if(MASTER.getId().equals(id) || SUB_MASTER.getId().equals(id)){
            return true;
        }
        return false;
    }

    /**
     * 是否包含角色id
     * @param roleId
     * @return
     */
    public static boolean isContainsRoleId(String roleId){
        return ROLE_ID_MAP.containsKey(roleId);
    }

}
