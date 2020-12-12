package com.nongbushim.Helper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

public class APIHelper {

    public final static String MONTHLY_URL = "http://www.kamis.or.kr/service/price/xml.do?action=monthlySalesList";
    public final static String DAILY_URL = "http://www.kamis.or.kr/service/price/xml.do?action=periodProductList";
    public final static String FIXED_PARAMETERS = "p_cert_key=111&p_cert_id=222&p_returntype=json";
    public final static HttpHeaders HTTP_HEADERS;
    public final static HttpEntity<?> HTTP_ENTITY;
    public final static String ACCESS_KEY = "c870db87-9503-48c8-aca3-dee7f28a42ba";

    static {
        HTTP_HEADERS = new HttpHeaders();
        HTTP_HEADERS.add("key", APIHelper.ACCESS_KEY);
        HTTP_ENTITY = new HttpEntity<>(APIHelper.HTTP_HEADERS);
    }

    public static List<ResponseEntity<String>> getResponsesFromOpenAPI(List<String> requestParameters, String monthlyUrl) {
        UriComponents uri;
        RestTemplate restTemplate = new RestTemplate();
        List<ResponseEntity<String>> resultMap = new ArrayList<>();
        for (String parameter : requestParameters) {
            uri = UriComponentsBuilder.fromHttpUrl(monthlyUrl + parameter + FIXED_PARAMETERS).build();
            resultMap.add(restTemplate.exchange(uri.toString(), HttpMethod.GET, HTTP_ENTITY, String.class));
        }
        return resultMap;
    }
}
