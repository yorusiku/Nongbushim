package com.nongbushim.Dto.KamisRequest;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class KamisRequestDto {
    private String p_itemcategorycode;
    private String p_kindcode;
    private String p_convert_kg_yn;
    private String p_itemcode;
}
