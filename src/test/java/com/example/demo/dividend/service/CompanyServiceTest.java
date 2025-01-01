package com.example.demo.dividend.service;

import com.example.demo.dividend.exception.impl.NoCompanyException;
import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.Dividend;
import com.example.demo.dividend.model.ScrapedResult;
import com.example.demo.dividend.persist.entity.CompanyEntity;
import com.example.demo.dividend.persist.repository.CompanyRepository;
import com.example.demo.dividend.persist.repository.DividendRepository;
import com.example.demo.dividend.scraper.Scraper;
import org.apache.commons.collections4.Trie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private Trie<String, String> trie;

    @Mock
    private Scraper yahooFinanceScraper;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private DividendRepository dividendRepository;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void 회사_저장_성공() {
        // given
        String ticker = "AAPL";
        Company company = new Company(ticker, "Apple Inc.");
        CompanyEntity companyEntity = CompanyEntity.builder()
                .ticker(ticker)
                .name("Apple Inc.")
                .build();
        
        List<Dividend> dividends = List.of(
            new Dividend(LocalDateTime.now(), "0.5"),
            new Dividend(LocalDateTime.now().minusMonths(3), "0.5")
        );
        ScrapedResult scrapedResult = new ScrapedResult(company, dividends);
        
        when(companyRepository.existsByTicker(ticker)).thenReturn(false);
        when(yahooFinanceScraper.scrapCompanyByTicker(ticker)).thenReturn(company);
        when(yahooFinanceScraper.scrap(company)).thenReturn(scrapedResult);
        when(companyRepository.save(any())).thenReturn(companyEntity);
        when(dividendRepository.saveAll(anyList())).thenReturn(List.of());

        // when
        Company savedCompany = companyService.save(ticker);

        // then
        assertNotNull(savedCompany);
        assertEquals(ticker, savedCompany.getTicker());
        verify(companyRepository).save(any());
        verify(dividendRepository).saveAll(anyList());
    }

    @Test
    void 회사_조회_성공() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        CompanyEntity company1 = CompanyEntity.builder()
                .id(1L)
                .ticker("AAPL")
                .name("Apple Inc.")
                .build();
        CompanyEntity company2 = CompanyEntity.builder()
                .id(2L)
                .ticker("GOOGL")
                .name("Alphabet Inc.")
                .build();
        
        Page<CompanyEntity> expectedPage = new PageImpl<>(List.of(company1, company2));
        when(companyRepository.findAll(pageRequest)).thenReturn(expectedPage);

        // when
        Page<CompanyEntity> actualPage = companyService.getAllCompany(pageRequest);

        // then
        assertNotNull(actualPage);
        assertEquals(2, actualPage.getContent().size());
        verify(companyRepository).findAll(pageRequest);
    }

    @Test
    void 회사_삭제_성공() {
        // given
        String ticker = "AAPL";
        CompanyEntity company = CompanyEntity.builder()
                .id(1L)
                .ticker(ticker)
                .name("Apple Inc.")
                .build();
                
        when(companyRepository.findByTicker(ticker)).thenReturn(Optional.of(company));

        // when
        String deletedCompanyName = companyService.deleteCompany(ticker);

        // then
        assertNotNull(deletedCompanyName);
        assertEquals(company.getName(), deletedCompanyName);
        verify(dividendRepository).deleteAllByCompanyId(company.getId());
        verify(companyRepository).delete(company);
        verify(trie).remove(company.getName());
    }

    @Test
    void 존재하지_않는_회사_삭제_실패() {
        // given
        String ticker = "INVALID";
        when(companyRepository.findByTicker(ticker)).thenReturn(Optional.empty());

        // when & then
        assertThrows(NoCompanyException.class, () -> companyService.deleteCompany(ticker));
    }
}
