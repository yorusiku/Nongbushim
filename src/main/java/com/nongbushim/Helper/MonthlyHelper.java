package com.nongbushim.Helper;

import com.nongbushim.Dto.KamisResponse.Monthly.MonthlyItemDto;

import java.util.Arrays;
import java.util.List;

public class MonthlyHelper {
    public static List<String> getYearMonthlyPriceList(MonthlyItemDto dto) {
        return Arrays.asList(
                dto.getM1(),
                dto.getM2(),
                dto.getM3(),
                dto.getM4(),
                dto.getM5(),
                dto.getM6(),
                dto.getM7(),
                dto.getM8(),
                dto.getM9(),
                dto.getM10(),
                dto.getM11(),
                dto.getM12()
        );
    }
}
