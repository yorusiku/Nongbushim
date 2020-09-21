package com.nongbushim.Controller;

import com.nongbushim.Service.AutoComplete.AutoCompleteService;
import com.nongbushim.Service.AutoComplete.AutoCompleteServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Controller
public class AutoCompleteController {
    private final AutoCompleteService service;

    public AutoCompleteController(AutoCompleteServiceImpl service) {
        this.service = service;
    }

    @RequestMapping("/autoComplete")
    @ResponseBody
    public List<String> autoComplete(@RequestParam(value = "term", required = false, defaultValue = "") String term) {
        List<String> suggestions = new LinkedList<>();
        try {
            return service.searchAutoCompleteTarget(term);
        } catch (IOException e) {
        }
        return suggestions;
    }
}
