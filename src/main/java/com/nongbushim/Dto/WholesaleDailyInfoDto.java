package com.nongbushim.Dto;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Enum.CountyCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WholesaleDailyInfoDto {
    List<DailyItemDto> dailyItemList;
    CountyCode countyCode;

}
