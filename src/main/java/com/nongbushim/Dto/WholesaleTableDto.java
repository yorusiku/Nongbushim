package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WholesaleTableDto {
    private int monthAgoPrice;
    private int yearAgoPrice;
}
