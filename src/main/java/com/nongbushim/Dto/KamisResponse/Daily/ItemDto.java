package com.nongbushim.Dto.KamisResponse.Daily;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String itemname;
    private String kindname;
    private String countyname;
    private String marketname;
    private String yyyy;
    private String regday;
    private String price;
}
