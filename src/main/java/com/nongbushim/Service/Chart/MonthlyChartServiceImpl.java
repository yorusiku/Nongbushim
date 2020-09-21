package com.nongbushim.Service.Chart;

import com.nongbushim.Dto.KamisResponse.Monthly.MonthlyItemDto;
import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleMonthlyInfoDto;
import com.nongbushim.Dto.WholesaleRegionInfoDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service("monthlyChartService")
public class MonthlyChartServiceImpl extends ChartService {
    private static final int THIS_YEAR = LocalDate.now().getYear();

    @Override
    public WholesaleChartInfoDto createChart(List<WholesaleInfoDto> wholesaleInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        int lastItemIdx;
        String[] label = new String[12];
        for (WholesaleInfoDto dto : wholesaleInfoList) {
            WholesaleMonthlyInfoDto wholesaleMonthlyInfoDto = (WholesaleMonthlyInfoDto) dto;
            if (CHART_TITLE == null) CHART_TITLE = wholesaleMonthlyInfoDto.getPrice().getCaption();
            List<Integer> monthlySales = new LinkedList<>();
            WholesaleRegionInfoDto wholesaleRegionInfoDto = new WholesaleRegionInfoDto();

            lastItemIdx = wholesaleMonthlyInfoDto.getPrice().getItem().size() - 1;
            if (THIS_YEAR != Integer.parseInt(wholesaleMonthlyInfoDto.getPrice().getItem().get(lastItemIdx).getYyyy()))
                continue;

            int idx = 0;
            while (idx <= 11 && lastItemIdx >= 0) {
                MonthlyItemDto current = wholesaleMonthlyInfoDto.getPrice().getItem().get(lastItemIdx);

                List<String> currentYearMonthlySalesList = currentYearMonthlySalesList(current);
                for (int monthIdx = 11; monthIdx >= 0 && idx <= 11; monthIdx--) {
                    String sales = currentYearMonthlySalesList.get(monthIdx);
                    if ("-".equals(sales)) continue;
                    if (label[11 - idx] == null) label[11 - idx] = createLabel(current, monthIdx);
                    monthlySales.add(0, Integer.parseInt(sales.replace(",", "")));
                    idx++;
                }
                lastItemIdx--;
            }
            wholesaleRegionInfoDto.setRegion(wholesaleMonthlyInfoDto.getCountyCode().getName());
            wholesaleRegionInfoDto.setPrices(monthlySales);
            setColour(wholesaleRegionInfoDto);
            wholesaleRegionInfoDtoList.add(wholesaleRegionInfoDto);
        }
        chartInfoDto.setTitle(CHART_TITLE);
        chartInfoDto.setWholesaleRegionInfoList(wholesaleRegionInfoDtoList);
        chartInfoDto.setLabel(Arrays.stream(label).filter(Objects::nonNull).collect(Collectors.toList()));
        setAvgMaxMin(chartInfoDto);
        return chartInfoDto;
    }

    private String createLabel(MonthlyItemDto current, int monthIdx) {
        String month = (monthIdx + 1) / 10 == 1 ? String.valueOf(monthIdx + 1) : "0" + (monthIdx + 1);
        return current.getYyyy() + "/" + month;
    }

    private List<String> currentYearMonthlySalesList(MonthlyItemDto current) {
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
}
