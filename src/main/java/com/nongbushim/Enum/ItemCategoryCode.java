package com.nongbushim.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 부류코드
 */
@Getter
@AllArgsConstructor
public enum ItemCategoryCode {
    ITEM_CATEGORY_CODE_100("100", "식량작물"),
    ITEM_CATEGORY_CODE_200("200", "채소류"),
    ITEM_CATEGORY_CODE_300("300", "특용작물"),
    ITEM_CATEGORY_CODE_400("400", "과일류"),
    ITEM_CATEGORY_CODE_500("500", "축산물"),
    ITEM_CATEGORY_CODE_600("600", "수산물");
    private String code;
    private String name;
}
