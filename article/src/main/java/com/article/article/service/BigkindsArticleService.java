package com.article.article.service;

import com.article.article.dto.BigkindsRequestParam;
import com.article.article.dto.BigkindsResponse;
import com.article.article.dto.CompanySearchParam;
import com.article.article.entity.Article;
import com.article.article.entity.ArticleCnt;
import com.article.article.mapper.ArticleMapper;
import com.article.article.repository.ArticleCntRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class BigkindsArticleService {

    @Value("${bigkinds.api.url}")
    private String bigkindsUrl;

    private static final Logger log = LoggerFactory.getLogger(BigkindsArticleService.class);
    private final ArticleCntRepository articleCntRepository;
    private final ArticleMapper articleMapper;
    private final RestTemplate restTemplate;

    public BigkindsArticleService(ArticleCntRepository articleCntRepository, ArticleMapper articleMapper, RestTemplate restTemplate) {
        this.articleCntRepository = articleCntRepository;
        this.articleMapper = articleMapper;
        this.restTemplate = restTemplate;
    }

    // 빅카인즈 뉴스 검색로직
    @Transactional
    public List<Article> searchBigkindsArticle (CompanySearchParam searchParam) throws JsonProcessingException {
        List<Article> articleList = new ArrayList<>();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        BigkindsRequestParam requestParam = new BigkindsRequestParam(searchParam.getCompanyName() + " " + searchParam.getCeoName());
        if (articleCntRepository.findFirstByidSeqOrderByArticleYMDDesc(searchParam.getId_seq()) != null)  {
            ArticleCnt latestArticleCnt = articleCntRepository.findFirstByidSeqOrderByArticleYMDDesc(searchParam.getId_seq());
            LocalDate latestDate = latestArticleCnt.getArticleYMD();
            requestParam.setStartDate(latestDate);
        }

        HttpEntity<BigkindsRequestParam> requestEntity = new HttpEntity<>(requestParam, headers);

        // api 호출
        ResponseEntity<BigkindsResponse> response = restTemplate.exchange(bigkindsUrl, HttpMethod.POST, requestEntity, BigkindsResponse.class);
        BigkindsResponse bigkindsResponse = response.getBody();

        // 빅카인즈 전체 기사 수 확인
        int total = bigkindsResponse.getReturnObject().getTotalHits();

        if (bigkindsResponse != null && bigkindsResponse.getReturnObject() != null && bigkindsResponse.getReturnObject().getDocuments() != null) {
            for (BigkindsResponse.Document document : bigkindsResponse.getReturnObject().getDocuments()) {

                // 수집한 기사를 리스트에 추가
                Article sendBigkindsArticle = articleMapper.bigKindsResponseToArticle(document, searchParam);
                articleList.add(sendBigkindsArticle);

            }

            log.info("빅카인즈 기사 수집 총 {}건 검색 되었습니다.", total);
        }
        return articleList;
    }


}
