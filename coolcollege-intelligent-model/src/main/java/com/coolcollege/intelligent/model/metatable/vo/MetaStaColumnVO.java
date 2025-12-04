package com.coolcollege.intelligent.model.metatable.vo;

import com.alibaba.fastjson.JSONObject;
import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.model.coolrelation.vo.CoolCourseVO;
import com.coolcollege.intelligent.model.metatable.TbMetaStaTableColumnDO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnReasonDTO;
import com.coolcollege.intelligent.model.metatable.dto.TbMetaColumnResultDTO;
import com.coolcollege.intelligent.model.safetycheck.dto.TbMetaColumnAppealDTO;
import com.coolcollege.intelligent.model.sop.vo.TaskSopVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.coolcollege.intelligent.model.metatable.MetaTableConstant.CheckResultConstant.*;
@ApiModel
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaStaColumnVO extends TbMetaStaTableColumnDO {

    private List<TbMetaColumnResultDTO> columnResultList;

    private TaskSopVO taskSopVO;

    private CoolCourseVO coolCourseVO;

    private CoolCourseVO freeCourseVO;

    private String columnTypeName;

    private BigDecimal maxScore;

    private BigDecimal minScore;

    @ApiModelProperty("检查表名称")
    private String metaTableName;

    private List<Boolean> executeDemandList;

    @ApiModelProperty("不合格项原因列表")
    List<TbMetaColumnReasonDTO> columnReasonList;

    @ApiModelProperty("是否强制拍照 0否 1是")
    private Integer mustPic;

    @ApiModelProperty("申诉快捷項列表")
    List<TbMetaColumnAppealDTO> columnAppealList;

    @ApiModelProperty("是否AI检查")
    private Integer isAiCheck;

    @ApiModelProperty("ai标准描述")
    private String aiCheckStdDesc;

    @ApiModelProperty("检查描述是否必填")
    private Boolean descRequired;

    @ApiModelProperty("自动工单有效期（时）")
    private Integer autoQuestionTaskValidity;

    @ApiModelProperty("是否设置工单有效期")
    private Boolean isSetAutoQuestionTaskValidity;

    @ApiModelProperty("AI模型")
    private String aiModel;

    @ApiModelProperty("AI模型名称")
    private String aiModelName;

    @ApiModelProperty("强制检查图片上传数量-最小值")
    public Integer minCheckPicNum;

    @ApiModelProperty("强制检查图片上传数量-最大值")
    public Integer maxCheckPicNum;

    public void fillColumnResultList() {
        if (CollectionUtils.isNotEmpty(columnResultList)) {
            return;
        }
        columnResultList = new ArrayList<>();
        // 合格
        TbMetaColumnResultDTO passResult =
                TbMetaColumnResultDTO.builder().metaTableId(this.getMetaTableId()).metaColumnId(this.getId())
                .score(this.getSupportScore()).money(this.getAwardMoney()).mappingResult(PASS).build();
        columnResultList.add(passResult);
        // 不合格
        TbMetaColumnResultDTO failResult =
                TbMetaColumnResultDTO.builder().metaTableId(this.getMetaTableId()).metaColumnId(this.getId())
                .score(this.getLowestScore()).money(new BigDecimal(Constants.ZERO_STR).subtract(this.getPunishMoney())).mappingResult(FAIL).build();
        columnResultList.add(failResult);
        // 不适用
        TbMetaColumnResultDTO inapplicableResult =
                TbMetaColumnResultDTO.builder().metaTableId(this.getMetaTableId()).metaColumnId(this.getId()).score(new BigDecimal(Constants.ZERO_STR))
                .money(new BigDecimal(Constants.ZERO_STR)).mappingResult(INAPPLICABLE).build();
        columnResultList.add(inapplicableResult);
    }

    public List<Boolean> getExecuteDemandList() {
        if(StringUtils.isNotBlank(this.getExecuteDemand())){
            return JSONObject.parseArray(getExecuteDemand(), Boolean.class);
        }
        return Collections.emptyList();
    }
}
