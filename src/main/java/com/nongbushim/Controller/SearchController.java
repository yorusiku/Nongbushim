package com.nongbushim.Controller;

import com.nongbushim.Dto.*;
import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Service.Chart.ChartService;
import com.nongbushim.Service.Chart.DailyChartServiceImpl;
import com.nongbushim.Service.Chart.MonthlyChartServiceImpl;
import com.nongbushim.Service.Excel.ExcelService;
import com.nongbushim.Service.Search.DailySearchServiceImpl;
import com.nongbushim.Service.Search.MonthlySearchServiceImpl;
import com.nongbushim.Service.Search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
public class SearchController {
    private final static String MONTHLY_URL = "http://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList";
    private final static String DAILY_URL = "http://www.kamis.or.kr/service/price/xml.do?action=periodProductList";
    private final static String FIXED_PARAMETERS = "p_cert_key=111&p_cert_id=222&p_returntype=json";
    private final static HttpHeaders HTTP_HEADERS;
    private final static HttpEntity<?> HTTP_ENTITY;
    private final static String ACCESS_KEY = "c870db87-9503-48c8-aca3-dee7f28a42ba";
    private static final int THIS_YEAR = LocalDate.now().getYear();
    private static final String NO_SEARCH_MONTHLY_RESULT = THIS_YEAR + "년도에 해당하는 데이터가 없습니다";
    private static final String NO_SEARCH_DAILY_RESULT = "최근 2주일간 해당하는 데이터가 없습니다";

    static {
        HTTP_HEADERS = new HttpHeaders();
        HTTP_HEADERS.add("key", ACCESS_KEY);
        HTTP_ENTITY = new HttpEntity<>(HTTP_HEADERS);
    }

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
        resultMap = getResponsesFromOpenAPI(monthlyRequestParameters, MONTHLY_URL);

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
        resultMap = getResponsesFromOpenAPI(dailyRequestParameters, DAILY_URL);

        List<WholesaleInfoDto> wholesaleDailyInfoList = dailySearchService.getWholesalePrice(resultMap);
        if (wholesaleDailyInfoList.isEmpty()) {
            model.addAttribute("dailyNoSearchResult", NO_SEARCH_DAILY_RESULT);
            model.addAttribute("dailyChartInfo", dailyChartInfoDto);
            return "PriceSearch";
        }

        // save monthly&daily price information to the excel in memory
        createExcel(monthlyChartInfoDto, wholesaleMonthlyInfoList, wholesaleDailyInfoList);
        if (isInvalid(wholesaleDailyInfoList)){
            model.addAttribute("dailyNoSearchResult", NO_SEARCH_DAILY_RESULT);
            model.addAttribute("dailyChartInfo", dailyChartInfoDto);
            return "PriceSearch";
        }
        dailyChartInfoDto = dailyChartService.createChart(wholesaleDailyInfoList);
        model.addAttribute("dailyChartInfo", dailyChartInfoDto);

        return "PriceSearch";
    }

    private boolean isInvalid(List<WholesaleInfoDto> wholesaleDailyInfoList) {
        WholesaleDailyInfoDto target = wholesaleDailyInfoList.stream()
                .filter(WholesaleDailyInfoDto.class::isInstance)
                .map(WholesaleDailyInfoDto.class::cast)
                .max(Comparator.comparing(dto -> {
                    int lastIdx = dto.getDailyItemList().size()-1;
                    DailyItemDto lastItem = dto.getDailyItemList().get(lastIdx);
                    LocalDate lastDate = LocalDate.parse(lastItem.getYyyy()+"/"+lastItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                    return lastDate;
                }))
                .get();
        DailyItemDto lastItem = target.getDailyItemList().get(target.getDailyItemList().size()-1);
        LocalDate latestDate = LocalDate.parse(lastItem.getYyyy()+"/"+lastItem.getRegday(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return latestDate.isBefore(LocalDate.now().minusDays(14));
    }

    private void createExcel(WholesaleChartInfoDto monthlyChartInfoDto, List<WholesaleInfoDto> wholesaleMonthlyInfoList, List<WholesaleInfoDto> wholesaleDailyInfoList) {
        String title = monthlyChartInfoDto.getTitle();
        excelService.createExcel(wholesaleMonthlyInfoList, wholesaleDailyInfoList, title);
    }

    private List<ResponseEntity<String>> getResponsesFromOpenAPI(List<String> requestParameters, String monthlyUrl) {
        UriComponents uri;
        RestTemplate restTemplate = new RestTemplate();
        List<ResponseEntity<String>> resultMap = new ArrayList<>();
        for (String parameter : requestParameters) {
            uri = UriComponentsBuilder.fromHttpUrl(monthlyUrl + parameter + FIXED_PARAMETERS).build();
            resultMap.add(restTemplate.exchange(uri.toString(), HttpMethod.GET, HTTP_ENTITY, String.class));
        }
        return resultMap;
    }
}
