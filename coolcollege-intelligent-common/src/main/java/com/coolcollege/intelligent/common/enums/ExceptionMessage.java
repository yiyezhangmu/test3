package com.coolcollege.intelligent.common.enums; ;

public enum ExceptionMessage {
    /**
     * 请传递角色id
     */
    ROLEID_MISS(1000,"请传递角色id"),
    /**
     * 请传递角色名称
     */
    RELENAME_MISS(1001,"请传递角色名称"),
    /**
     * 角色正在使用中,不允许删除
     */
    ROLE_BY_PERSON_USE(1002,"角色正在使用中,不允许删除"),
    /**
     *角色添加失败
     */
    ROLE_ADD_FAILED(1003,"角色添加失败"),
    /**
     *角色删除失败
     */
    ROLE_DEL_FAILED(1004,"角色删除失败"),
    /**
     *权限删除失败
     */
    MENU_DEL_FAILED(1005,"权限删除失败"),
    /**
     *角色和权限解除失败
     */
    ROLE_DEL_MENU_FAILED(1005,"角色和权限解除失败"),
    /**
     *请传递用户id
     */
    USER_MISS(1006,"请传递用户id"),
    /**
     *请传递门店id
     */
    STORE_MISS(1007,"请传递门店id"),
    /**
     *用户下的门店查询失败
     */
    STORES_USER(1008,"用户下的门店查询失败"),
    /**
     *用户添加门店失败
     */
    STORES_To_USER(1008,"用户添加门店失败"),
    /**
     *给角色添加用户失败
     */
    USERS_TO_ROLE(1009,"给角色添加用户失败"),

    /**
     * 钉钉角色不允许删除
     */
    DING_ROLE(1010,"钉钉角色不允许删除");

    /**
     * 错误編碼
     */
    private Integer code;
    /**
     * 错误信息
     */
    private String message;

     ExceptionMessage(Integer  code, String message){
        this.code=code;
        this.message=message;
    }

    public Integer getCode() {
        return code;
    }


    public String getMessage() {
        return message;
    }

}
