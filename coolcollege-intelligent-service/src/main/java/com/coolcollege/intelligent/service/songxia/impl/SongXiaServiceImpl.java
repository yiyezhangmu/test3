package com.coolcollege.intelligent.service.songxia.impl;

import com.coolcollege.intelligent.dao.songxia.SongXiaMapper;
import com.coolcollege.intelligent.facade.dto.PageDTO;
import com.coolcollege.intelligent.facade.dto.openApi.SongXiaDTO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSalesInfoVO;
import com.coolcollege.intelligent.facade.dto.openApi.vo.SongXiaSampleInfoVO;
import com.coolcollege.intelligent.facade.request.PageRequest;
import com.coolcollege.intelligent.service.songxia.SongXiaService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.List;

@Service
public class SongXiaServiceImpl implements SongXiaService {
    @Resource
    private SongXiaMapper songXiaMapper;

    @Override
    public PageDTO<SongXiaSalesInfoVO> getSalesInfo(SongXiaDTO songXiaDTO) {
        PageHelper.startPage(songXiaDTO.getPageNum(), songXiaDTO.getPageSize());
        PageDTO<SongXiaSalesInfoVO> vo = new PageDTO<>();
        if (StringUtils.isBlank(songXiaDTO.getStartReportDate())||StringUtils.isBlank(songXiaDTO.getEndReportDate())) {
            return vo;
        }
        Date startReportDate =  Date.valueOf(songXiaDTO.getStartReportDate());
        Date endReportDate =  Date.valueOf(songXiaDTO.getEndReportDate());
        List<SongXiaSalesInfoVO> vos = songXiaMapper.getSalesInfo(startReportDate,endReportDate);
        PageInfo<SongXiaSalesInfoVO> pageInfo = new PageInfo<>(vos);
        vo.setTotal(pageInfo.getTotal());
        vo.setList(pageInfo.getList());
        vo.setPageSize(pageInfo.getPageSize());
        vo.setPageNum(pageInfo.getPageNum());
        return vo;
    }

    @Override
    public PageDTO<SongXiaSampleInfoVO> getSampleInfo(PageRequest request) {
        PageHelper.startPage(request.getPageNum()==null?1:request.getPageNum(), request.getPageSize()==null?20:request.getPageSize());
        PageDTO<SongXiaSampleInfoVO> vo = new PageDTO<>();
        List<SongXiaSampleInfoVO> vos=songXiaMapper.getSampleInfo();
        PageInfo<SongXiaSampleInfoVO> pageInfo = new PageInfo<>(vos);
        vo.setTotal(pageInfo.getTotal());
        vo.setList(pageInfo.getList());
        vo.setPageSize(pageInfo.getPageSize());
        vo.setPageNum(pageInfo.getPageNum());
        return vo;
    }

    @Override
    public PageDTO<SongXiaSampleInfoVO> getStockInfo(SongXiaDTO request) {
        PageHelper.startPage(request.getPageNum()==null?1:request.getPageNum(), request.getPageSize()==null?20:request.getPageSize());
        PageDTO<SongXiaSampleInfoVO> vo = new PageDTO<>();
        List<SongXiaSampleInfoVO> vos=songXiaMapper.getStockInfo(request.getStartReportDate(),request.getEndReportDate());
        PageInfo<SongXiaSampleInfoVO> pageInfo = new PageInfo<>(vos);
        vo.setTotal(pageInfo.getTotal());
        vo.setList(pageInfo.getList());
        vo.setPageSize(pageInfo.getPageSize());
        vo.setPageNum(pageInfo.getPageNum());
        return vo;
    }

}
