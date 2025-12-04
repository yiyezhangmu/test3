package com.coolcollege.intelligent.model.newbelle.dto;

import lombok.Data;

@Data
public class BaseGoodsDetailDTO {
    private String product_key;   //商品key
    private String product_no;   //商品编码(14位流水号)
    private String product_code;   //商品代号
    private String product_name;   //商品名称
    private String affiliation_no;   //本部编码
    private String affiliation_name;   //本部名称
    private String brand_no;   //品牌部编码
    private String brand_cname;   //品牌部中文名
    private String brand_name_short;   //品牌部简称
    private String brand_detail_no;   //品牌编码
    private String brand_detail_cname;   //品牌中文名
    private String category_no1;   //商品类别1编码(产品类别编码)
    private String category_name1;   //商品类别1名称(产品类别名称)
    private String category_no2;   //商品类别2编码(大类编码)
    private String category_name2;   //商品类别2名称(大类名称)
    private String category_no3;   //商品类别3编码(小类编码)
    private String category_name3;   //商品类别3名称(小类名称)
    private String gender_no;   //性别编码
    private String gender_name;   //性别名称
    private String product_year_no;   //年份编码
    private String product_year_name;   //年份名称
    private String season_no;   //季节编码
    private String season_name;   //季节名称
    private String product_season_no;   //商品季节编码
    private String product_season_name;   //商品季节名称
    private String purchase_season_no;   //采购季节编码
    private String purchase_season_name;   //采购季节名称
    private String color_no;   //颜色编码
    private String color_name;   //颜色名称
    private String lining_no;   //内里编码
    private String lining_name;   //内里名称
    private String supplier_no;   //供应商编码
    private String supplier_name;   //供应商名称
    private String suggest_prm;   //建议牌价
    private String picture_url;   //图片路径(取主数据图片路径)
    private String listing_date;   //上市日期
    private String style_no;   //风格编码
    private String style_name;   //风格名称
    private String heel_type_no;   //跟型编码
    private String heel_type_name;   //跟型名称
    private String main_mtrl_no;   //主料编码
    private String main_mtrl_name;   //主料名称
    private String dev_attribute_no;   //开发属性编码
    private String dev_attribute_name;   //开发属性名称
    private String order_style_no;   //订货形式编码
    private String order_style_name;   //订货形式名称
    private String customize_no5;   //自定义分类5编码
    private String customize_name5;   //自定义分类5名称

}
