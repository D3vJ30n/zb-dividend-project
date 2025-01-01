package com.example.demo.dividend.service;

import com.example.demo.dividend.exception.impl.NoCompanyException;
import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.Dividend;
import com.example.demo.dividend.model.ScrapedResult;
import com.example.demo.dividend.persist.entity.CompanyEntity;
import com.example.demo.dividend.persist.entity.DividendEntity;
import com.example.demo.dividend.persist.repository.CompanyRepository;
import com.example.demo.dividend.persist.repository.DividendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class DividendService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName", value = "finance")
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
            .orElseThrow(NoCompanyException::new);

        // 2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
            .map(e -> new Dividend(e.getDate(), e.getDividend()))
            .toList();

        return new ScrapedResult(
            new Company(company.getTicker(), company.getName()),
            dividends
        );
    }

    public List<Dividend> findAllByCompanyId(Long companyId) {
        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(companyId);
        return dividendEntities.stream()
                .map(e -> new Dividend(e.getDate(), e.getDividend()))
                .collect(Collectors.toList());
    }
}