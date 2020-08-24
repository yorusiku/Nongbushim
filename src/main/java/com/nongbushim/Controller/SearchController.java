package com.nongbushim.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nongbushim.Dto.*;
import com.nongbushim.Dto.KamisRequest.*;
import com.nongbushim.Dto.KamisResponse.*;
import com.nongbushim.Enum.CountyCode;
import com.nongbushim.SearchService;
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
    private final static Set<CountyCode> WHOLESALE_COUNTY_CODES = EnumSet.of(CountyCode.COUNTY_CODE_1101, CountyCode.COUNTY_CODE_2100, CountyCode.COUNTY_CODE_2200, CountyCode.COUNTY_CODE_2401, CountyCode.COUNTY_CODE_2501);

    static {
        HTTP_HEADERS = new HttpHeaders();
        HTTP_HEADERS.add("key", ACCESS_KEY);
        HTTP_ENTITY = new HttpEntity<>(HTTP_HEADERS);
    }

    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    @GetMapping(value = {"/pricesearch", "/PriceSearch.html"})
    public String priceSearch(Model model) {
        model.addAttribute("form", new FormDto());
        return "PriceSearch";
    }

    @RequestMapping("/autoComplete")
    @ResponseBody
    public List<String> autoComplete(@RequestParam(value = "term", required = false, defaultValue = "") String term) {
        List<String> suggestions = new LinkedList<>();
        try {
            return service.searchAutoCompleteTarget(term);
        } catch (IOException e) {
        }
        return suggestions;
    }

    @PostMapping(value = {"/pricesearch", "/PriceSearch.html"})
    public String search(@ModelAttribute("form") FormDto form, Model model) throws IOException {
        String input = form.getText();
        List<String> monthlyRequestParameters;
        List<String> dailyRequestParameters;
        try {
            monthlyRequestParameters = createMonthlyRequestParameters(input);
            dailyRequestParameters = createDailyRequestParameters(input);
        } catch (ArrayIndexOutOfBoundsException e) {
            // 어떤 키워드도 없이 검색버튼을 눌렀을 시 검색창으로 돌아감
            return "PriceSearch";
        }

        // 월별
        UriComponents uri;
        RestTemplate restTemplate = new RestTemplate();
        List<ResponseEntity<String>> resultMap = new ArrayList<>();
        for (String parameter : monthlyRequestParameters) {
            uri = UriComponentsBuilder.fromHttpUrl(MONTHLY_URL + parameter + FIXED_PARAMETERS).build();
            resultMap.add(restTemplate.exchange(uri.toString(), HttpMethod.GET, HTTP_ENTITY, String.class));
        }

        //일별
        UriComponents uri2;
        RestTemplate restTemplate2 = new RestTemplate();
        List<ResponseEntity<String>> resultMap2 = new ArrayList<>();
        for (String parameter : dailyRequestParameters) {
            uri2 = UriComponentsBuilder.fromHttpUrl(DAILY_URL + parameter + FIXED_PARAMETERS).build();
            resultMap2.add(restTemplate2.exchange(uri2.toString(), HttpMethod.GET, HTTP_ENTITY, String.class));
        }

        List<WholesaleInfoDto> wholesaleMonthlyInfoList = getWholesaleMonthlyPrice(resultMap);
        WholesaleChartInfoDto monthlyChartInfoDto;
        try {
            monthlyChartInfoDto = createMonthlyChartInfo(wholesaleMonthlyInfoList);
            model.addAttribute("chartInfo", monthlyChartInfoDto);

            model.addAttribute("seoulData", monthlyChartInfoDto.getWholesaleRegionInfoList().get(0).getMonthlySales());
            model.addAttribute("seoulLabel", monthlyChartInfoDto.getWholesaleRegionInfoList().get(0).getRegion());

            model.addAttribute("busanData", monthlyChartInfoDto.getWholesaleRegionInfoList().get(1).getMonthlySales());
            model.addAttribute("busanLabel", monthlyChartInfoDto.getWholesaleRegionInfoList().get(1).getRegion());

            model.addAttribute("daeguData", monthlyChartInfoDto.getWholesaleRegionInfoList().get(2).getMonthlySales());
            model.addAttribute("daeguLabel", monthlyChartInfoDto.getWholesaleRegionInfoList().get(2).getRegion());

            model.addAttribute("gwangjuData", monthlyChartInfoDto.getWholesaleRegionInfoList().get(3).getMonthlySales());
            model.addAttribute("gwangjuLabel", monthlyChartInfoDto.getWholesaleRegionInfoList().get(3).getRegion());

            model.addAttribute("daejeonData", monthlyChartInfoDto.getWholesaleRegionInfoList().get(4).getMonthlySales());
            model.addAttribute("daejeonLabel", monthlyChartInfoDto.getWholesaleRegionInfoList().get(4).getRegion());
        } catch (IndexOutOfBoundsException e) {

        }

        return "PriceSearch";
    }

    private List<WholesaleInfoDto> getWholesaleMonthlyPrice(List<ResponseEntity<String>> resultMapList) {
        Gson gson = new Gson();
        KamisResponseSingleDto singleDto;
        KamisResponsePluralDto pluralDto;
        // 도매값 대상
        List<WholesaleInfoDto> wholesaleInfoList = new ArrayList<>();
        for (ResponseEntity<String> resultMap : resultMapList) {
            String countyCode;
            try {
                singleDto = gson.fromJson(resultMap.getBody(), KamisResponseSingleDto.class);
                countyCode = singleDto.getCondition().get(0).get(9);
                wholesaleInfoList.add(new WholesaleInfoDto(singleDto.getPrice(), CountyCode.searchCountyCode(countyCode)));
            } catch (JsonSyntaxException e) {
                pluralDto = gson.fromJson(resultMap.getBody(), KamisResponsePluralDto.class);
                countyCode = pluralDto.getCondition().get(0).get(9);
                wholesaleInfoList.add(new WholesaleInfoDto(pluralDto.getPrice().get(0), CountyCode.searchCountyCode(countyCode)));
            }
        }

        return wholesaleInfoList;
    }

    private List<String> createMonthlyRequestParameters(String input) throws IOException {
        List<KamisMonthlyRequestDto> requestDtoList = convert(input);
        List<String> parameters = new ArrayList<>();
        for (KamisMonthlyRequestDto dto : requestDtoList) {
            parameters.add(
                    "&p_yyyy=" + dto.getP_yyyy() + "&"
                            + "p_period=" + dto.getP_period() + "&"
                            + "p_itemcategorycode=" + dto.getP_itemcategorycode() + "&"
                            + "p_itemcode=" + dto.getP_itemcode() + "&"
                            + "p_kindcode=" + dto.getP_kindcode() + "&"
                            + "p_graderank=" + dto.getP_graderank() + "&"
                            + "p_countycode=" + dto.getP_countycode() + "&"
                            + "p_convert_kg_yn=" + dto.getP_convert_kg_yn() + "&"
            );
        }
        return parameters;

    }

    private List<String> createDailyRequestParameters(String input) throws IOException {
        List<KamisDailyRequestDto> requestDtoList = convertForDailyRequest(input);
        List<String> parameters = new ArrayList<>();
        for (KamisDailyRequestDto dto : requestDtoList) {
            parameters.add(
                    "&p_productclscode=" + dto.getP_productclscode() + "&"
                            + "p_startday=" + dto.getP_startday() + "&"
                            + "p_endday=" + dto.getP_endday() + "&"
                            + "p_itemcategorycode=" + dto.getP_itemcategorycode() + "&"
                            + "p_itemcode=" + dto.getP_itemcode() + "&"
                            + "p_kindcode=" + dto.getP_kindcode() + "&"
                            + "p_productrankcode=" + dto.getP_productrankcode() + "&"
                            + "p_countrycode=" + dto.getP_countrycode() + "&"
                            + "p_convert_kg_yn=" + dto.getP_convert_kg_yn() + "&"
            );
        }
        return parameters;
    }

    private WholesaleChartInfoDto createMonthlyChartInfo(List<WholesaleInfoDto> wholesaleInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        int lastItemIdx;
        String[] label = new String[12];
        for (WholesaleInfoDto wholesaleInfoDto : wholesaleInfoList) {
            int[] monthlySales = new int[12];
            WholesaleRegionInfoDto wholesaleRegionInfoDto = new WholesaleRegionInfoDto();

            lastItemIdx = wholesaleInfoDto.getPrice().getItem().size() - 1;
            int idx = 0;
            while (idx <= 11) {
                Item current = wholesaleInfoDto.getPrice().getItem().get(lastItemIdx);

                List<String> currentYearMonthlySalesList = currentYearMonthlySalesList(current);
                for (int monthIdx = 11; monthIdx >= 0 && idx <= 11; monthIdx--) {
                    String sales = currentYearMonthlySalesList.get(monthIdx);
                    if ("-".equals(sales)) continue;
                    label[11 - idx] = current.getYyyy() + "년-" + (monthIdx + 1) + "월";
                    monthlySales[11 - idx] = Integer.parseInt(sales.replace(",", ""));
                    idx++;
                }
                lastItemIdx--;
            }
            wholesaleRegionInfoDto.setRegion(wholesaleInfoDto.getCountyCode().getName());
            wholesaleRegionInfoDto.setMonthlySales(monthlySales);
            wholesaleRegionInfoDtoList.add(wholesaleRegionInfoDto);
        }
        chartInfoDto.setTitle(wholesaleInfoList.get(0).getPrice().getCaption());
        chartInfoDto.setWholesaleRegionInfoList(wholesaleRegionInfoDtoList);
        chartInfoDto.setLabel(label);
        setAvgMaxMin(chartInfoDto);
        return chartInfoDto;
    }

    private void setAvgMaxMin(WholesaleChartInfoDto chartInfoDto) {
        List<WholesaleRegionInfoDto> list = chartInfoDto.getWholesaleRegionInfoList();
        int[] avgArr = new int[12];
        int[] maxArr = new int[12];
        int[] minArr = new int[12];

        for (int i = 0; i < 12; i++) {
            int sum = 0;
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (WholesaleRegionInfoDto dto : list) {
                int monthlySales = dto.getMonthlySales()[i];
                sum += monthlySales;
                if (max < monthlySales) max = monthlySales;
                if (min > monthlySales) min = monthlySales;

            }
            avgArr[i] = sum / list.size();
            maxArr[i] = max;
            minArr[i] = min;
        }
        chartInfoDto.setAvgPrice(avgArr);
        chartInfoDto.setMaxPrice(maxArr);
        chartInfoDto.setMinPrice(minArr);
    }

    private List<String> currentYearMonthlySalesList(Item current) {
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

    private List<KamisMonthlyRequestDto> convert(String input) throws IOException {
        ItemInfoDto itemInfoDto = service.searchInfo(input);

        List<KamisMonthlyRequestDto> wholesaleRequestList = new ArrayList<>();
        for (CountyCode wholesaleCode : WHOLESALE_COUNTY_CODES) {
            KamisMonthlyRequestDto dto = KamisMonthlyRequestDto.builder()
                    .p_itemcategorycode(itemInfoDto.getItemCategoryCode())
                    .p_graderank(itemInfoDto.getGradeRank())
                    .p_itemcode(itemInfoDto.getItemCode())
                    .p_kindcode(itemInfoDto.getKindCode())
                    .p_convert_kg_yn("N")
                    .p_yyyy("2020")
                    .p_period("3")
                    .p_countycode(wholesaleCode.getCode())
                    .build();
            wholesaleRequestList.add(dto);
        }
        return wholesaleRequestList;

    }

    private List<KamisDailyRequestDto> convertForDailyRequest(String input) throws IOException {
        ItemInfoDto itemInfoDto = service.searchInfo(input);
        List<KamisDailyRequestDto> wholesaleRequestList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate fourteenDaysAgo = now.minusDays(14);
        String endDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDay = fourteenDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        for (CountyCode wholesaleCode : WHOLESALE_COUNTY_CODES) {
            KamisDailyRequestDto dto = KamisDailyRequestDto.builder()
                    .p_startday(startDay)
                    .p_endday(endDay)
                    .p_itemcategorycode(itemInfoDto.getItemCategoryCode())
                    .p_productrankcode(itemInfoDto.getProductRank())
                    .p_itemcode(itemInfoDto.getItemCode())
                    .p_kindcode(itemInfoDto.getKindCode())
                    .p_convert_kg_yn("N")
                    .p_countrycode(wholesaleCode.getCode())
                    .p_productclscode("02")
                    .build();
            wholesaleRequestList.add(dto);
        }
        return wholesaleRequestList;

    }

}
