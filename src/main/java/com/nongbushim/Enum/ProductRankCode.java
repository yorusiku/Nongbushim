package com.nongbushim.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 등급
 */
@Getter
@AllArgsConstructor
public enum ProductRankCode {
    PRODUCT_RANK_01("01", "1", "1등급"),
    PRODUCT_RANK_02("02", "2", "2등급"),
    PRODUCT_RANK_03("03", "3", "3등급"),
    PRODUCT_RANK_04("04", "1", "상품"),
    PRODUCT_RANK_05("05", "2", "중품"),
    PRODUCT_RANK_06("06", "3", "하품"),
    PRODUCT_RANK_07("07", "1", "유기농"),
    PRODUCT_RANK_08("08", "2", "무농약"),
    PRODUCT_RANK_09("09", "3", "저농약"),
    PRODUCT_RANK_10("10", "1", "냉장"),
    PRODUCT_RANK_11("11", "3", "냉동"),
    PRODUCT_RANK_12("12", "4", "무항생제"),
    PRODUCT_RANK_13("13", "1", "S과"),
    PRODUCT_RANK_14("14", "2", "M과"),
    PRODUCT_RANK_15("15", "1", "M과"),
    PRODUCT_RANK_16("16", "2", "S과");

    private String produceRank;
    private String gradeRank;
    private String name;

    public static ProductRankCode searchRank(String name) {
        for (ProductRankCode rank : values()) {
            if (rank.name.equals(name))
                return rank;
        }
        throw new IllegalArgumentException("ProductRankCode does not contain " + name);
    }
}
