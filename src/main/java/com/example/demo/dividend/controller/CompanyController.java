package com.example.demo.dividend.controller;

import com.example.demo.dividend.model.Company;
import com.example.demo.dividend.persist.entity.CompanyEntity;
import com.example.demo.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// 회사 정보를 관리하는 컨트롤러 클래스
@RestController
@RequestMapping("/company") // "/company" 경로로 시작하는 요청을 처리
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
public class CompanyController {

    private final CompanyService companyService; // 회사 정보를 처리하는 서비스 객체를 주입받음

    // 키워드 자동완성을 처리하는 메서드
    @GetMapping("/autocomplete") // "/company/autocomplete" 경로로 GET 요청을 처리
    public ResponseEntity<?> autocomplete(@RequestParam String keyword) {
        // 키워드를 기반으로 회사 이름 목록을 검색
        var result = this.companyService.getCompanyNamesByKeyword(keyword);
        // 검색 결과를 클라이언트에 반환
        return ResponseEntity.ok(result);
    }

    // 회사 목록을 검색하는 메서드
    @GetMapping // "/company" 경로로 GET 요청을 처리
    @PreAuthorize("hasRole('READ')") // READ 권한이 있는 사용자만 접근 가능
    public ResponseEntity<?> searchCompany(final Pageable pageable) {
        // 페이지네이션을 지원하며 회사 목록을 검색
        Page<CompanyEntity> companies = this.companyService.getAllCompany(pageable);
        // 검색 결과를 클라이언트에 반환
        return ResponseEntity.ok(companies);
    }

    // 새로운 회사를 추가하는 메서드
    @PostMapping // "/company" 경로로 POST 요청을 처리
    @PreAuthorize("hasRole('WRITE')") // WRITE 권한이 있는 사용자만 접근 가능
    public ResponseEntity<?> addCompany(@RequestBody Company request) {
        // 요청 데이터에서 티커(ticker)를 가져옴
        String ticker = request.getTicker().trim();
        // 티커 값이 비어 있으면 예외를 발생
        if (ticker.isEmpty()) {
            throw new RuntimeException("ticker is empty");
        }

        // 새로운 회사를 저장
        Company company = this.companyService.save(ticker);
        // 자동완성 키워드에 추가
        this.companyService.addAutocompleteKeyword(company.getName());
        // 저장된 회사 정보를 클라이언트에 반환
        return ResponseEntity.ok(company);
    }

    // 회사를 삭제하는 메서드
    @DeleteMapping("/{ticker}") // "/company/{ticker}" 경로로 DELETE 요청을 처리
    @PreAuthorize("hasRole('WRITE')") // WRITE 권한이 있는 사용자만 접근 가능
    public ResponseEntity<?> deleteCompany(@PathVariable String ticker) {
        // 티커를 기반으로 회사를 삭제
        String companyName = this.companyService.deleteCompany(ticker);
        // 삭제된 회사 이름을 클라이언트에 반환
        return ResponseEntity.ok(companyName);
    }
}

/*
### 주요 동작과 이유

1. 키워드 자동완성 (autocomplete)
   - 클라이언트에서 입력한 키워드를 기반으로 회사 이름 목록을 검색함.
   - 검색 결과를 클라이언트에 반환하여 자동완성 기능을 지원함.

2. 회사 목록 검색 (searchCompany)
   - 모든 회사 목록을 페이지네이션을 통해 검색하여 반환함.
   - READ 권한이 있는 사용자만 접근 가능하도록 설정됨.

3. 회사 추가 (addCompany)
   - 클라이언트 요청에서 제공된 티커 정보를 기반으로 새로운 회사를 추가함.
   - 추가된 회사 이름을 자동완성 키워드에 등록하여 검색 기능을 확장함.
   - WRITE 권한이 있는 사용자만 접근 가능하도록 설정됨.

4. 회사 삭제 (deleteCompany)
   - 티커를 기반으로 회사 정보를 삭제함.
   - 삭제된 회사의 이름을 반환하여 클라이언트에게 결과를 전달함.
   - WRITE 권한이 있는 사용자만 접근 가능하도록 설정됨.

---

### 코드의 목적
이 클래스는 회사 정보를 관리하기 위한 API를 제공하는 컨트롤러임.
회사의 자동완성 기능, 목록 검색, 추가, 삭제를 처리하는 엔드포인트를 정의함.
이를 통해 클라이언트와의 데이터 교환을 효과적으로 수행하며, Spring Security를 사용해 권한에 따라 요청을 제한함.
 */
