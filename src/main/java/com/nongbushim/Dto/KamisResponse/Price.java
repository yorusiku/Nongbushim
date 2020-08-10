package com.nongbushim.Dto.KamisResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Price {
    private String productclscode;

    private String caption;

    private List<Item> item;

}
