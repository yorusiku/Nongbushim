package com.nongbushim.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CountyCode {
    COUNTY_CODE_1101("1101", "서울"),
    COUNTY_CODE_2100("2100", "부산"),
    COUNTY_CODE_2200("2200", "대구"),
    COUNTY_CODE_2300("2300", "인천"),
    COUNTY_CODE_2401("2401", "광주"),
    COUNTY_CODE_2501("2501", "대전"),
    COUNTY_CODE_2601("2601", "울산"),
    COUNTY_CODE_3111("3111", "수원"),
    COUNTY_CODE_3211("3211", "춘천"),
    COUNTY_CODE_3311("3311", "청주"),
    COUNTY_CODE_3511("3511", "전주"),
    COUNTY_CODE_3711("3711", "포항"),
    COUNTY_CODE_3911("3911", "제주"),
    COUNTY_CODE_3113("3113", "의정부"),
    COUNTY_CODE_3613("3613", "순천"),
    COUNTY_CODE_3714("3714", "안동"),
    COUNTY_CODE_3814("3814", "창원"),
    COUNTY_CODE_3145("3145", "용인");

    private String code;
    private String name;

    public static CountyCode searchCountyCode(String code) {
        for (CountyCode countyCode : values()) {
            if (countyCode.code.equals(code))
                return countyCode;
        }
        throw new IllegalArgumentException("CountyCode does not contain " + code);

    }
}
