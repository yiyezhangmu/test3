package com.coolcollege.intelligent.common.util;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DOConvert {
    public static <DO, VO> PageInfo<VO> DO2VO(PageInfo<DO> pageInfoPo, Function<DO,VO> convert) {
        // 创建Page对象，实际上是一个ArrayList类型的集合
        Page<VO> page = new Page<>(pageInfoPo.getPageNum(), pageInfoPo.getPageSize());
        page.setTotal(pageInfoPo.getTotal());
        PageInfo<VO> voPageInfo = new PageInfo<>(page);
        if(pageInfoPo.getTotal()>0){
            List<VO> VOS = pageInfoPo.getList().stream().map(
                    DO -> convert.apply(DO)
            ).collect(Collectors.toList());
            voPageInfo.getList().addAll(VOS);
        }
        return  voPageInfo;
    }
}
