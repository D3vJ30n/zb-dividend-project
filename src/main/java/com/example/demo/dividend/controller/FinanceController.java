package com.example.demo.dividend.controller;

import com.example.demo.dividend.model.ScrapedResult;
import com.example.demo.dividend.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 이 클래스는 FinanceService를 통해 재무 데이터를 조회하는 REST API 컨트롤러임
// 클라이언트 요청을 받아 비즈니스 로직을 처리하고 결과를 반환하는 역할을 함
@RestController // REST API 컨트롤러임을 나타냄
@RequestMapping("/finance") // 기본 URL 경로를 "/finance"로 설정
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
public class FinanceController {

    // FinanceService를 통해 비즈니스 로직을 처리
    private final FinanceService financeService;

    // 특정 회사의 배당 정보를 조회하는 API 엔드포인트
    @GetMapping("/dividend/{companyName}") // "/dividend/{companyName}" 경로로 요청을 처리
    public ResponseEntity<ScrapedResult> searchFinance(@PathVariable String companyName) {
        // PathVariable로 전달된 companyName을 기반으로 배당 정보를 조회
        ScrapedResult result = this.financeService.getDividendByCompanyName(companyName);

        // 조회된 결과를 HTTP 응답 본문으로 반환
        return ResponseEntity.ok(result);
    }
}

/*
### 주요 동작과 이유

1. @RestController
   - 이 클래스가 REST API 요청을 처리하는 컨트롤러임을 나타냄.
   - 메서드에서 반환되는 값은 HTTP 응답 본문으로 직렬화됨.

2. @RequestMapping("/finance")
   - 이 컨트롤러의 기본 URL 경로를 "/finance"로 설정.
   - 모든 엔드포인트는 "/finance"로 시작함.

3. @GetMapping("/dividend/{companyName}")
   - HTTP GET 요청을 처리하는 메서드를 정의.
   - URL 경로의 {companyName} 부분을 변수로 받아옴.

4. @PathVariable
   - URL 경로에서 {companyName} 값을 추출하여 메서드의 매개변수로 전달.

5. FinanceService
   - 비즈니스 로직을 처리하는 서비스 계층으로, 회사 이름을 기반으로 배당 정보를 조회.

6. ResponseEntity
   - HTTP 응답을 캡슐화하여 상태 코드와 함께 결과 데이터를 반환.

---

### 코드의 목적
이 클래스는 특정 회사 이름으로 배당 정보를 조회하는 REST API를 구현함.  
FinanceService를 호출하여 배당 데이터를 가져오고, 이를 클라이언트에게 JSON 형태로 반환함.  
이를 통해 클라이언트는 회사 이름을 기반으로 배당 정보를 쉽게 조회할 수 있음.
 */
