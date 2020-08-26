package com.example.controller;

import com.example.service.OrderService;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    RateLimiter rateLimiter = RateLimiter.create(100);


    //根据令牌桶算法限制每次访问的请求数，拿到令牌的请求才能进行后续的操作，否则等待5s后继续尝试，如果还是拿不到，就丢弃该请求
    @GetMapping("kill")
    public String kill(Integer id) {
        try {
            log.info("the order id is :{}",id);
            if (!rateLimiter.tryAcquire(5, TimeUnit.SECONDS)){
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
}


