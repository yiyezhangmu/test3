package com.coolcollege.intelligent.common.enums.store;

public enum DingSyncType {
    /**
     * 同步部门下面的管理员或者子管理员
     */
    ADMINUSER("adminUser"),
    /**
     * 门店下特殊岗位的用户
     */
    SPECIALPOSITION("specialPosition"),
    /**
     * 门店下的所有用户
     */
    ALLUSER("allUser"),

    /**
     * 门店下拥有主管权限的用户
     */
    CHARGE("charge"),

    /**
     * 门店拥有XX角色的用户
     */
    ROLE("role"),
    /**
     * 门店父节点下的所有用户
     */
    PARENTUSER("parentUser"),
    /**
     * 上级节点下的管理员
     */
    PARENTADMIN("parentAdmin"),
    /**
     * 上级节点下拥有特殊岗位的用户
     */
    PARENTSPECIALPOSITION("parentSpecialPosition"),

    /**
     * 门店上一级拥有主管权限的用户
     */
    PARENT_CHARGE("parentCharge"),
    /**
     *  门店上一级拥有XX角色的用户
     */
    PARENT_ROLE("parentRole"),
    ;
    private String value;

    DingSyncType( String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }

}
