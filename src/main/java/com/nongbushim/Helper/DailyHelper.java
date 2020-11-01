package com.nongbushim.Helper;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DailyHelper {
    public static List<String> createLabel(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        List<String> label = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusWeeks(2).plusDays(1);

        for (int days=0;days<14;days++){
            LocalDate date = startDate.plusDays(days);
            DayOfWeek day = date.getDayOfWeek();
            if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) continue;
            label.add(date.format(DateTimeFormatter.ofPattern("MM/dd")));
        }
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
