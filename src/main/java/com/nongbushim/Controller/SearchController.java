package com.nongbushim.Controller;

import com.nongbushim.Dto.FormDto;
import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleChartInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Helper.APIHelper;
import com.nongbushim.Service.Chart.ChartService;
import com.nongbushim.Service.Chart.DailyChartServiceImpl;
import com.nongbushim.Service.Chart.MonthlyChartServiceImpl;
import com.nongbushim.Service.Excel.ExcelService;
import com.nongbushim.Service.Search.DailySearchServiceImpl;
import com.nongbushim.Service.Search.MonthlySearchServiceImpl;
import com.nongbushim.Service.Search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Controller
public class SearchController {

    private static final int THIS_YEAR = LocalDate.now().getYear();
    private static final String NO_SEARCH_MONTHLY_RESULT = THIS_YEAR + "년도에 해당하는 데이터가 없습니다";
    private static final String NO_SEARCH_DAILY_RESULT = "최근 2주일간 해당하는 데이터가 없습니다";

    @Autowired
    private final SearchService dailySearchService;
    @Autowired
    private final SearchService monthlySearchService;
    @Autowired
    private final ChartService dailyChartService;
    @Autowired
    private final ChartService monthlyChartService;
    @Autowired
    private final ExcelService excelService;

    public SearchController(DailySearchServiceImpl dailySearchService, MonthlySearchServiceImpl monthlySearchService, DailyChartServiceImpl dailyChartService, MonthlyChartServiceImpl monthlyChartService, ExcelService excelService) {
        this.dailySearchService = dailySearchService;
        this.monthlySearchService = monthlySearchService;
        this.dailyChartService = dailyChartService;
        this.monthlyChartService = monthlyChartService;
        this.excelService = excelService;
    }

    @GetMapping(value = {"/pricesearch", "/PriceSearch.html"})
    public String priceSearch(Model model) {
        model.addAttribute("form", new FormDto());
        return "PriceSearch";
    }

    @PostMapping(value = {"/pricesearch", "/PriceSearch.html"})
    public String search(@ModelAttribute("form") FormDto form, Model model) throws IOException {
        String input = form.getText();
        List<String> monthlyRequestParameters;
        List<String> dailyRequestParameters;
        try {
            monthlyRequestParameters = monthlySearchService.createOpenAPIRequestParameters(input);
            dailyRequestParameters = dailySearchService.createOpenAPIRequestParameters(input);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 어떤 키워드도 없이 검색버튼을 눌렀을 시 검색창으로 돌아감
            return "PriceSearch";
        }

        List<ResponseEntity<String>> resultMap;
        // 월별
        WholesaleChartInfoDto monthlyChartInfoDto = null;
        resultMap = APIHelper.getResponsesFromOpenAPI(monthlyRequestParameters, APIHelper.MONTHLY_URL);

        List<WholesaleInfoDto> wholesaleMonthlyInfoList = monthlySearchService.getWholesalePrice(resultMap);

        if (wholesaleMonthlyInfoList.isEmpty()) {
            model.addAttribute("monthlyNoSearchResult", NO_SEARCH_MONTHLY_RESULT);
            model.addAttribute("monthlyChartInfo", monthlyChartInfoDto);
            return "PriceSearch";
        }
        monthlyChartInfoDto = monthlyChartService.createChart(wholesaleMonthlyInfoList);
        model.addAttribute("monthlyChartInfo", monthlyChartInfoDto);

        //일별
        WholesaleChartInfoDto dailyChartInfoDto = null;
        resultMap = APIHelper.getResponsesFromOpenAPI(dailyRequestParameters, APIHelper.DAILY_URL);

        List<WholesaleInfoDto> wholesaleDailyInfoList = dailySearchService.getWholesalePrice(resultMap);
        if (wholesaleDailyInfoList.isEmpty() || isInvalid(wholesaleDailyInfoList)) {
            model.addAttribute("dailyNoSearchResult", NO_SEARCH_DAILY_RESULT);
            model.addAttribute("dailyChartInfo", dailyChartInfoDto);
            return "PriceSearch";
        }

        dailyChartInfoDto = dailyChartService.createChart(wholesaleDailyInfoList);
        model.addAttribute("dailyChartInfo", dailyChartInfoDto);
        model.addAttribute("excelForm", new FormDto());

        return "PriceSearch";
    }

    @CrossOrigin
    @PostMapping(value = "/pricesearch/excel", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Resource> downloadExcel(@ModelAttribute("excelForm") FormDto form) throws IOException {
        String input = form.getText();
        List<String> monthlyRequestParameters = monthlySearchService.createOpenAPIRequestParameters(input);
        List<String> dailyRequestParameters = dailySearchService.createOpenAPIRequestParameters(input);

        List<ResponseEntity<String>> resultMap;
        // 월별
        resultMap = APIHelper.getResponsesFromOpenAPI(monthlyRequestParameters, APIHelper.MONTHLY_URL);
        List<WholesaleInfoDto> wholesaleMonthlyInfoList = monthlySearchService.getWholesalePrice(resultMap);

        //일별
        resultMap = APIHelper.getResponsesFromOpenAPI(dailyRequestParameters, APIHelper.DAILY_URL);
        List<WholesaleInfoDto> wholesaleDailyInfoList = dailySearchService.getWholesalePrice(resultMap);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=prices.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(createExcel(wholesaleMonthlyInfoList, wholesaleDailyInfoList));
    }

    private InputStreamResource createExcel(List<WholesaleInfoDto> wholesaleMonthlyInfoList, List<WholesaleInfoDto> wholesaleDailyInfoList) {
        return new InputStreamResource(excelService.createExcel(wholesaleMonthlyInfoList, wholesaleDailyInfoList));
    }

    private boolean isInvalid(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        WholesaleDailyInfoDto target = wholesaleDailyInfoList.stream()
                .filter(WholesaleDailyInfoDto.class::isInstance)
                .map(WholesaleDailyInfoDto.class::cast)
                .max(Comparator.comparing(dto -> {
                    int lastIdx = dto.getDailyItemList().size() - 1;
                    DailyItemDto lastItem = dto.getDailyItemList().get(lastIdx);
                    LocalDate lastDate = LocalDate.parse(lastItem.getYyyy() + "/" + lastItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    return lastDate;
                }))
                .get();
        DailyItemDto lastItem = target.getDailyItemList().get(target.getDailyItemList().size() - 1);
        LocalDate latestDate = LocalDate.parse(lastItem.getYyyy() + "/" + lastItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return latestDate.isBefore(LocalDate.now().minusDays(14));
    }

}
