package com.nongbushim.Dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KamisRequestDto {
    private String p_yyyy;
    private String p_period;
    private String p_itemcategorycode;
    private String p_kindcode;
    private String p_graderank;
    private String p_countycode;
    private String p_convert_kg_yn;
    private String p_itemcode;
}
