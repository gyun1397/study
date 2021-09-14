package com.domain.util;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@Configuration
public class RedisUtil {

    @Value("${spring.redis.port}")
    public int          port;
    @Value("${spring.redis.host}")
    public String       host;
    @Value("${spring.redis.password}")
    public String       password;

    private static StringRedisTemplate stringRedisTemplate = null;
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        return connectionFactory;
    }
    
    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory());
        RedisUtil.setRedisTemplate(stringRedisTemplate);
        return stringRedisTemplate;
    }
    
    public static String getData(String key) {
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    public static void setData(String key, String value) {
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        valueOperations.set(key,value);
    }

    public static void setDataExpire(String key, String value, long duration) {
        ValueOperations<String,String> valueOperations = stringRedisTemplate.opsForValue();
        Duration expireDuration = Duration.ofMillis(duration);
        valueOperations.set(key,value,expireDuration);
    }
    
    public static boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }
    
    public static void deleteData(String key) {
        stringRedisTemplate.delete(key);
    }
    

    public static void setRedisTemplate(StringRedisTemplate templat) {
        stringRedisTemplate = templat;
    }
}
