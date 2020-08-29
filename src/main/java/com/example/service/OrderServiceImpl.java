package com.example.service;

import com.example.dao.OrderDAO;
import com.example.dao.StockDAO;
import com.example.dao.UserDAO;
import com.example.entity.Order;
import com.example.entity.Stock;
import com.example.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

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
@Slf4j
public class OrderServiceImpl implements OrderService {


    @Autowired
    StockDAO stockDAO;

    @Autowired
    OrderDAO orderDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Override
    public int kill(Integer id) {

        //redis缓存预热，设置缓存失效时间，限时进行抢购
//        if (!stringRedisTemplate.hasKey("kill" + id)) {
//            log.error("抢购超时，当前活动过于火爆，已经结束！");
//            throw new RuntimeException("抢购超时，当前活动已经结束！");
//        }

        //校验库存
        Stock stock = checkStock(id);
        //扣减库存
        updateSale(stock);
        //生成订单
        return createOrder(stock);

    }

    @Override
    public String md5(Integer id, Integer userId) {
        //验证用户合法性
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户信息不存在");
        }

        //验证商品是否存在
        Stock stock = stockDAO.checkStock(id);
        if (stock == null) {
            throw new RuntimeException("商品信息不合法");
        }

        //根据用户id和商品id来生成hashkey
        String hashKey = "KEY_" + userId + "_" + id;

        //生成md5签名,"!Q*jS#"是一个随机盐。应该抽象为一个工具类生成
        String key = DigestUtils.md5DigestAsHex((userId + id + "!Q*jS#").getBytes());
        stringRedisTemplate.opsForValue().set(hashKey, key, 120, TimeUnit.SECONDS);
        log.info("redis 写入：[{}] [{}]", hashKey, key);
        return key;
    }

    @Override
    public int killToken(Integer userId, Integer id, String md5) {
        String hashKey = "KEY_" + userId + "_" + id;
        if (stringRedisTemplate.opsForValue().get(hashKey) == null){
            throw new RuntimeException("没有携带验证信息");
        }
        if (!stringRedisTemplate.opsForValue().get(hashKey).equals(md5)) {
            throw new RuntimeException("当前请求数据不合法，请稍后再尝试");
        }

        //校验库存
        Stock stock = checkStock(id);
        //扣减库存
        updateSale(stock);
        //生成订单
        return createOrder(stock);
    }

    /**
     * 校验库存
     *
     * @param id
     * @return
     */
    private Stock checkStock(Integer id) {
        Stock stock = stockDAO.checkStock(id);
        if (stock.getSale().equals(stock.getCount())) {
            throw new RuntimeException("库存不足！");
        }
        return stock;
    }

    /**
     * 扣减库存
     *
     * @param stock
     */
    private void updateSale(Stock stock) {
        int updateRows = stockDAO.updateSale(stock);
        if (updateRows == 0) {
            log.error("抢购失败，请重试！");
            throw new RuntimeException("抢购失败，请重试！");
        }
    }

    /**
     * 创建订单
     *
     * @param stock
     * @return
     */
    private Integer createOrder(Stock stock) {
        Order order = new Order();
        order.setName(stock.getName()).setSId(stock.getId()).setCreateDate(new Date());
        orderDAO.createOrder(order);
        return order.getId();
    }

}
