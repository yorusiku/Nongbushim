package com.nongbushim.Service.Search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nongbushim.Dto.ItemInfoDto;
import com.nongbushim.Dto.KamisRequest.KamisDailyRequestDto;
import com.nongbushim.Dto.KamisResponse.Daily.ConditionDto;
import com.nongbushim.Dto.KamisResponse.Daily.DailyItemDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleDailyInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Enum.CountyCode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service("dailySearchService")
public class DailySearchServiceImpl extends SearchService {

    @Override
    public List<String> createOpenAPIRequestParameters(String input) throws IOException {
        List<KamisDailyRequestDto> requestDtoList = convert(input);
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

    @Override
    public List<WholesaleInfoDto> getWholesalePrice(List<ResponseEntity<String>> resultMapList) {
        List<WholesaleInfoDto> wholesaleInfoList = new ArrayList<>();
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
                wholesaleInfoList.add(WholesaleDailyInfoDto.builder()
                        .dailyItemList(dailyItemList)
                        .countyCode(CountyCode.searchCountyCode(countyCode))
                        .build());

            } catch (JsonProcessingException e) {
            } catch (JSONException e) {
                // 데이터가 없는 경우
            }

        }

        return wholesaleInfoList;
    }

    private List<KamisDailyRequestDto> convert(String input) throws IOException {
        ItemInfoDto itemInfoDto = searchInfo(input);
        List<KamisDailyRequestDto> wholesaleRequestList = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate oneYearAgo = now.minusYears(1);
        String endDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String startDay = oneYearAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
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
