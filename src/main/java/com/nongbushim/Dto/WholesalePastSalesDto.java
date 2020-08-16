package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholesalePastSalesDto {
    private int monthAgoPrice;
    private int yearAgoPrice;
}
