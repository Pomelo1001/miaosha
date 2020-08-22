package com.example.dao;

import com.example.entity.Order;

/**
 * @version 1.1.0
 * @author：caopu
 * @BelongsProject: miaosha
 * @BelongsPackage: com.example.dao
 * @time：2020-8-22
 * @Description: todo
 */
public interface OrderDAO {
    /**
     * 秒杀成功，创建订单
     * @param order
     */
    void createOrder(Order order);

}
