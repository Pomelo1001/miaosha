package com.example.controller;

import com.example.service.OrderService;
import com.example.service.UserService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @version 1.1.0
 * @author：caopu
 * @BelongsProject: miaosha
 * @BelongsPackage: com.example.controller
 * @time：2020-8-22
 * @Description: todo
 */
@RestController
@RequestMapping("goods")
@Slf4j
public class StockController {

    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;


    RateLimiter rateLimiter = RateLimiter.create(100);


    //根据令牌桶算法限制每次访问的请求数，拿到令牌的请求才能进行后续的操作，否则等待5s后继续尝试，如果还是拿不到，就丢弃该请求
    //令牌桶限流+乐观锁防止超卖 + redis缓存预热，设置缓存失效时间，限时进行抢购
    @GetMapping("kill")
    public String kill(Integer id) {
        try {
            if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
                log.error("当前请求被限流，无法完成后续处理逻辑.....");
                return "秒杀失败";
            }
            int orderId = orderService.kill(id);
            return "秒杀成功，订单id为：" + orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    //令牌桶限流+数据库乐观锁防止超卖 + md5签名(hash接口隐藏)
    @GetMapping("killToken")
    public String killToken(Integer userId,Integer id,String md5) {
        try {
            if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
                log.error("当前请求被限流，无法完成后续处理逻辑.....");
                return "秒杀失败";
            }
            int orderId = orderService.killToken(userId,id,md5);
            return "秒杀成功，订单id为：" + orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    //令牌桶限流+数据库乐观锁防止超卖 + md5签名(hash接口隐藏)+单用户频率限制
    @GetMapping("killTokenLimit")
    public String killTokenLimit(Integer userId,Integer id,String md5) {
        try {
            if (!rateLimiter.tryAcquire(1, TimeUnit.SECONDS)) {
                log.error("当前请求被限流，无法完成后续处理逻辑.....");
                return "秒杀失败";
            }

            int count = userService.saveUserCount(userId);
            log.info("截止目前，当前用户的访问次数为：[{}]", count);
            boolean isBanned = userService.getUserCount(userId);
            if (isBanned){
                log.error("购买失败，超过访问频率限制");
                return "购买失败，超过访问频率限制";
            }

            int orderId = orderService.killToken(userId,id,md5);
            return "秒杀成功，订单id为：" + orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    @GetMapping("md5")
    public String getMD5(Integer userId, Integer id) {
        String md5;
        try {
            md5 = orderService.md5(id, userId);
        } catch (Exception e) {
            e.printStackTrace();
            return "获取md5值失败:" + e.getMessage();
        }

        return "获取md5值为:" + md5;
    }
}


