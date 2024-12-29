package com.example.demo.dividend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

// @RequiredArgsConstructor는 final 필드나 @NonNull 필드에 대해 생성자를 자동으로 생성하기 위해 사용됨
@RequiredArgsConstructor

// Spring Framework에서 이 클래스를 설정 정보로 인식하도록 하기 위해 @Configuration을 선언함
@Configuration
public class CacheConfig {

    // Redis 서버의 호스트 이름을 가져오기 위해 @Value로 외부 설정 파일에서 값을 주입받음
    @Value("${spring.redis.host}")
    private String host;

    // Redis 서버의 포트 번호를 가져오기 위해 @Value로 외부 설정 파일에서 값을 주입받음
    @Value("${spring.redis.port}")
    private int port;

    // RedisConnectionFactory 빈을 생성하여 Redis 서버와의 연결을 관리함
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // RedisStandaloneConfiguration 객체를 생성하여 독립형 Redis 서버 설정을 구성함
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();

        // Redis 서버의 호스트 이름을 설정함
        conf.setHostName(host);

        // Redis 서버의 포트 번호를 설정함
        conf.setPort(port);

        // LettuceConnectionFactory 객체를 생성하여 Redis 연결 팩토리를 반환함
        return new LettuceConnectionFactory(conf);
    }

    // RedisCacheManager 빈을 생성하여 Redis를 캐시로 사용하는 설정을 관리함
    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // RedisCacheConfiguration을 기본 설정으로 초기화함
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
            // 캐시 키를 StringRedisSerializer를 사용하여 문자열로 직렬화함
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            // 캐시 값을 GenericJackson2JsonRedisSerializer를 사용하여 JSON 형식으로 직렬화함
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
            // 캐시 항목의 기본 TTL(Time-to-Live)을 3분으로 설정함
            .entryTtl(Duration.ofMinutes(3));

        // RedisCacheManager를 생성하여 RedisConnectionFactory와 캐시 설정을 적용함
        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(conf) // 기본 캐시 설정을 적용
            .build(); // CacheManager 객체를 반환
    }
}

/*
### 주요 동작과 이유

1. **`@RequiredArgsConstructor`**
   - 클래스의 `final` 필드와 `@NonNull`이 붙은 필드에 대해 생성자를 자동으로 생성하기 위해 사용함.
   - 현재는 `final` 필드가 없지만, 추가될 경우를 대비하여 작성됨.

2. **RedisConnectionFactory**
   - Redis와의 연결을 생성하고 관리하기 위해 사용됨.
   - `LettuceConnectionFactory`를 통해 RedisStandaloneConfiguration을 기반으로 단일 노드 Redis 서버에 연결하도록 설정함.

3. **RedisCacheManager**
   - Spring CacheManager 구현체로, Redis를 캐시로 사용하기 위해 설정함.
   - 캐시 데이터의 직렬화 방식을 설정하기 위해 `StringRedisSerializer`와 `GenericJackson2JsonRedisSerializer`를 사용함.
   - TTL 설정(`entryTtl`)으로 캐시의 유효 기간을 3분으로 제한하여 오래된 데이터를 자동으로 제거하도록 설정함.

4. **직렬화 설정**
   - `StringRedisSerializer`: 캐시 키를 문자열로 저장하기 위해 사용.
   - `GenericJackson2JsonRedisSerializer`: 객체를 JSON 형식으로 변환하여 캐시 값으로 저장하기 위해 사용.

---

### 코드의 목적
이 클래스는 Redis를 기반으로 캐시를 설정하기 위한 구성 클래스임. Redis 서버 연결 정보를 설정하고, Spring CacheManager를 사용해 Redis 캐시 설정을 관리함.
이를 통해 Redis를 효율적으로 활용하여 데이터를 직렬화하고 TTL(Time-to-Live) 설정을 통해 캐시 데이터의 만료를 관리함.
 */