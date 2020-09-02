package com.nongbushim.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nongbushim.Dto.*;
import com.nongbushim.Dto.KamisRequest.*;
import com.nongbushim.Dto.KamisResponse.Daily.ConditionDto;
import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.KamisResponse.Monthly.MonthlyItemDto;
import com.nongbushim.Dto.KamisResponse.Monthly.PriceDto;
import com.nongbushim.Enum.CountyCode;
import com.nongbushim.SearchService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
    private static String CHART_TITLE;
    private static final int THIS_YEAR = LocalDate.now().getYear();
    private static final String NO_SEARCH_MONTHLY_RESULT = THIS_YEAR + "년도에 해당하는 데이터가 없습니다";
    private static final String NO_SEARCH_DAILY_RESULT = "검색조건에 해당하는 데이터가 없습니다";

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

        List<WholesaleMonthlyInfoDto> wholesaleMonthlyInfoList = getWholesaleMonthlyPrice(resultMap);
        if (wholesaleMonthlyInfoList.isEmpty()) {
            model.addAttribute("monthlyNoSearchResult", NO_SEARCH_MONTHLY_RESULT);
            return "PriceSearch";
        }
        WholesaleChartInfoDto monthlyChartInfoDto = createMonthlyChartInfo(wholesaleMonthlyInfoList);

        List<WholesaleDailyInfoDto> wholesaleDailyInfoList = getWholesaleDailyPrice(resultMap2);
        if (wholesaleDailyInfoList.isEmpty()) {
            model.addAttribute("dailyNoSearchResult", NO_SEARCH_DAILY_RESULT);
            return "PriceSearch";
        }
        WholesaleChartInfoDto dailyChartInfoDto = createDailyChartInfo(wholesaleDailyInfoList);

        model.addAttribute("monthlyChartInfo", monthlyChartInfoDto);
        model.addAttribute("dailyChartInfo", dailyChartInfoDto);

        return "PriceSearch";
    }

    private WholesaleChartInfoDto createDailyChartInfo(List<WholesaleDailyInfoDto> wholesaleDailyInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        List<String> label = createLabel(wholesaleDailyInfoList);

        for (WholesaleDailyInfoDto dto : wholesaleDailyInfoList) {
            WholesaleRegionInfoDto wholesaleRegionInfoDto = new WholesaleRegionInfoDto();

            List<Integer> dailyPrices = new ArrayList<>();
            int len = dto.getDailyItemList().size();
            for (int i = 0; i < len; i++) {
                DailyItemDto currentItem = dto.getDailyItemList().get(i);
                dailyPrices.add(Integer.parseInt(currentItem.getPrice().replace(",", "")));
            }
            wholesaleRegionInfoDto.setRegion(dto.getCountyCode().getName());
            wholesaleRegionInfoDto.setPrices(dailyPrices);
            setColour(wholesaleRegionInfoDto);
            wholesaleRegionInfoDtoList.add(wholesaleRegionInfoDto);
        }
        chartInfoDto.setTitle(CHART_TITLE);
        chartInfoDto.setWholesaleRegionInfoList(wholesaleRegionInfoDtoList);
        chartInfoDto.setLabel(label);
        setAvgMaxMin(chartInfoDto);
        return chartInfoDto;
    }

    private List<String> createLabel(List<WholesaleDailyInfoDto> wholesaleDailyInfoList) {
        List<String> label = new ArrayList<>();
        for (DailyItemDto dto : wholesaleDailyInfoList.get(0).getDailyItemList())
            label.add(dto.getRegday());
        return label;
    }

    private List<WholesaleDailyInfoDto> getWholesaleDailyPrice(List<ResponseEntity<String>> resultMapList) {
        // 도매값 대상
        List<WholesaleDailyInfoDto> wholesaleInfoList = new ArrayList<>();
        for (ResponseEntity<String> resultMap : resultMapList) {
            JSONObject responseBody = new JSONObject(resultMap.getBody());
            ObjectMapper mapper = new ObjectMapper();
            try {
                ConditionDto condition = mapper.readValue(responseBody.getJSONArray("condition").getJSONObject(0).toString(), ConditionDto.class);
                String countyCode = condition.getP_countycode();

                JSONArray jsonArray = responseBody.getJSONObject("data").getJSONArray("item");
                List<DailyItemDto> dailyItemList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject current = jsonArray.getJSONObject(i);
                    String itemName = current.get("itemname").toString();
                    if ("[]".equals(itemName)) continue;
                    DailyItemDto dailyItemDto = mapper.readValue(current.toString(), DailyItemDto.class);
                    dailyItemList.add(dailyItemDto);
                }
                wholesaleInfoList.add(new WholesaleDailyInfoDto(dailyItemList, CountyCode.searchCountyCode(countyCode)));

            } catch (JsonProcessingException e) {
            } catch (JSONException e) {
                // 데이터가 없는 경우
                if (e.getMessage().equals("JSONObject[\"data\"] is not a JSONObject."))
                    return wholesaleInfoList;
            }

        }

        return wholesaleInfoList;
    }

    private List<WholesaleMonthlyInfoDto> getWholesaleMonthlyPrice(List<ResponseEntity<String>> resultMapList) {
        List<WholesaleMonthlyInfoDto> wholesaleInfoList = new ArrayList<>();
        for (ResponseEntity<String> resultMap : resultMapList) {
            JSONObject responseBody = new JSONObject(resultMap.getBody());
            ObjectMapper mapper = new ObjectMapper();

            WholesaleMonthlyInfoDto res = new WholesaleMonthlyInfoDto();
            try {
                List<String> condition = Arrays.asList(mapper.readValue(responseBody.getJSONArray("condition").getJSONArray(0).toString(), String[].class));

                //레스폰스에 도, 소매 둘다 있는 경우
                JSONArray priceArr = responseBody.optJSONArray("price");
                //레스폰스에 도매만 있는 경우
                JSONObject priceObj = responseBody.optJSONObject("price");

                JSONArray itemArr = null;
                JSONObject itemObj = null;
                JSONObject wholesaleObject;
                List<MonthlyItemDto> item;

                // 도매값 대상
                wholesaleObject = (priceArr != null) ? priceArr.getJSONObject(0) : priceObj;
                //도매 가격정보 존재
                itemArr = wholesaleObject.optJSONArray("item");
                //도매 가격정보 부족(false positive)
                itemObj = wholesaleObject.optJSONObject("item");
                String productclscode = wholesaleObject.getString("productclscode");
                String caption = wholesaleObject.getString("caption");
                String countyCode = condition.get(condition.size() - 1);

                res.setCountyCode(CountyCode.searchCountyCode(countyCode));
                PriceDto priceDto = new PriceDto();
                priceDto.setProductclscode(productclscode);
                priceDto.setCaption(caption);
                if (itemArr != null) {
                    item = Arrays.asList(mapper.readValue(itemArr.toString(), MonthlyItemDto[].class));
                } else if (itemObj != null) {
                    item = Collections.singletonList(mapper.readValue(itemObj.toString(), MonthlyItemDto.class));
                } else {
                    // 도, 소매 가격정보 없음
                    return new ArrayList<>();
                }
                priceDto.setItem(item);
                res.setPrice(priceDto);
                wholesaleInfoList.add(res);

            } catch (JsonProcessingException e) {
            }
        }

        int invalidCount = 0;
        for (WholesaleMonthlyInfoDto dto : wholesaleInfoList) {
            List<MonthlyItemDto> item = dto.getPrice().getItem();
            int lastIdx = item.size() - 1;
            if (THIS_YEAR != Integer.parseInt(item.get(lastIdx).getYyyy())) invalidCount++;
        }
        if (invalidCount == wholesaleInfoList.size())
            wholesaleInfoList = new ArrayList<>();
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

    private WholesaleChartInfoDto createMonthlyChartInfo(List<WholesaleMonthlyInfoDto> wholesaleInfoList) {
        WholesaleChartInfoDto chartInfoDto = new WholesaleChartInfoDto();
        List<WholesaleRegionInfoDto> wholesaleRegionInfoDtoList = new ArrayList<>();
        int lastItemIdx;
        String[] label = new String[12];
        for (WholesaleMonthlyInfoDto wholesaleMonthlyInfoDto : wholesaleInfoList) {
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
                    label[11 - idx] = createLabel(current, monthIdx);
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
        CHART_TITLE = wholesaleInfoList.get(0).getPrice().getCaption();
        chartInfoDto.setTitle(CHART_TITLE);
        chartInfoDto.setWholesaleRegionInfoList(wholesaleRegionInfoDtoList);
        chartInfoDto.setLabel(Arrays.asList(label));
        setAvgMaxMin(chartInfoDto);
        return chartInfoDto;
    }

    private void setColour(WholesaleRegionInfoDto dto) {
        Random r = new Random();
        int[] randomNumbers = r.ints(3, 0, 200).toArray();
        String rgba = "rgba(" + randomNumbers[0] + "," + randomNumbers[1] + "," + randomNumbers[2] + ",1)";
        dto.setBackgroundColor(rgba);
        dto.setBorderColor(rgba);
    }

    private String createLabel(MonthlyItemDto current, int monthIdx) {
        String month = (monthIdx + 1) / 10 == 1 ? String.valueOf(monthIdx + 1) : "0" + (monthIdx + 1);
        return current.getYyyy() + "/" + month;
    }

    private void setAvgMaxMin(WholesaleChartInfoDto chartInfoDto) {
        List<WholesaleRegionInfoDto> list = chartInfoDto.getWholesaleRegionInfoList();
        List<Integer> avgArr = new ArrayList<>();
        List<Integer> maxArr = new ArrayList<>();
        List<Integer> minArr = new ArrayList<>();

        int len = list.get(0).getPrices().size();
        for (int i = 0; i < len; i++) {
            int sum = 0;
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (WholesaleRegionInfoDto dto : list) {
                int monthlySales;
                try {
                    monthlySales = dto.getPrices().get(i);
                    sum += monthlySales;
                    if (max < monthlySales) max = monthlySales;
                    if (min > monthlySales) min = monthlySales;
                } catch (IndexOutOfBoundsException e) {
                }
            }
            avgArr.add(sum / list.size());
            maxArr.add(max);
            minArr.add(min);
        }
        chartInfoDto.setAvgPrice(avgArr);
        chartInfoDto.setMaxPrice(maxArr);
        chartInfoDto.setMinPrice(minArr);
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
                    .p_period("10")
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
