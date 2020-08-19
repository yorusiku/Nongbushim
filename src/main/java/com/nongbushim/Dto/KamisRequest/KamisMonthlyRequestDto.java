package com.nongbushim.Dto.KamisRequest;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KamisMonthlyRequestDto extends KamisRequestDto {
    private String p_graderank;
    private String p_yyyy;
    private String p_period;
    private String p_countycode;

}
