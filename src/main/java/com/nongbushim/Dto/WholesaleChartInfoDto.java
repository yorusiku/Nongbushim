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
    private int[] avgPrice;
    private int[] maxPrice;
    private int[] minPrice;
    private List<WholesaleRegionInfoDto> wholesaleRegionInfoList;
}
