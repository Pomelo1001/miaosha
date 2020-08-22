package com.example.dao;

import com.example.entity.Stock;

/**
 * @version 1.1.0
 * @author：caopu
 * @BelongsProject: miaosha
 * @BelongsPackage: com.example.dao
 * @time：2020-8-22
 * @Description: todo
 */
public interface StockDAO {
    /**
     * 根据描述商品的id查询库存
     * @param id
     * @return
     */
    Stock checkStock(Integer id);

    /**
     * 根据商品id更新库存
     * @param stock
     */
    int updateSale(Stock stock);
}
