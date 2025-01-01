package com.example.demo.dividend.scraper;

import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}