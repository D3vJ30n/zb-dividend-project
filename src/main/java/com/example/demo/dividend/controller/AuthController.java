package com.example.demo.dividend.controller;

import com.example.demo.dividend.model.ApiResponse;
import com.example.demo.dividend.model.Auth;
import com.example.demo.dividend.model.constants.Message;
import com.example.demo.dividend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 인증 관련 요청을 처리하는 컨트롤러 클래스
@RestController
@RequestMapping("/auth") // 기본 요청 경로를 "/auth"로 설정
@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성하기 위해 사용
public class AuthController {

    private final AuthService authService; // 인증 관련 로직을 처리하는 AuthService를 주입받음

    // 회원가입 요청을 처리하는 메서드
    @PostMapping("/signup") // "/auth/signup" 경로로 POST 요청을 받음
    public ResponseEntity<?> signup(@Valid @RequestBody Auth.SignUp request) {
        // AuthService를 통해 회원가입 요청을 처리
        var result = this.authService.signup(request.getUsername(), request.getPassword());
        // 성공 응답을 반환
        return ResponseEntity.ok(ApiResponse.success(Message.SUCCESS_SIGNUP, result));
    }

    // 로그인 요청을 처리하는 메서드
    @PostMapping("/signin") // "/auth/signin" 경로로 POST 요청을 받음
    public ResponseEntity<?> signin(@Valid @RequestBody Auth.SignIn request) {
        // AuthService를 통해 로그인 요청을 처리하고 토큰을 생성
        var token = this.authService.signin(request.getUsername(), request.getPassword());
        // 성공 응답과 함께 토큰을 반환
        return ResponseEntity.ok(ApiResponse.success(Message.SUCCESS_SIGNIN, Map.of("token", token)));
    }
}

/*
### 주요 동작과 이유

1. 회원가입 메서드 (signup)
   - 클라이언트로부터 회원가입 요청 데이터를 수신하고, AuthService를 호출하여 회원가입 로직을 처리함.
   - 요청 데이터는 @Valid를 통해 유효성 검사를 수행한 후 처리됨.
   - 처리 결과를 성공 메시지와 함께 클라이언트에게 응답으로 반환함.

2. 로그인 메서드 (signin)
   - 클라이언트로부터 로그인 요청 데이터를 수신하고, AuthService를 호출하여 로그인 로직을 처리함.
   - 처리 결과로 생성된 JWT 토큰을 Map 형식으로 클라이언트에게 응답으로 반환함.
   - 요청 데이터는 @Valid를 통해 유효성 검사를 수행한 후 처리됨.

3. ApiResponse
   - 응답 데이터 구조를 통일하기 위해 사용됨.
   - 응답의 성공 여부, 메시지, 데이터를 포함하여 클라이언트에게 반환함.

4. Message
   - 메시지 상수를 정의한 클래스를 사용하여 응답 메시지를 통일하고 관리하기 쉽게 만듦.

---

### 코드의 목적
이 클래스는 인증 관련 API 요청(회원가입 및 로그인)을 처리하는 컨트롤러임.
회원가입과 로그인 요청 데이터를 수신하고, AuthService를 호출하여 필요한 로직을 처리한 뒤 결과를 클라이언트에게 반환함.
이를 통해 인증과 관련된 작업을 중앙화하고, 응답 데이터 구조를 통일하여 클라이언트와의 통신을 효율적으로 관리함.
 */
