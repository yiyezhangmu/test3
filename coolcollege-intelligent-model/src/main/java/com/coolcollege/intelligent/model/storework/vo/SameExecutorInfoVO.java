package com.coolcollege.intelligent.model.storework.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2022/10/17 17:28
 * @Version 1.0
 */
@Data
public class SameExecutorInfoVO {

    private Date beginHandleTime;

    private Date endHanleTime;

    private HandlerUserVO handlerUserVO;

}
