package com.nongbushim.Service.Search;

import com.nongbushim.Dto.ItemInfoDto;
import com.nongbushim.Dto.WholesaleInfo.WholesaleInfoDto;
import com.nongbushim.Enum.CountyCode;
import com.nongbushim.Enum.ItemCode;
import com.nongbushim.Enum.ProductRankCode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public abstract class SearchService {
    protected final static Set<CountyCode> WHOLESALE_COUNTY_CODES = EnumSet.of(CountyCode.COUNTY_CODE_1101, CountyCode.COUNTY_CODE_2100, CountyCode.COUNTY_CODE_2200, CountyCode.COUNTY_CODE_2401, CountyCode.COUNTY_CODE_2501);

    abstract public List<String> createOpenAPIRequestParameters(String input) throws IOException;

    abstract public List<WholesaleInfoDto> getWholesalePrice(List<ResponseEntity<String>> resultMapList);

    protected ItemInfoDto searchInfo(String input) throws IOException {
        String[] terms = input.split(" ");
        int lastIdx = terms.length-1;
        String itemName = terms[0];
        String kind = terms[1];
        String grade = terms[lastIdx];
        if (lastIdx > 2) {
            // input이 3단어보다 긴 경우
            StringBuilder sb = new StringBuilder(kind);
            while (--lastIdx > 1) {
                sb.append(" ").append(terms[lastIdx]);
            }
            kind = sb.toString();
        }
        return createItemInfoDto(itemName, kind, grade);
    }

    private ItemInfoDto createItemInfoDto(String itemName, String kind, String grade) throws IOException {
        ItemCode itemCode = ItemCode.searchCode(itemName);
        ProductRankCode rank = ProductRankCode.searchRank(grade);
        ItemInfoDto itemInfoDto = new ItemInfoDto();
        itemInfoDto.setItemCode(itemCode.getCode());
        itemInfoDto.setItemCategoryCode(itemCode.getItemCategoryCode().getCode());
        itemInfoDto.setGradeRank(rank.getGradeRank());
        itemInfoDto.setProductRank(rank.getProduceRank());
        itemInfoDto.setKindCode(searchKindCode(itemName + " " + kind));
        return itemInfoDto;
    }

    protected String searchKindCode(String item) throws IOException {
        InputStream resource = new ClassPathResource("static/listWithKindcode.txt").getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));

        String line;
        while((line = reader.readLine()) != null){
            if (line.substring(3).equals(item)) break;
        }
        String kindCode = line.substring(0,2);
        return kindCode;
    }

}
