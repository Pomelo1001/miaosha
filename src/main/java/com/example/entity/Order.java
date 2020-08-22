package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @version 1.1.0
 * @author：caopu
 * @BelongsProject: miaosha
 * @BelongsPackage: com.example.entity
 * @time：2020-8-22
 * @Description: todo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class Order {
    private Integer id;
    private Integer sId;
    private String name;
    private Date createDate;
}
