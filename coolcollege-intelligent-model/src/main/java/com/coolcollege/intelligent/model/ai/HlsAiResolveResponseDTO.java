package com.coolcollege.intelligent.model.ai;

import lombok.Data;

@Data
public class HlsAiResolveResponseDTO {

    private String enterpriseId;

    /**
     * tb_data_sta_table_column_表id
     */
    private Long id;

    /**
     * True / False 2中取值：
     * - True：审核通过
     * - False：审核不通过
     */
    private Boolean check_status;

    /**
     * 审核结果描述：
     * 如果check_status为True，check_msg为空字符串；
     * 如果check_status为False，check_msg会给出审核不通过的描述，如“未检测到有效期, 效期卡可能存在手写体、污渍、遮挡、弯曲折叠等情况”
     */
    private String check_msg;

    /**
     * 审核状态码：
     * 600：访问服务成功；
     * 601：效期卡检测失败
     * 602：效期卡识别失败
     * 603：代码内部报错
     */
    private Integer check_code;

    /**
     * 所有图片的url：通过图使用原始url；不通过图(叠加不通过的框)使用我新增的url
     */
    private String ocr_pics;

}
