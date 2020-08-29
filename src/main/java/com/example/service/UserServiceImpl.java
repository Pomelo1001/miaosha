package com.example.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.1.0
 * @author：caopu
 * @time：2020-8-27
 * @Description: todo
 */
@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private final int MAX_LIMIT = 10;

    @Override
    public boolean getUserCount(Integer userId) {
        String key = "LIMIT" + "_" + userId;
        String limitNum = stringRedisTemplate.opsForValue().get(key);
        if (limitNum == null) {
            log.error("该用户没有访问记录，信息异常，请注意");
            return true;
        }
        return Integer.valueOf(limitNum) > MAX_LIMIT;
    }

    @Override
    public int saveUserCount(Integer userId) {
        String key = "LIMIT" + "_" + userId;
        int limit = 1;
        String limitNum = stringRedisTemplate.opsForValue().get(key);
        if (limitNum == null) {
            //第一次调用设置为0
            stringRedisTemplate.opsForValue().set(key, "1", 3600, TimeUnit.SECONDS);
        } else {
            //非第一次调用+1
            limit = Integer.parseInt(limitNum) + 1;
            stringRedisTemplate.opsForValue().set(key, String.valueOf(limit), 3600, TimeUnit.SECONDS);
        }
        return limit;
    }
}
