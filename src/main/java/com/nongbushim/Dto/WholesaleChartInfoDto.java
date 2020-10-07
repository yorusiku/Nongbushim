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
    private List<String> avgPrice;
    private List<String> maxPrice;
    private List<String> minPrice;
    private List<WholesaleRegionInfoDto> wholesaleRegionInfoList;
}
