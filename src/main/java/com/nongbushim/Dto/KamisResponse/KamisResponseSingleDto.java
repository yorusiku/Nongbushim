package com.nongbushim.Dto.KamisResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 * API결과값이 도매값만 포함하고 있을 경우 이 클래스 사용
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KamisResponseSingleDto extends KamisResponseDto {

    private Price price;
}
