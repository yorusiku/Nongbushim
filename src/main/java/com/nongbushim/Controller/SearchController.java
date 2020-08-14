package com.nongbushim.Controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nongbushim.Dto.*;
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
import java.util.*;


@Controller
public class SearchController {
    private final static String URL = "http://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList";
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
        List<String> parameters;
        try {
            parameters = createParameters(input);
        } catch (ArrayIndexOutOfBoundsException e) {
            return "PriceSearch";
        }

        UriComponents uri;
        RestTemplate restTemplate = new RestTemplate();
        List<ResponseEntity<String>> resultMap = new ArrayList<>();
        for (String parameter : parameters) {
            uri = UriComponentsBuilder.fromHttpUrl(URL + parameter + FIXED_PARAMETERS).build();
            resultMap.add(restTemplate.exchange(uri.toString(), HttpMethod.GET, HTTP_ENTITY, String.class));
        }

        List<WholesaleInfoDto> wholesaleInfoList = getWholesalePrice(resultMap);
        WholesaleChartInfoDto chartInfoDto;
        try {
            chartInfoDto = createChartInfo(wholesaleInfoList);
            model.addAttribute("chartInfo", chartInfoDto);

            model.addAttribute("seoulData", chartInfoDto.getWholesaleRegionInfoList().get(0).getMonthlySales());
            model.addAttribute("seoulLabel", chartInfoDto.getWholesaleRegionInfoList().get(0).getRegion());

            model.addAttribute("busanData", chartInfoDto.getWholesaleRegionInfoList().get(1).getMonthlySales());
            model.addAttribute("busanLabel", chartInfoDto.getWholesaleRegionInfoList().get(1).getRegion());

            model.addAttribute("daeguData", chartInfoDto.getWholesaleRegionInfoList().get(2).getMonthlySales());
            model.addAttribute("daeguLabel", chartInfoDto.getWholesaleRegionInfoList().get(2).getRegion());

            model.addAttribute("gwangjuData", chartInfoDto.getWholesaleRegionInfoList().get(3).getMonthlySales());
            model.addAttribute("gwangjuLabel", chartInfoDto.getWholesaleRegionInfoList().get(3).getRegion());

            model.addAttribute("daejeonData", chartInfoDto.getWholesaleRegionInfoList().get(4).getMonthlySales());
            model.addAttribute("daejeonLabel", chartInfoDto.getWholesaleRegionInfoList().get(4).getRegion());
        } catch (IndexOutOfBoundsException e) {

        }

        return "PriceSearch";
    }

    private List<WholesaleInfoDto> getWholesalePrice(List<ResponseEntity<String>> resultMapList) {
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

    private List<String> createParameters(String input) throws IOException {
        List<KamisRequestDto> requestDtoList = convert(input);
        List<String> parameters = new ArrayList<>();
        for (KamisRequestDto dto : requestDtoList) {
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

    private WholesaleChartInfoDto createChartInfo(List<WholesaleInfoDto> wholesaleInfoList) {
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
            wholesaleRegionInfoDto.setTableDto(createTableInfo(wholesaleInfoDto));
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
        IntSummaryStatistics monthAgoStatistics = list.stream().mapToInt(dto -> dto.getTableDto().getMonthAgoPrice()).summaryStatistics();
        IntSummaryStatistics yearAgoStatistics = list.stream().mapToInt(dto -> dto.getTableDto().getYearAgoPrice()).summaryStatistics();
        chartInfoDto.setAvgMonthAgoPrice((int) monthAgoStatistics.getAverage());
        chartInfoDto.setAvgYearAgoPrice((int) yearAgoStatistics.getAverage());
        chartInfoDto.setMaxMonthAgoPrice(monthAgoStatistics.getMax());
        chartInfoDto.setMaxYearAgoPrice(yearAgoStatistics.getMax());
        chartInfoDto.setMinMonthAgoPrice(monthAgoStatistics.getMin());
        chartInfoDto.setMinYearAgoPrice(yearAgoStatistics.getMin());
    }

    private WholesalePastSalesDto createTableInfo(WholesaleInfoDto wholesaleInfoDto) {
        WholesalePastSalesDto wholesalePastSalesDto = new WholesalePastSalesDto();
        int lastItemIdx = wholesaleInfoDto.getPrice().getItem().size() - 1;
        Item thisYearItem = wholesaleInfoDto.getPrice().getItem().get(lastItemIdx);
        Item lastYearItem = wholesaleInfoDto.getPrice().getItem().get(lastItemIdx - 1);
        List<String> thisYearMonthlySalesList = currentYearMonthlySalesList(thisYearItem);
        List<String> lastYearMonthlySalesList = currentYearMonthlySalesList(lastYearItem);
        LocalDate today = LocalDate.now();
        int lastMonthIdx = today.getMonthValue() - 2;

        wholesalePastSalesDto.setMonthAgoPrice(Integer.parseInt(thisYearMonthlySalesList.get(lastMonthIdx).replace(",", "")));
        wholesalePastSalesDto.setYearAgoPrice(Integer.parseInt(lastYearMonthlySalesList.get(lastMonthIdx).replace(",", "")));

        return wholesalePastSalesDto;
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

    private List<KamisRequestDto> convert(String input) throws IOException {
        ItemInfoDto itemInfoDto = service.searchInfo(input);

        List<KamisRequestDto> wholesaleRequestList = new ArrayList<>();
        for (CountyCode wholesaleCode : WHOLESALE_COUNTY_CODES) {
            KamisRequestDto dto = KamisRequestDto.builder()
                    .p_yyyy("2020")
                    .p_period("3")
                    .p_itemcategorycode(itemInfoDto.getItemCategoryCode())
                    .p_graderank(itemInfoDto.getGradeRank())
                    .p_itemcode(itemInfoDto.getItemCode())
                    .p_kindcode(itemInfoDto.getKindCode())
                    .p_convert_kg_yn("N")
                    .p_countycode(wholesaleCode.getCode())
                    .build();
            wholesaleRequestList.add(dto);
        }
        return wholesaleRequestList;

    }

}