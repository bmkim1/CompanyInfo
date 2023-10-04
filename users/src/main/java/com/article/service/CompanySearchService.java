package com.article.service;

import com.article.entity.CompanyInfo;
import com.article.repository.CompanyInfoRepository;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CompanySearchService {


    private final CompanyInfoRepository companyInfoRepository;
    private final RestTemplate restTemplate;

    public CompanySearchService(CompanyInfoRepository companyInfoRepository, RestTemplate restTemplate) {
        this.companyInfoRepository = companyInfoRepository;
        this.restTemplate = restTemplate;
    }

    // 회사 정보를 가져오는 로직
    public String processUsersSequentially() {
        Page<CompanyInfo> companyPage = companyInfoRepository.findAll(PageRequest.of(0, 20, Sort.by(Sort.Order.asc("idSeq"))));

        List<CompanyInfo> companyInfos = companyPage.getContent();
        StringBuilder resultBuilder = new StringBuilder();

        for (CompanyInfo companyInfo : companyInfos) {
            try {
                if (companyInfo.getTermination() == null) {
                    companyInfo.setTermination("null");
                }
                if (companyInfo.getCorporateStatus() == null) {
                    companyInfo.setCorporateStatus("null");
                }

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id_seq", companyInfo.getIdSeq());
                jsonObject.put("companyName", companyInfo.getCompanyName());
                jsonObject.put("ceoName", companyInfo.getCeoName());
                jsonObject.put("termination", companyInfo.getTermination());
                jsonObject.put("corporateStatus", companyInfo.getCorporateStatus());
                String jsonResult = jsonObject.toString();
                System.out.println("Sending JSON: " + jsonResult);

                String postUrl = "http://localhost:8085/api/article/search";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonResult, headers);

                ResponseEntity<String> responseEntity = restTemplate.exchange(
                        postUrl,
                        HttpMethod.POST,
                        requestEntity,
                        String.class
                );

                resultBuilder.append(jsonResult).append("\n");

                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Error";
            }
        }

        return resultBuilder.toString();
    }

}