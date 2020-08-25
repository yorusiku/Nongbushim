package com.nongbushim.Dto.KamisResponse.Monthly;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceDto {
    private String productclscode;

    private String caption;

    private List<MonthlyItemDto> item;

}
