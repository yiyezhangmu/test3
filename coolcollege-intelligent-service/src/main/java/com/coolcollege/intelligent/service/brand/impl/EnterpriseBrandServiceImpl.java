package com.coolcollege.intelligent.service.brand.impl;

import cn.hutool.core.collection.CollStreamUtil;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.exception.ServiceException;
import com.coolcollege.intelligent.common.util.BeanUtil;
import com.coolcollege.intelligent.dao.brand.EnterpriseBrandMapper;
import com.coolcollege.intelligent.model.brand.EnterpriseBrandDO;
import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandQueryRequest;
import com.coolcollege.intelligent.model.brand.request.EnterpriseBrandUpdateRequest;
import com.coolcollege.intelligent.model.brand.vo.EnterpriseBrandVO;
import com.coolcollege.intelligent.model.userholder.CurrentUser;
import com.coolcollege.intelligent.model.userholder.UserHolder;
import com.coolcollege.intelligent.service.brand.EnterpriseBrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 品牌 服务实现类
 * </p>
 *
 * @author wangff
 * @since 2025/3/6
 */
@Service
@RequiredArgsConstructor
public class EnterpriseBrandServiceImpl implements EnterpriseBrandService {
    private final EnterpriseBrandMapper brandMapper;

    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;


    @Override
    public Long insert(String enterpriseId, EnterpriseBrandUpdateRequest request) {
        if (StringUtils.isBlank(request.getCode())) {
            request.setCode(generateAndVerifyCode(enterpriseId));
        } else if (brandMapper.existsByCode(enterpriseId, request.getCode())) {
            throw new ServiceException(ErrorCodeEnum.USER_NOT_EXIST);
        }
        EnterpriseBrandDO brandDO = BeanUtil.toBean(request, EnterpriseBrandDO.class);
        beforeUpdate(enterpriseId, brandDO, true);
        brandMapper.insertSelective(brandDO, enterpriseId);
        return brandDO.getId();
    }

    @Override
    public Long update(String enterpriseId, EnterpriseBrandUpdateRequest request) {
        request.setCode(null);
        EnterpriseBrandDO brandDO = BeanUtil.toBean(request, EnterpriseBrandDO.class);
        beforeUpdate(enterpriseId, brandDO, false);
        brandMapper.updateByPrimaryKeySelective(brandDO, enterpriseId);
        return brandDO.getId();
    }

    /**
     * 生成code并校验是否存在
     * @param enterpriseId 企业id
     * @return 品牌code
     */
    private String generateAndVerifyCode(String enterpriseId) {
        String code = "";
        HashSet<String> codes = new HashSet<>();
        do {
            codes.add(code);
            code = generateCode();
        } while (codes.contains(code) || brandMapper.existsByCode(enterpriseId, code));
        return code;
    }

    private static String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            code.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }

    /**
     * 更新前置操作
     * @param enterpriseId 企业id
     * @param brandDO 实体对象
     */
    private void beforeUpdate(String enterpriseId, EnterpriseBrandDO brandDO, boolean isInsert) {
        if (brandMapper.existsNameExcludeId(enterpriseId, brandDO.getId(), brandDO.getName())) {
            throw new ServiceException(ErrorCodeEnum.BRAND_NAME_EXIST);
        }
        CurrentUser user = UserHolder.getUser();
        if (isInsert) {
            brandDO.setCreateUserId(user.getUserId());
            brandDO.setCreateUserName(user.getName());
        }
        brandDO.setUpdateUserId(user.getUserId());
        brandDO.setUpdateUserName(user.getName());
    }

    @Override
    public boolean removeBatch(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return false;
        }
        List<EnterpriseBrandDO> brands = brandMapper.selectByIds(enterpriseId, ids);
        List<EnterpriseBrandDO> initBrands = brands.stream().filter(v -> Constants.INDEX_ONE.equals(v.getInitStatus())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(initBrands)) {
            String msg = initBrands.stream().map(EnterpriseBrandDO::getName).collect(Collectors.joining("、"));
            throw new ServiceException(ErrorCodeEnum.BRAND_INIT_COMPLETED, msg);
        }
        return brandMapper.deleteBatch(enterpriseId, ids) > 0;
    }

    @Override
    public EnterpriseBrandVO getVOById(String enterpriseId, Long id) {
        EnterpriseBrandDO brandDO = brandMapper.selectById(enterpriseId, id);
        return BeanUtil.toBean(brandDO, EnterpriseBrandVO.class);
    }

    @Override
    public List<EnterpriseBrandVO> getVOList(String enterpriseId, EnterpriseBrandQueryRequest request) {
        List<EnterpriseBrandDO> list = brandMapper.getList(enterpriseId, request);
        return BeanUtil.toList(list, EnterpriseBrandVO.class);
    }

    @Override
    public PageInfo<EnterpriseBrandVO> getVOPage(String enterpriseId, EnterpriseBrandQueryRequest request) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<EnterpriseBrandDO> list = brandMapper.getList(enterpriseId, request);
        PageInfo<EnterpriseBrandDO> page = new PageInfo<>(list);
        return BeanUtil.toPage(page, EnterpriseBrandVO.class);
    }

    @Override
    public Map<Long, String> getNameMapByIds(String enterpriseId, List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        List<EnterpriseBrandDO> list = brandMapper.getNameByIds(enterpriseId, ids);
        return CollStreamUtil.toMap(list, EnterpriseBrandDO::getId, EnterpriseBrandDO::getName);
    }
}
