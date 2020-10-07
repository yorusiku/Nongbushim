package com.nongbushim.Service.Chart;

import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleRegionInfoDto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class ChartService {
    protected static String CHART_TITLE;

    public abstract WholesaleChartInfoDto createChart(List<WholesaleInfoDto> wholesaleInfoList);

    protected void setAvgMaxMin(WholesaleChartInfoDto chartInfoDto) {
        List<WholesaleRegionInfoDto> list = chartInfoDto.getWholesaleRegionInfoList();
        List<String> avgArr = new ArrayList<>();
        List<String> maxArr = new ArrayList<>();
        List<String> minArr = new ArrayList<>();

        int len = list.stream().max(Comparator.comparing(dto -> dto.getPrices().size())).get().getPrices().size();
        for (int i = 0; i < len; i++) {
            int sum = 0;
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            int dtoCount = 0;
            for (WholesaleRegionInfoDto dto : list) {
                int price;
                try {
                    price = dto.getPrices().get(i);
                    if (price==0) continue;
                    sum += price;
                    dtoCount++;
                    if (max < price) max = price;
                    if (min > price) min = price;
                } catch (Exception e) {
                }
            }
            if (dtoCount == 0) {
                avgArr.add("0");
                maxArr.add("0");
                minArr.add("0");
            }
            else {
                avgArr.add(String.format("%,d",sum / dtoCount));
                maxArr.add(String.format("%,d",max));
                minArr.add(String.format("%,d",min));
            }
        }
        chartInfoDto.setAvgPrice(avgArr);
        chartInfoDto.setMaxPrice(maxArr);
        chartInfoDto.setMinPrice(minArr);
    }

    protected void setColour(WholesaleRegionInfoDto dto) {
        Random r = new Random();
        int[] randomNumbers = r.ints(3, 0, 200).toArray();
        String rgba = "rgba(" + randomNumbers[0] + "," + randomNumbers[1] + "," + randomNumbers[2] + ",1)";
        dto.setBackgroundColor(rgba);
        dto.setBorderColor(rgba);
    }
}
