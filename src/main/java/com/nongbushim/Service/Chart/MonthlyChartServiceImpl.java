package com.nongbushim.Service.Chart;

import com.nongbushim.Dto.KamisResponse.Monthly.MonthlyItemDto;
import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleMonthlyInfoDto;
import com.nongbushim.Dto.WholesaleRegionInfoDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service("monthlyChartService")
public class MonthlyChartServiceImpl extends ChartService {
    private static final int THIS_YEAR = LocalDate.now().getYear();

    @Override
    public WholesaleChartInfoDto createChart(List<WholesaleInfoDto> wholesaleInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        List<String> label = createLabel();

        int lastItemIdx;
        for (WholesaleInfoDto dto : wholesaleInfoList) {
            WholesaleMonthlyInfoDto wholesaleMonthlyInfoDto = (WholesaleMonthlyInfoDto) dto;

            List<Integer> monthlyPrices = new LinkedList<>();
            List<String> monthlyPricesForTable = new LinkedList<>();
            WholesaleRegionInfoDto wholesaleRegionInfoDto = new WholesaleRegionInfoDto();

            lastItemIdx = wholesaleMonthlyInfoDto.getPrice().getItem().size() - 1;
            if (THIS_YEAR != Integer.parseInt(wholesaleMonthlyInfoDto.getPrice().getItem().get(lastItemIdx).getYyyy()))
                continue;

            Map<Integer, List<String>> monthlyPricesPerYearMap = createMonthlyPricePerYearMap(wholesaleMonthlyInfoDto.getPrice().getItem());
            LocalDate now = LocalDate.now();
            for (int i = 0; i < 12; i++) {
                int currentYear = now.minusMonths(i).getYear();
                int currentMonth = now.minusMonths(i).getMonthValue();
                if (!monthlyPricesPerYearMap.containsKey(currentYear)) {
                    monthlyPrices.add(0, 0);
                    monthlyPricesForTable.add(0, "-");
                    continue;
                }

                String price = monthlyPricesPerYearMap.get(currentYear).get(currentMonth - 1);

                if ("-".equals(price)) {
                    monthlyPrices.add(0, null);
                    monthlyPricesForTable.add(0, price);
                } else {
                    monthlyPrices.add(0, Integer.parseInt(price.replace(",", "")));
                    monthlyPricesForTable.add(0, price);
                }
            }
            wholesaleRegionInfoDto.setRegion(wholesaleMonthlyInfoDto.getCountyCode().getName());
            wholesaleRegionInfoDto.setPrices(monthlyPrices);
            wholesaleRegionInfoDto.setPricesForTable(monthlyPricesForTable);
            setColour(wholesaleRegionInfoDto);
            wholesaleRegionInfoDtoList.add(wholesaleRegionInfoDto);
        }
        chartInfoDto.setTitle(createChartTitle(wholesaleInfoList));
        chartInfoDto.setWholesaleRegionInfoList(wholesaleRegionInfoDtoList);
        chartInfoDto.setLabel(label);
        setAvgMaxMin(chartInfoDto);
        return chartInfoDto;
    }

    private String createChartTitle(List<WholesaleInfoDto> wholesaleInfoList) {
        return CHART_TITLE = wholesaleInfoList.stream().filter(WholesaleMonthlyInfoDto.class::isInstance)
                .map(WholesaleMonthlyInfoDto.class::cast)
                .filter(dto -> dto.getPrice() != null)
                .findFirst().get().getPrice().getCaption();
    }

    private List<String> getYearMonthlySalesList(MonthlyItemDto current) {
        return Arrays.asList(
                current.getM1(),
                current.getM2(),
                current.getM3(),
                current.getM4(),
                current.getM5(),
                current.getM6(),
                current.getM7(),
                current.getM8(),
                current.getM9(),
                current.getM10(),
                current.getM11(),
                current.getM12()
        );
    }

    private Map<Integer, List<String>> createMonthlyPricePerYearMap(List<MonthlyItemDto> item) {
        Map<Integer, List<String>> map = new LinkedHashMap<>();
        for (MonthlyItemDto dto : item) {
            map.put(Integer.parseInt(dto.getYyyy()), getYearMonthlySalesList(dto));
        }
        return map;
    }

    private List<String> createLabel() {
        List<String> label = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            int currentYear = now.minusMonths(i).getYear();
            int currentMonth = now.minusMonths(i).getMonthValue();
            label.add(0, currentYear + "/" + editTypo(currentMonth));
        }
        return label;
    }

    private String editTypo(int monthValue) {
        return monthValue / 10 == 1 ? String.valueOf(monthValue) : "0" + monthValue;
    }
}
