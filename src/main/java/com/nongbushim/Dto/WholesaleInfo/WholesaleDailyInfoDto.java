package com.nongbushim.Dto.WholesaleInfo;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Enum.CountyCode;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class WholesaleDailyInfoDto extends WholesaleInfoDto{
    List<DailyItemDto> dailyItemList;
}
