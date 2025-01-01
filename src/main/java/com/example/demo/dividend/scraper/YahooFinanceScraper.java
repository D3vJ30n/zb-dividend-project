package com.example.demo.dividend.scraper;

import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.Dividend;
import com.example.demo.dividend.model.ScrapedResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Component
public class YahooFinanceScraper implements Scraper {

    private static String BASE_URL = "https://query2.finance.yahoo.com/v8/finance/chart/%s?period1=0&period2=9999999999&interval=1mo&events=div";
    private static String SUMMARY_URL = "https://finance.yahoo.com/quote/%s";

    private final ObjectMapper objectMapper;

    public YahooFinanceScraper() {
        this.objectMapper = new ObjectMapper();
    }

    // 테스트용 URL 설정
    public static void setTestUrls(String summaryUrl, String baseUrl) {
        SUMMARY_URL = summaryUrl;
        BASE_URL = baseUrl;
    }

    @Override
    public ScrapedResult scrap(Company company) {
        var scrapResult = new ScrapedResult(company);

        try {
            String url = String.format(BASE_URL, company.getTicker());
            log.info("Scraping URL: {}", url);

            Connection connection = Jsoup.connect(url);
            Document document = connection.ignoreContentType(true).get();
            String jsonStr = document.text();
            log.debug("Received JSON document: {}", jsonStr);

            JsonNode root = objectMapper.readTree(jsonStr);
            JsonNode events = root.path("chart").path("result").get(0).path("events").path("dividends");
            
            List<Dividend> dividends = new ArrayList<>();
            Iterator<JsonNode> elements = events.elements();
            
            while (elements.hasNext()) {
                JsonNode element = elements.next();
                long timestamp = element.path("date").asLong();
                double amount = element.path("amount").asDouble();
                
                LocalDateTime date = LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
                dividends.add(new Dividend(date, String.format("%.2f", amount)));
                log.debug("Parsed dividend: date={}, amount={}", date, amount);
            }

            scrapResult.setDividends(dividends);
            log.info("Successfully scraped {} dividend records", dividends.size());

        } catch (IOException e) {
            log.error("scraping failed -> " + company.getTicker(), e);
            throw new RuntimeException("배당금 데이터 스크래핑 중 오류가 발생했습니다: " + e.getMessage());
        }

        return scrapResult;
    }

    @Override
    public Company scrapCompanyByTicker(String ticker) {
        String url = String.format(SUMMARY_URL, ticker);

        try {
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByTag("h1").first();
            if (titleEle == null) {
                throw new RuntimeException("회사 정보를 찾을 수 없습니다 -> " + ticker);
            }

            String title = titleEle.text().split(" - ")[1].trim();
            return new Company(ticker, title);
        } catch (IOException e) {
            log.error("failed to scrap ticker -> " + ticker, e);
            throw new RuntimeException("회사 정보 스크래핑 중 오류가 발생했습니다: " + e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            log.error("invalid company ticker -> " + ticker, e);
            throw new RuntimeException("유효하지 않은 회사 티커입니다: " + ticker);
        }
    }
}