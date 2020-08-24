package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 월단위 검색 결과 그래프에 표시할 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholesaleMonthlyRegionInfoDto {
    private String region;
    private int[] monthlySales;
}
