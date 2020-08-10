package com.nongbushim.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 등급
 */
@Getter
@AllArgsConstructor
public enum GradeRank {
    GRADE_RANK_1("1", "상품"),
    GRADE_RANK_2("2", "중품");
    private String code;
    private String name;

    public static GradeRank searchGradeRank(String name) {
        for (GradeRank rank: values()) {
            if (rank.name.equals(name))
                return rank;
        }
        throw new IllegalArgumentException("GradeRank does not contain "+name);

    }
}
