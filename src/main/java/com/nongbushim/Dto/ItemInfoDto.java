package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemInfoDto {
    private String itemCategoryCode;
    private String itemCode;
    private String kindCode;
    private String gradeRank;
}
