package com.nongbushim.Service.Search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nongbushim.Dto.ItemInfoDto;
import com.nongbushim.Dto.KamisRequest.KamisMonthlyRequestDto;
import com.nongbushim.Dto.KamisResponse.Monthly.MonthlyItemDto;
import com.nongbushim.Dto.KamisResponse.Monthly.PriceDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleMonthlyInfoDto;
import com.nongbushim.Enum.CountyCode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service("monthlySearchService")
public class MonthlySearchServiceImpl extends SearchService {
    private static final int THIS_YEAR = LocalDate.now().getYear();

    @Override
    public List<String> createOpenAPIRequestParameters(String input) throws IOException {
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

    @Override
    public List<WholesaleInfoDto> getWholesalePrice(List<ResponseEntity<String>> resultMapList) {
        List<WholesaleInfoDto> wholesaleInfoList = new ArrayList<>();
        for (ResponseEntity<String> resultMap : resultMapList) {
            JSONObject responseBody = new JSONObject(resultMap.getBody());
            ObjectMapper mapper = new ObjectMapper();

            WholesaleMonthlyInfoDto res = new WholesaleMonthlyInfoDto();
            try {
                String test = responseBody.getJSONArray("condition").get(0).toString();
                List<Object> condition = Arrays.asList(mapper.readValue(test, Object[].class));

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
                String countyCode = (String) condition.get(condition.size() - 1);

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
                    continue;
                }
                priceDto.setItem(item);
                res.setPrice(priceDto);
                wholesaleInfoList.add(res);

            } catch (JsonProcessingException e) {
            }
        }

        int invalidCount = 0;
        for (WholesaleInfoDto dto : wholesaleInfoList) {
            WholesaleMonthlyInfoDto monthlyInfo = (WholesaleMonthlyInfoDto) dto;
            List<MonthlyItemDto> item = monthlyInfo.getPrice().getItem();
            int lastIdx = item.size() - 1;
            if (THIS_YEAR != Integer.parseInt(item.get(lastIdx).getYyyy())) invalidCount++;

        }
        if (invalidCount == wholesaleInfoList.size())
            wholesaleInfoList = new ArrayList<>();
        return wholesaleInfoList;
    }

    private List<KamisMonthlyRequestDto> convert(String input) throws IOException {
        ItemInfoDto itemInfoDto = searchInfo(input);

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
}
