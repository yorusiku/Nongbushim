package com.nongbushim.Dto.KamisRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KamisDailyRequestDto extends KamisRequestDto {
    private String p_startday;
    private String p_endday;
    private String p_productclscode;
    private String p_productrankcode;
    private String p_countrycode;


}
