package com.coolcollege.intelligent.model.enterprise.request;

import lombok.Data;

import java.util.List;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/01/26
 */
@Data
public class AppMenuCustomizeRequest {
    List<AppMenuCustomizeInfo> menuList;
}
