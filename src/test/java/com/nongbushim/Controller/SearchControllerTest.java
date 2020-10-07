package com.nongbushim.Controller;

import com.nongbushim.Dto.FormDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static InputStream resource;
    private static BufferedReader reader;

    @BeforeAll
    public static void setup() throws IOException {
        resource = new ClassPathResource("static/list.txt").getInputStream();
        reader = new BufferedReader(new InputStreamReader(resource));
    }

    @Test
    public void whenSearchAllItem_shouldReturn200() throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
                mockMvc.perform(post("/pricesearch")
                        .flashAttr("form", new FormDto(line)))
                        .andExpect(status().isOk());
        }
    }

    @AfterAll
    public static void done() throws IOException {
        resource.close();
        reader.close();
    }
}
