package com.nongbushim.Service.Chart;

import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleRegionInfoDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service("dailyChartService")
public class DailyChartServiceImpl extends ChartService {
    @Override
    public WholesaleChartInfoDto createChart(List<WholesaleInfoDto> wholesaleInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        List<String> label = createLabel(wholesaleInfoList);

        for (WholesaleInfoDto infoDto : wholesaleInfoList) {
            WholesaleDailyInfoDto dto = (WholesaleDailyInfoDto) infoDto;
            WholesaleRegionInfoDto wholesaleRegionInfoDto = new WholesaleRegionInfoDto();

            List<Integer> dailyPrices = new ArrayList<>();
            List<String> dailyPricesForTable = new ArrayList<>();
            int len = getMaxSizeDailyInfoDto(wholesaleInfoList).getDailyItemList().size();
            for (int i = 0; i < len; i++) {
                try {
                    DailyItemDto currentItem = dto.getDailyItemList().get(i);
                    String price = currentItem.getPrice().replace(",", "");
                    dailyPrices.add(Integer.parseInt(price));
                    dailyPricesForTable.add(price);
                }catch (IndexOutOfBoundsException e){
                    dailyPricesForTable.add("-");
                }
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

    private List<String> createLabel(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        List<String> label = new ArrayList<>();
        WholesaleDailyInfoDto maxSizeDto = getMaxSizeDailyInfoDto(wholesaleDailyInfoList);
        for (DailyItemDto dto : maxSizeDto.getDailyItemList())
            label.add(dto.getRegday());
        return label;
    }

    private WholesaleDailyInfoDto getMaxSizeDailyInfoDto(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        return wholesaleDailyInfoList.stream()
                    .filter(WholesaleDailyInfoDto.class::isInstance)
                    .map(WholesaleDailyInfoDto.class::cast)
                    .max(Comparator.comparing(dto -> dto.getDailyItemList().size()))
                    .get();
    }
}
