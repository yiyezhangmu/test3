package com.coolcollege.intelligent.controller.boss.manage;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.enterprise.EnterpriseAuditStatusEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.response.ResponseResult;
import com.coolcollege.intelligent.common.util.PageHelperUtil;
import com.coolcollege.intelligent.common.util.StringUtil;
import com.coolcollege.intelligent.common.util.UUIDUtils;
import com.coolcollege.intelligent.dto.AppEnterpriseOpenDto;
import com.coolcollege.intelligent.facade.SyncFacade;
import com.coolcollege.intelligent.model.enterprise.EnterpriseAuditInfoDO;
import com.coolcollege.intelligent.model.enterprise.dto.EnterpriseAuditDTO;
import com.coolcollege.intelligent.model.enterprise.vo.EnterpriseAuditVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.rpc.config.EnterpriseInitConfigApiService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseAuditInfoService;
import com.coolcollege.intelligent.service.enterprise.EnterpriseUserService;
import com.coolcollege.intelligent.util.datasource.DataSourceHelper;
import com.coolstore.base.enums.AppTypeEnum;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 企业审核表
 * 
 * @author xugangkun
 * @email 670809626@qq.com
 * @date 2021-07-19 16:27:52
 */
@RestController
@RequestMapping({"/boss/manage/enterpriseAudit"})
public class EnterpriseAuditInfoController {

	@Autowired
	private EnterpriseAuditInfoService enterpriseAuditInfoService;

	@Autowired
	private EnterpriseUserService enterpriseUserService;

	@Autowired
	private SyncFacade syncFacade;

	@Autowired
	private EnterpriseInitConfigApiService enterpriseInitConfigApiService;

	/**
	 * 企业注册申请列表
	 * @param pageSize
	 * @param pageNumber
	 * @param enterpriseName
	 * @param auditStatus 审核状态
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.common.response.ResponseResult
	 * @date: 2021/7/20 10:02
	 */
	@GetMapping("/list")
	public ResponseResult auditList(@RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
									@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber,
									@RequestParam(value = "enterpriseName", required = false) String enterpriseName,
									@RequestParam(value = "auditStatus", required = false) Integer auditStatus){
		List<EnterpriseAuditVO> auditList = enterpriseAuditInfoService.enterpriseAuditList(pageSize, pageNumber, enterpriseName, auditStatus);
		return ResponseResult.success(PageHelperUtil.getPageVO(new PageInfo(auditList)));
	}

	/**
	 * 企业审核
	 * @param enterpriseAuditDTO 审核对象
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.common.response.ResponseResult
	 * @date: 2021/7/20 15:58
	 */
	@PostMapping("/audit")
	public ResponseResult audit(@RequestBody @Valid EnterpriseAuditDTO enterpriseAuditDTO){
		CurrentUser user = UserHolder.getUser();
		DataSourceHelper.reset();
		EnterpriseAuditInfoDO auditInfo = enterpriseAuditInfoService.selectById(enterpriseAuditDTO.getId());
		if (!EnterpriseAuditStatusEnum.AUDIT_PENDING.getValue().equals(auditInfo.getAuditStatus())) {
			throw new ServiceException("当前审核已通过或者已拒绝,审核失败");
		}
		auditInfo.setAuditStatus(enterpriseAuditDTO.getAuditStatus());
		auditInfo.setRemark(enterpriseAuditDTO.getRemark());
		auditInfo.setAuditUserId(user.getUserId());
		//修改审核记录
		enterpriseAuditInfoService.auditEnterprise(auditInfo);
		if (EnterpriseAuditStatusEnum.AUDIT_PASSED.getValue().equals(enterpriseAuditDTO.getAuditStatus())) {
			//审核通过初始化企业库
			AppEnterpriseOpenDto appEnterpriseOpenDto = new AppEnterpriseOpenDto();
			appEnterpriseOpenDto.setEnterpriseName(auditInfo.getEnterpriseName());
			appEnterpriseOpenDto.setApplyUserName(auditInfo.getApplyUserName());
			appEnterpriseOpenDto.setPassword(auditInfo.getPassword());
			appEnterpriseOpenDto.setMobile(auditInfo.getMobile());
			appEnterpriseOpenDto.setEmail(auditInfo.getEmail());
			String appType;
			if (StringUtils.isNotEmpty(auditInfo.getAppType()))
				appType = auditInfo.getAppType();
			else
				appType = AppTypeEnum.APP.getValue();
			String corpId = appType + UUIDUtils.get32UUID();
			appEnterpriseOpenDto.setCorpId(corpId);
			appEnterpriseOpenDto.setAppType(appType);
			//rpc 调用app开通
			enterpriseInitConfigApiService.appEnterpriseOpen(appEnterpriseOpenDto);
		}
		return ResponseResult.success(true);
	}

	/**
	 * 企业审核
	 * @param enterpriseAuditDTO 审核对象
	 * @author: xugangkun
	 * @return com.coolcollege.intelligent.common.response.ResponseResult
	 * @date: 2021/7/20 15:58
	 */
	@PostMapping("/reopenAudit")
	public ResponseResult reopenAudit(@RequestBody EnterpriseAuditDTO enterpriseAuditDTO){
		EnterpriseAuditInfoDO auditInfo = enterpriseAuditInfoService.selectById(enterpriseAuditDTO.getId());
		if (auditInfo == null) {
			return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "找不到该记录");
		}
		if (!EnterpriseAuditStatusEnum.AUDIT_FAILED.getValue().equals(auditInfo.getAuditStatus())) {
			return ResponseResult.fail(ErrorCodeEnum.FAIL.getCode(), "申请审核状态不符合。只有审核失败的申请才能重新打开");
		}
		auditInfo.setAuditStatus(EnterpriseAuditStatusEnum.AUDIT_PENDING.getValue());
		//修改审核记录
		enterpriseAuditInfoService.auditEnterprise(auditInfo);
		return ResponseResult.success(true);
	}



}
