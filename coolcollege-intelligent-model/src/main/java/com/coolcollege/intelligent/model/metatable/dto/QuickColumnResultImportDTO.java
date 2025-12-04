package com.coolcollege.intelligent.model.metatable.dto;

import com.coolcollege.intelligent.common.enums.table.ColumnEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @author chenyupeng
 * @since 2022/4/12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickColumnResultImportDTO {

    private Long id;

    private Long metaQuickColumnId;

    private String name;

    private String score;

    private BigDecimal defaultMoney;

    private Integer checkPic;

    private String checkDec;

    private String result;

    public QuickColumnResultImportDTO(String name, String score, String defaultMoney,String checkPic,String checkDec, String result) {
        this.name = name;
        this.score = score;
        if(StringUtils.isBlank(defaultMoney)) {
            this.defaultMoney = BigDecimal.ZERO;
        }else {
            this.defaultMoney = new BigDecimal(defaultMoney);
        }
        if (StringUtils.isNotEmpty(checkPic)){
            switch (checkPic){
                case "强制上传图片":
                    this.checkPic= ColumnEnum.MUST_PICA.getNum();
                    break;
                case "强制拍照":
                    this.checkPic=ColumnEnum.MUST_PICB.getNum();
                    break;
                case "强制拍视频":
                    this.checkPic=ColumnEnum.MUST_PICD.getNum();
                    break;
                default:
                    this.checkPic=ColumnEnum.MUST_PICC.getNum();
            }
        }else {
            this.checkPic=ColumnEnum.MUST_PICC.getNum();
        }
        if (StringUtils.isNotEmpty(checkDec)){
            switch (checkDec){
                case "强制":
                    this.checkDec=ColumnEnum.FORCE.getCode();
                    break;
                default:
                    this.checkDec=ColumnEnum.IGNORE.getCode();
            }
        }else {
            this.checkDec=ColumnEnum.IGNORE.getCode();
        }
        this.result = result;
    }
}
