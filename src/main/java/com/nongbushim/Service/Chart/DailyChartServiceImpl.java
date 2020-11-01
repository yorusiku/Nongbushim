package com.nongbushim.Service.Chart;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleRegionInfoDto;
import com.nongbushim.Helper.DailyHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("dailyChartService")
public class DailyChartServiceImpl extends ChartService {
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
            int maxIdx = 0;
            int currentIdx = 0;

            while (maxIdx < maxLen && currentIdx < maxLen) {
                DailyItemDto currentItem = dto.getDailyItemList().get(currentIdx);
                LocalDate currentDate = LocalDate.parse(currentItem.getYyyy() + "/" + currentItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                DailyItemDto maxItem = maxDto.getDailyItemList().get(maxIdx);
                LocalDate maxDate = LocalDate.parse(maxItem.getYyyy() + "/" + maxItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                if (maxDate.isBefore(currentDate)) {
                    dailyPrices.add(0);
                    dailyPricesForTable.add("-");
                    maxIdx++;
                    continue;
                }
                String price = currentItem.getPrice();
                dailyPrices.add(Integer.parseInt(price.replace(",", "")));
                dailyPricesForTable.add(price);
                maxIdx++;
                currentIdx++;
            }
            wholesaleRegionInfoDto.setRegion(dto.getCountyCode().getName());
            wholesaleRegionInfoDto.setPrices(dailyPrices);
            wholesaleRegionInfoDto.setPricesForTable(dailyPricesForTable);
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
