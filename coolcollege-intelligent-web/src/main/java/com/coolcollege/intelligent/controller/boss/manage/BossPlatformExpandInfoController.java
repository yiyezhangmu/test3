package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.model.platform.PlatformExpandInfoDO;
import com.coolcollege.intelligent.model.platform.dto.PlatformExpandInfoDTO;
import com.coolcollege.intelligent.service.platform.PlatformExpandInfoService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


/**
 * 平台拓展信息配置表
 * 
 * @author xugangkun
 * @email 670809626@qq.com
 * @date 2021-12-01 14:26:21
 */
@RestController
@RequestMapping("/boss/manage/platformExpandInfo")
public class BossPlatformExpandInfoController {
	
	@Autowired
	private PlatformExpandInfoService platformExpandInfoService;
	
	
	/**
	 * 信息
	 */
	@GetMapping("/info")
	public ResponseResult info(@RequestParam(value = "id") Long id){
		DataSourceHelper.reset();
		PlatformExpandInfoDO platformExpandInfo = platformExpandInfoService.selectById(id);
		return ResponseResult.success(platformExpandInfo);
	}

	@GetMapping("/getByCode")
	public ResponseResult info(@RequestParam(value = "code") String code){
		DataSourceHelper.reset();
		PlatformExpandInfoDO platformExpandInfo = platformExpandInfoService.selectByCode(code);
		return ResponseResult.success(platformExpandInfo);
	}

	/**
	 * 保存
	 */
	@PostMapping("/save")
	public ResponseResult save(@RequestBody @Valid PlatformExpandInfoDTO dto){
		DataSourceHelper.reset();
		PlatformExpandInfoDO platformExpandInfo = new PlatformExpandInfoDO();
		platformExpandInfo.setCode(dto.getCode());
		platformExpandInfo.setName(dto.getName());
		platformExpandInfo.setRemark(dto.getRemark());
		platformExpandInfo.setContent(dto.getContent());
		platformExpandInfo.setValid(dto.getValid());
		platformExpandInfoService.save(platformExpandInfo);
		return ResponseResult.success();
	}
	
	/**
	 * 修改
	 */
	@PostMapping("/updateByCode")
	public ResponseResult updateByCode(@RequestBody @Valid PlatformExpandInfoDTO dto){
		DataSourceHelper.reset();
		PlatformExpandInfoDO platformExpandInfo = new PlatformExpandInfoDO();
		platformExpandInfo.setCode(dto.getCode());
		platformExpandInfo.setName(dto.getName());
		platformExpandInfo.setRemark(dto.getRemark());
		platformExpandInfo.setContent(dto.getContent());
		platformExpandInfo.setValid(dto.getValid());
		platformExpandInfoService.updateByCode(platformExpandInfo);
		return ResponseResult.success();
	}
	
	
}
