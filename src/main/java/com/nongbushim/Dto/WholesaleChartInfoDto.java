package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 검색 결과 그래프에 표시할 정보
 * 월, 가격
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholesaleChartInfoDto {
    private String region;
    private String[] label;
    private int[] monthlySales;
    private WholesaleTableDto tableDto;
}
