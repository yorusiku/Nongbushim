package com.nongbushim.Service.AutoComplete;

import java.io.IOException;
import java.util.List;

public interface AutoCompleteService {
    List<String> searchAutoCompleteTarget(String term) throws IOException;
}
