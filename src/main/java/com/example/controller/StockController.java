package com.example.controller;

import com.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
public class StockController {

    @Autowired
    OrderService orderService;


    @GetMapping("kill")
    public String kill(Integer id) {
        try {

            int orderId = orderService.kill(id);
            return "秒杀成功，订单id为：" + orderId;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}


