package com.leyou.common.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Jiang-gege
 * 2019/9/2322:58
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    CATEGORY_NOT_FOUND(404,"商品分类没查到"),
    BRAND_NOT_FOUND(404,"商品不存在"),
    GOODS_NOT_FOUND(404,"商品不存在"),
    SPEC_GROUP_NOT_FOUN(404,"商品规格组未查到"),
    SPEC_PARAM_NOT_FOUN(404,"商品规格参数未查到"),
    BRAND_SAVE_ERROR(500,"品牌新增失败"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    INVALID_FILE_TYPE(500,"无效的文件类型"),
    GOODS_SAVE_ERROR(500,"新增商品失败"),
    INVALID_USER_DATA_TYPE(400,"用户数据类型无效"),

    ;
    private int code;
    private String msg;

}