package com.example.service;

import com.example.dao.OrderDAO;
import com.example.dao.StockDAO;
import com.example.entity.Order;
import com.example.entity.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @version 1.1.0
 * @author：caopu
 * @BelongsProject: miaosha
 * @BelongsPackage: com.example.service
 * @time：2020-8-22
 * @Description: todo
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {


    @Autowired
    StockDAO stockDAO;

    @Autowired
    OrderDAO orderDAO;


    @Override
    public int kill(Integer id) {
        //校验库存
        Stock stock = checkStock(id);
        //扣减库存
        updateSale(stock);
        //生成订单
        return createOrder(stock);

}

    //校验库存
    private Stock checkStock(Integer id) {
        Stock stock = stockDAO.checkStock(id);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足！");
        }
        return stock;
    }

    //扣减库存
    private void updateSale(Stock stock) {
        int updateRows = stockDAO.updateSale(stock);
        if (updateRows == 0){
            throw new RuntimeException("请购失败，请重试！");
        }
    }

    //创建订单

    private Integer createOrder(Stock stock) {
        Order order = new Order();
        order.setName(stock.getName()).setSId(stock.getId()).setCreateDate(new Date());
        orderDAO.createOrder(order);
        return order.getId();
    }

}
