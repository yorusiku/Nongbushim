package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholesaleChartInfoDto {
    private String title;
    private String[] label;
    private int avgMonthAgoPrice;
    private int avgYearAgoPrice;
    private int maxMonthAgoPrice;
    private int maxYearAgoPrice;
    private int minMonthAgoPrice;
    private int minYearAgoPrice;
    private List<WholesaleRegionInfoDto> wholesaleRegionInfoList;
}
