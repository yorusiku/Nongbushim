package com.nongbushim.Service.AutoComplete;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutoCompleteServiceImpl implements AutoCompleteService{
    @Override
    public List<String> searchAutoCompleteTarget(String term) throws IOException {
        InputStream resource = new ClassPathResource("static/list.txt").getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource));

        return reader.lines().filter(s -> s.contains(term)).collect(Collectors.toList());
    }
}
