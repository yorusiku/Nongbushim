package com.nongbushim.Dto.KamisResponse.Daily;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRawValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyItemDto {
    private String itemname;
    private String kindname;
    private String countyname;
    private String marketname;
    private String yyyy;
    private String regday;
    private String price;
}
