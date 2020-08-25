package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 월단위 검색 결과 그래프에 표시할 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholesaleRegionInfoDto {
    private String region;
    private List<Integer> prices;
}
