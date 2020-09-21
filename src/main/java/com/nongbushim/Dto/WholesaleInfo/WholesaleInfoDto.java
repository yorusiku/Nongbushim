package com.nongbushim.Dto.WholesaleInfo;

import com.nongbushim.Enum.CountyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WholesaleInfoDto {
    CountyCode countyCode;
}
