package com.uqii.teabot.services;

import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RedisService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void removeKey(String key) {
    redisTemplate.delete(key);
  }

  public void setToHash(String key, Object hashKey, Object value) {
    redisTemplate.opsForHash().put(key, hashKey, value);
  }

  public String getFromHash(String key, Object hashKey) {
    return (String) redisTemplate.opsForHash().get(key, hashKey);
  }

  public boolean isEmpty(String key, Object hashKey) {
    return !redisTemplate.opsForHash().hasKey(key, hashKey);
  }

  public boolean isExists(String key, Object hashKey) {
    return redisTemplate.opsForHash().hasKey(key, hashKey);
  }

  public String getEvaluatingKey(Long userId) {
    return "user:" + userId + ":evaluating";
  }

  public String getEditingKey(Long userId) {
    return "user:" + userId + ":editing";
  }

  public String getDeletingKey(Long userId) {
    return "user:" + userId + ":deleting";
  }

  public String getCreatingKey(Long userId) {
    return "user:" + userId + ":creating";
  }

  public void clearUserCache(Long userId) {
    Set<String> keys = redisTemplate.keys("*:" + userId + ":*");
    if (keys != null) {
      for (String key : keys) {
        removeKey(key);
      }
    }
  }
}
