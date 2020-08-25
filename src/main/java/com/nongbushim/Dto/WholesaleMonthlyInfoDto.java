package com.nongbushim.Dto;

import com.nongbushim.Dto.KamisResponse.Monthly.PriceDto;
import com.nongbushim.Enum.CountyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholesaleMonthlyInfoDto {
    PriceDto price;
    CountyCode countyCode;
}