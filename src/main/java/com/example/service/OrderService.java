package com.example.service;

/**
 * @version 1.1.0
 * @author：caopu
 * @BelongsProject: miaosha
 * @BelongsPackage: com.example.service
 * @time：2020-8-22
 * @Description: todo
 */
public interface OrderService {
    /**
     *普通的秒杀接口：处理订单方法，返回订单id
     * @param id
     * @return
     */
    int kill(Integer id);

    /**
     * 用来生成md5签名的方法
     * @param id
     * @param userId
     * @return
     */
    String  md5(Integer id, Integer userId);

    /**
     * 所谓“接口隐藏”：本质上是通过请求先获取到一个md5，然后携带该md5签名，进行请求。验证该签名合法未失效，那么就可以继续进行后续操作
     * 带md5签名的处理订单方法
     * @return
     */
    int killToken(Integer userId,Integer id,String md5);
}
