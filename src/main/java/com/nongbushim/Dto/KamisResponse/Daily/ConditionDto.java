package com.nongbushim.Dto.KamisResponse.Daily;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionDto {
    private String p_startday;
    private String p_endday;
    private String p_itemcategorycode;
    private String p_itemcode;
    private String p_kindcode;
    private String p_productrankcode;
    private String p_countycode;
    private String p_convert_kg_yn;
    private String p_key;
    private String p_id;
    private String p_returntype;
}
