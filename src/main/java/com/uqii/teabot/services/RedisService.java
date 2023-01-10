package com.uqii.teabot.services;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class RedisService {
	private RedisTemplate<String, String> redisTemplate;
	
	public void removeKey(String key) {
		redisTemplate.delete(key);
	}
	
	public void removeByPattern(String pattern) {
		Set<String> keys = redisTemplate.keys(pattern);
		if (keys != null) {
			for (var key: keys)
				removeKey(key);
		}
	}
	
	public boolean isEmpty(String key, Object hashKey) {
		return !redisTemplate.opsForHash().hasKey(key, hashKey);
	}
	
	public boolean isExists(String key, Object hashKey) {
		return redisTemplate.opsForHash().hasKey(key, hashKey);
	}
	
	public void setToHash(String key, Object hashKey, Object value) {
		redisTemplate.opsForHash().put(key, hashKey, value);
	}
	
	public String getFromHash(String key, Object hashKey) {
		return (String) redisTemplate.opsForHash().get(key, hashKey);
	}
}
