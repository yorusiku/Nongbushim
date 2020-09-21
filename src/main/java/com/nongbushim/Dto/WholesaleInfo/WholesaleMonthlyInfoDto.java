package com.nongbushim.Dto.WholesaleInfo;

import com.nongbushim.Dto.KamisResponse.Monthly.PriceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WholesaleMonthlyInfoDto extends WholesaleInfoDto{
    PriceDto price;
}