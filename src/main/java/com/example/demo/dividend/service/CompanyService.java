package com.example.demo.dividend.service;

import com.example.demo.dividend.exception.impl.NoCompanyException;
import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.ScrapedResult;
import com.example.demo.dividend.persist.entity.CompanyEntity;
import com.example.demo.dividend.persist.entity.DividendEntity;
import com.example.demo.dividend.persist.repository.CompanyRepository;
import com.example.demo.dividend.persist.repository.DividendRepository;
import com.example.demo.dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.Trie;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie<String, String> trie;
    private final Scraper yahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        log.info("회사 정보 저장 시작 - ticker: {}", ticker);
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            log.error("이미 존재하는 회사입니다. ticker: {}", ticker);
            throw new RuntimeException("already exists ticker -> " + ticker);
        }
        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        log.debug("모든 회사 정보 조회 - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // 1. 회사를 스크래핑
        log.debug("회사 정보 스크래핑 시작 - ticker: {}", ticker);
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)) {
            log.error("존재하지 않는 회사입니다. ticker: {}", ticker);
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 2. 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        log.debug("배당금 정보 스크래핑 시작 - company: {}", company.getName());
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 3. 스크래핑 결과 반환
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(null, company.getTicker(), company.getName()));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(null, companyEntity.getId(), e.getDate(), e.getDividend()))
                .toList();
        this.dividendRepository.saveAll(dividendEntities);
        log.info("회사 및 배당금 정보 저장 완료 - company: {}, dividends: {}", company.getName(), dividendEntities.size());

        return company;
    }

    public List<String> getCompanyNamesByKeyword(String keyword) {
        log.debug("회사명 자동완성 검색 - keyword: {}", keyword);
        Pageable limit = PageRequest.of(0, 10);
        Page<CompanyEntity> companyEntities = this.companyRepository.findByNameStartingWithIgnoreCase(keyword, limit);
        return companyEntities.getContent()
                .stream()
                .map(CompanyEntity::getName)
                .toList();
    }

    public void addAutocompleteKeyword(String keyword) {
        log.debug("자동완성 키워드 추가 - keyword: {}", keyword);
        this.trie.put(keyword, null);
    }

    public List<String> autocomplete(String keyword) {
        log.debug("자동완성 검색 - keyword: {}", keyword);
        return this.trie.prefixMap(keyword).keySet()
                .stream()
                .limit(10)
                .toList();
    }

    public void deleteAutocompleteKeyword(String keyword) {
        log.debug("자동완성 키워드 삭제 - keyword: {}", keyword);
        this.trie.remove(keyword);
    }

    @CacheEvict(value = "finance", key = "#companyName")
    public String deleteCompany(String ticker) {
        log.info("회사 정보 삭제 시작 - ticker: {}", ticker);
        var company = this.companyRepository.findByTicker(ticker)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회사입니다. ticker: {}", ticker);
                    return new NoCompanyException();
                });

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);
        this.deleteAutocompleteKeyword(company.getName());
        log.info("회사 정보 삭제 완료 - company: {}", company.getName());
        return company.getName();
    }

    public Optional<Company> findByName(String name) {
        log.debug("회사명으로 회사 정보 검색 - name: {}", name);
        return this.companyRepository.findByName(name)
                .map(e -> new Company(e.getId(), e.getTicker(), e.getName()));
    }
}