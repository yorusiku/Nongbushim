package com.nongbushim.Helper;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DailyHelper {
    public static List<String> createLabel(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        List<String> label = new ArrayList<>();
        WholesaleDailyInfoDto maxSizeDto = getMaxSizeDailyInfoDto(wholesaleDailyInfoList);
        for (DailyItemDto dto : maxSizeDto.getDailyItemList())
            label.add(dto.getRegday());
        return label;
    }

    public static WholesaleDailyInfoDto getMaxSizeDailyInfoDto(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        return wholesaleDailyInfoList.stream()
                .filter(WholesaleDailyInfoDto.class::isInstance)
                .map(WholesaleDailyInfoDto.class::cast)
                .max(Comparator.comparing(dto -> dto.getDailyItemList().size()))
                .get();
    }
}
