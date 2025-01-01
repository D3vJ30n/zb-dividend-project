package com.example.demo.dividend.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

// 설정 클래스임을 나타내기 위해 @Configuration을 붙임
// Spring Framework에서 이 클래스를 설정 정보로 처리하도록 하기 위함
@Configuration
public class AppConfig {

    // Trie<String, String> 타입의 빈을 생성함
    // PatriciaTrie 객체를 반환하여 Trie 자료구조를 사용 가능하게 함
    @Bean
    public Trie<String, String> trie() {
        // PatriciaTrie 객체를 생성함
        // PatriciaTrie는 문자열 키의 공통 접두사를 공유하는 효율적인 문자열 검색 구조를 제공함
        return new PatriciaTrie<>();
    }

    // PasswordEncoder 타입의 빈을 생성함
    // BCryptPasswordEncoder 객체를 반환하여 비밀번호를 암호화할 수 있도록 함
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCryptPasswordEncoder 객체를 생성함
        // 단방향 암호화 방식으로 보안성을 높이고, 비밀번호 해시를 생성하기 위함
        return new BCryptPasswordEncoder();
    }
}

/*
### 추가 설명

1. @Configuration
   Spring Framework에서 이 클래스를 설정 정보로 사용하기 위해 선언함.
   이 클래스 내부에서 정의한 메서드들은 @Bean 어노테이션을 통해 Spring 컨테이너에 객체(빈)로 등록된다.

2. @Bean
   @Bean 어노테이션은 해당 메서드가 반환하는 객체를 Spring 컨테이너가 관리하는 빈(Bean)으로 등록하기 위해 사용함.
   Spring 컨테이너란?
   - 애플리케이션에서 사용하는 객체(빈)를 생성하고 관리하는 역할을 담당하는 Spring의 핵심 구성 요소.
   빈의 역할
   - 애플리케이션 전역에서 재사용 가능하며, 필요한 곳에서 의존성 주입(Dependency Injection)을 통해 사용 가능함.

3. 제네릭 표기법 <T>
   - Trie<String, String>: PatriciaTrie 객체가 String 타입의 키와 값을 저장하도록 명시함.
   - <String, String>과 같은 제네릭은 컴파일러에게 이 객체가 어떤 타입을 다룰지 알려줌으로써 코드의 타입 안정성을 보장함.
   - PatriciaTrie는 문자열 키를 효율적으로 관리하고 검색하는 자료구조이므로 키와 값의 타입을 명시적으로 설정함.

4. <> (다이아몬드 연산자)
   - new PatriciaTrie<>(): Java 7부터 도입된 다이아몬드 연산자. 객체 생성 시 제네릭 타입을 반복하지 않도록 간결하게 작성하기 위해 사용함.
   - 예를 들어, new PatriciaTrie<String, String>() 대신 new PatriciaTrie<>()로 타입을 생략 가능.
   - 컴파일러가 앞의 타입 정보를 기반으로 제네릭 타입을 추론함.

5. BCryptPasswordEncoder
   - 단방향 해시 함수(BCrypt)를 사용해 비밀번호를 암호화.
   - 해싱은 암호화된 값을 복호화할 수 없기 때문에, 보안이 중요한 비밀번호와 같은 데이터를 안전하게 저장하기 위해 사용함.
   - 추가적으로, BCrypt는 반복 횟수(cost)를 설정해 연산 속도를 조절할 수 있어, 공격자가 해시 값을 역추적하기 어렵게 함.
 */

/*
### 주요 동작과 이유

1. @Configuration
   - Spring Framework에서 이 클래스를 설정 정보로 인식하도록 선언함.
   - 이 클래스 내부에서 정의한 메서드들이 스프링 컨테이너에 관리되는 빈으로 등록되도록 함.

2. @Bean - trie()
   - PatriciaTrie 객체를 스프링 빈으로 등록하여 전역적으로 사용할 수 있도록 함.
   - `PatriciaTrie`는 문자열 키의 공통 접두사를 공유하는 자료구조로, 문자열 검색 및 관리에 효율적임.

3. @Bean - passwordEncoder()
   - BCryptPasswordEncoder 객체를 스프링 빈으로 등록하여 비밀번호 암호화를 지원함.
   - BCrypt는 단방향 암호화 방식을 사용해 비밀번호의 안전성과 복잡성을 보장함.

---

### 코드의 목적
이 클래스는 애플리케이션에서 문자열 검색 및 비밀번호 암호화를 위한 설정을 정의한 구성 클래스임.
PatriciaTrie를 활용해 문자열 키 관리 및 검색을 효율적으로 수행하고, BCryptPasswordEncoder를 사용해 안전한 비밀번호 암호화를 지원함.
이를 통해 개발자가 손쉽게 문자열 관리와 보안 설정을 사용할 수 있도록 지원함.
 */