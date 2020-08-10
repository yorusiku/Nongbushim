package com.nongbushim.Dto;

import com.nongbushim.Dto.KamisResponse.Price;
import com.nongbushim.Enum.CountyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholesaleInfoDto {
    Price price;
    CountyCode countyCode;
}
