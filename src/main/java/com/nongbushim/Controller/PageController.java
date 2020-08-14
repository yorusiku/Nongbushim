package com.nongbushim.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping(value = {"/warehouse", "/Warehouse.html"})
    public String warehouse() {
        return "Warehouse";
    }

    @GetMapping(value = {"/whoarewe", "/WhoAreWe.html"})
    public String whoAreWe() {
        return "WhoAreWe";
    }
}
