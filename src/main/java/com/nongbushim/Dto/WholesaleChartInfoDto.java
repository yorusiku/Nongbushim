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
    private List<String> label;
    private List<Integer> avgPrice;
    private List<Integer> maxPrice;
    private List<Integer> minPrice;
    private List<WholesaleRegionInfoDto> wholesaleRegionInfoList;
}
