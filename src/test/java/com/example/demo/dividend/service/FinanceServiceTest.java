package com.example.demo.dividend.service;

import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.Dividend;
import com.example.demo.dividend.model.ScrapedResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinanceServiceTest {

    @Mock
    private CompanyService companyService;

    @Mock
    private DividendService dividendService;

    @InjectMocks
    private FinanceService financeService;

    @Test
    @DisplayName("회사 이름으로 배당금 정보를 성공적으로 조회")
    void 배당금_정보_조회_성공() {
        // given
        String companyName = "Apple Inc.";
        Company company = new Company("AAPL", companyName);
        company.setId(1L);

        LocalDateTime now = LocalDateTime.now();
        List<Dividend> dividends = Arrays.asList(
            new Dividend(now, "0.85"),
            new Dividend(now.minusMonths(3), "0.82")
        );

        when(companyService.findByName(companyName)).thenReturn(Optional.of(company));
        when(dividendService.findAllByCompanyId(company.getId())).thenReturn(dividends);

        // when
        ScrapedResult result = financeService.getDividendByCompanyName(companyName);

        // then
        assertNotNull(result);
        assertEquals(companyName, result.getCompany().getName());
        assertEquals(2, result.getDividends().size());
        assertEquals(dividends.get(0).getDate(), result.getDividends().get(0).getDate());
        assertEquals(dividends.get(0).getDividend(), result.getDividends().get(0).getDividend());
        
        verify(companyService).findByName(companyName);
        verify(dividendService).findAllByCompanyId(company.getId());
    }

    @Test
    @DisplayName("존재하지 않는 회사명으로 조회시 예외가 발생")
    void 존재하지_않는_회사_조회_실패() {
        // given
        String companyName = "Invalid Company";
        when(companyService.findByName(companyName)).thenReturn(Optional.empty());

        // when & then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> financeService.getDividendByCompanyName(companyName));
            
        assertEquals("존재하지 않는 회사명입니다.", exception.getMessage());
        verify(companyService).findByName(companyName);
    }
}
