package com.example.demo.dividend.service;

import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.Dividend;
import com.example.demo.dividend.model.ScrapedResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyService companyService;
    private final DividendService dividendService;

    @Cacheable(key = "#companyName", value = "finance")
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("배당금 정보 조회 시작 - companyName: {}", companyName);
        
        // 1. 회사명을 기준으로 회사 정보를 조회
        Company company = this.companyService.findByName(companyName)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회사명입니다. name: {}", companyName);
                    return new RuntimeException("존재하지 않는 회사명입니다.");
                });

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<Dividend> dividendList = this.dividendService.findAllByCompanyId(company.getId());
        log.info("배당금 정보 조회 완료 - company: {}, dividends: {}", company.getName(), dividendList.size());

        // 3. 결과 조합
        return new ScrapedResult(company, dividendList);
    }
}
