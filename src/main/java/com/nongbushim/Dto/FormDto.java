package com.nongbushim.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 자동완성을 통해 검색한 키워드
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDto {
    private String text;
}
