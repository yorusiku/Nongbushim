package com.nongbushim.Service.Chart;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleRegionInfoDto;
import com.nongbushim.Helper.DailyHelper;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("dailyChartService")
public class DailyChartServiceImpl extends ChartService {
    private final LocalDate now = LocalDate.now();
    private final LocalDate startDate =  now.minusWeeks(2);

    @Override
    public WholesaleChartInfoDto createChart(List<WholesaleInfoDto> wholesaleInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        List<String> label = DailyHelper.createLabel(wholesaleInfoList);

        for (WholesaleInfoDto infoDto : wholesaleInfoList) {
            WholesaleDailyInfoDto dto = (WholesaleDailyInfoDto) infoDto;
            WholesaleRegionInfoDto wholesaleRegionInfoDto = new WholesaleRegionInfoDto();

            List<Integer> dailyPrices = new ArrayList<>();
            List<String> dailyPricesForTable = new ArrayList<>();

            WholesaleDailyInfoDto maxDto = DailyHelper.getMaxSizeDailyInfoDto(wholesaleInfoList);
            int maxLen = maxDto.getDailyItemList().size();
            int dtoLen = dto.getDailyItemList().size();
            int currentIdx = 0;

            LocalDate baseDate = startDate;
            while (currentIdx < maxLen && currentIdx < dtoLen) {
                if (baseDate.getDayOfWeek() == DayOfWeek.SATURDAY || baseDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    baseDate = baseDate.plusDays(1);
                    continue;
                }
                try {
                    DailyItemDto currentItem = dto.getDailyItemList().get(currentIdx);
                    LocalDate currentDate = LocalDate.parse(currentItem.getYyyy() + "/" + currentItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));

                    if (baseDate.isBefore(currentDate)) {
                        dailyPrices.add(0);
                        dailyPricesForTable.add("-");
                        baseDate = baseDate.plusDays(1);
                        continue;
                    } else if (baseDate.isAfter(currentDate)){
                        currentIdx++;
                        continue;
                    }
                    String price = currentItem.getPrice();
                    if ("-".equals(price)) dailyPrices.add(0);
                    else dailyPrices.add(Integer.parseInt(price.replace(",", "")));
                    dailyPricesForTable.add(price);
                    baseDate = baseDate.plusDays(1);
                    currentIdx++;
                } catch (IndexOutOfBoundsException e) {
                    dailyPricesForTable.add("-");
                }
            }
            wholesaleRegionInfoDto.setRegion(dto.getCountyCode().getName());
            wholesaleRegionInfoDto.setPrices(dailyPrices.subList(Math.max(dailyPrices.size() - 10, 0), dailyPrices.size()));
            wholesaleRegionInfoDto.setPricesForTable(dailyPricesForTable.subList(Math.max(dailyPricesForTable.size() - 10, 0), dailyPricesForTable.size()));
            setColour(wholesaleRegionInfoDto);
            wholesaleRegionInfoDtoList.add(wholesaleRegionInfoDto);
        }
        chartInfoDto.setTitle(CHART_TITLE);
        chartInfoDto.setWholesaleRegionInfoList(wholesaleRegionInfoDtoList);
        chartInfoDto.setLabel(label);
        setAvgMaxMin(chartInfoDto);
        return chartInfoDto;
    }

}
