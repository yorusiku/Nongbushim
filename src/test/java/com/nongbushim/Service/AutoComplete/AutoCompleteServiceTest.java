package com.nongbushim.Service.AutoComplete;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AutoCompleteServiceTest {

    @Autowired
    private AutoCompleteServiceImpl service;

    List<String> garlicList1 = new ArrayList<>();
    List<String> garlicList2 = new ArrayList<>();
    static InputStream resource;
    static BufferedReader reader;

    @BeforeEach
    public void init() throws IOException {
        resource = new ClassPathResource("static/list.txt").getInputStream();
        reader = new BufferedReader(new InputStreamReader(resource));
        garlicList1.add("피마늘 햇난지(대서) 상품");
        garlicList1.add("피마늘 햇난지(대서) 중품");
        garlicList1.add("피마늘 난지(대서) 상품");
        garlicList1.add("피마늘 난지(대서) 중품");
        garlicList1.add("피마늘 햇난지(남도) 상품");
        garlicList1.add("피마늘 햇난지(남도) 중품");
        garlicList1.add("피마늘 난지(남도) 상품");
        garlicList1.add("피마늘 난지(남도) 중품");
        garlicList1.add("피마늘 한지1접 상품");
        garlicList1.add("피마늘 한지1접 중품");
        garlicList1.add("피마늘 난지1접 상품");
        garlicList1.add("피마늘 난지1접 중품");
        garlicList1.add("피마늘 한지 상품");
        garlicList1.add("피마늘 한지 중품");
        garlicList1.add("피마늘 난지 상품");
        garlicList1.add("피마늘 난지 중품");
        garlicList1.add("피마늘 햇한지1접 상품");
        garlicList1.add("피마늘 햇한지1접 중품");
        garlicList1.add("피마늘 햇난지1접 상품");
        garlicList1.add("피마늘 햇난지1접 중품");
        garlicList1.add("피마늘 햇한지 상품");
        garlicList1.add("피마늘 햇한지 중품");
        garlicList1.add("피마늘 햇난지 상품");
        garlicList1.add("피마늘 햇난지 중품");
        garlicList2.add("깐마늘(국산) 깐마늘(국산) 상품");
        garlicList2.add("깐마늘(국산) 깐마늘(국산) 중품");
        garlicList2.add("깐마늘(국산) 깐마늘(대서) 상품");
        garlicList2.add("깐마늘(국산) 깐마늘(대서) 중품");
        garlicList2.add("깐마늘(국산) 햇깐마늘(대서) 상품");
        garlicList2.add("깐마늘(국산) 햇깐마늘(대서) 중품");
        garlicList2.add("깐마늘(국산) 깐마늘(남도) 상품");
        garlicList2.add("깐마늘(국산) 깐마늘(남도) 중품");
        garlicList2.add("깐마늘(국산) 햇깐마늘(남도) 상품");
        garlicList2.add("깐마늘(국산) 햇깐마늘(남도) 중품");
        garlicList2.add("깐마늘(수입) 깐마늘(수입) 중품");
    }

    @Test
    void whenInputIsCommonName_thenReturnAllItems() throws IOException {
        String term = "마늘";
        List<String> expected = new LinkedList<>();
        expected.addAll(garlicList1);
        expected.addAll(garlicList2);
        List<String> actual = service.searchAutoCompleteTarget(term);
        assertEquals(expected, actual);
    }

    @Test
    void whenInputIsSpecificName_thenReturnSpecificItems() throws IOException {
        String term = "깐마늘";
        List<String> expected = garlicList2;
        List<String> actual = service.searchAutoCompleteTarget(term);
        assertEquals(expected, actual);
    }

    @Test
    void whenInputIsInvalidName_thenReturnNothing() throws IOException {
        String term = "농부심";
        List<String> expected = new LinkedList<>();
        List<String> actual = service.searchAutoCompleteTarget(term);
        assertEquals(expected, actual);
    }

    @AfterAll
    static void done() throws IOException {
        resource.close();
        reader.close();
    }
}