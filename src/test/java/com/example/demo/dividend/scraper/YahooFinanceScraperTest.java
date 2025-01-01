package com.example.demo.dividend.scraper;

import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.Dividend;
import com.example.demo.dividend.model.ScrapedResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class YahooFinanceScraperTest {

    @InjectMocks
    private YahooFinanceScraper scraper;

    private static MockWebServer mockWebServer;
    private static final String MOCK_COMPANY_HTML = """
            <html>
                <h1>AAPL - Apple Inc.</h1>
            </html>
            """;

    private static final String MOCK_DIVIDEND_JSON = """
            {
                "chart": {
                    "result": [{
                        "events": {
                            "dividends": {
                                "1709251200": {
                                    "amount": 0.24,
                                    "date": 1709251200
                                }
                            }
                        }
                    }]
                }
            }
            """;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        
        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        YahooFinanceScraper.setTestUrls(
            baseUrl + "/quote/%s",
            baseUrl + "/v8/finance/chart/%s"
        );
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("회사 정보를 성공적으로 스크래핑")
    void 회사_정보_스크래핑_성공() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(MOCK_COMPANY_HTML));

        // when
        Company company = scraper.scrapCompanyByTicker("AAPL");

        // then
        assertNotNull(company, "Company should not be null");
        assertEquals("AAPL", company.getTicker(), "Company ticker should match");
        assertEquals("Apple Inc.", company.getName(), "Company name should match");
    }

    @Test
    @DisplayName("배당금 정보를 성공적으로 스크래핑")
    void 배당금_정보_스크래핑_성공() {
        // given
        Company company = new Company("AAPL", "Apple Inc.");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(MOCK_DIVIDEND_JSON));

        // when
        ScrapedResult result = scraper.scrap(company);

        // then
        assertNotNull(result, "ScrapedResult should not be null");
        assertNotNull(result.getCompany(), "Company in ScrapedResult should not be null");
        assertEquals("AAPL", result.getCompany().getTicker(), "Company ticker should match");
        assertEquals("Apple Inc.", result.getCompany().getName(), "Company name should match");
        
        List<Dividend> dividends = result.getDividends();
        assertNotNull(dividends, "Dividends list should not be null");
        assertFalse(dividends.isEmpty(), "Dividends list should not be empty");
        assertEquals(1, dividends.size(), "Should have exactly one dividend record");
        
        Dividend dividend = dividends.get(0);
        assertNotNull(dividend, "Dividend record should not be null");
        assertEquals("0.24", dividend.getDividend(), "Dividend amount should match");
        assertEquals(LocalDateTime.of(2024, 3, 1, 0, 0), dividend.getDate(), "Dividend date should match");
    }

    @Test
    @DisplayName("유효하지 않은 티커의 경우 예외가 발생")
    void 유효하지_않은_티커_스크래핑() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            scraper.scrapCompanyByTicker("INVALID");
        });
    }

    @Test
    @DisplayName("네트워크 오류 시 예외가 발생")
    void 네트워크_오류_처리() {
        // given
        mockWebServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            scraper.scrapCompanyByTicker("AAPL");
        });
    }
}
