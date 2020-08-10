package com.nongbushim.Dto.KamisResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KamisResponseDto {
    private List<List<String>> condition;

    private String error_code;
}
