package com.example.service;

/**
 * @version 1.1.0
 * @author：caopu
 * @time：2020-8-27
 * @Description: todo
 */
public interface UserService {
    /**
     * 判断单位时间内调用次数
     * @param userId
     * @return
     */
    boolean getUserCount(Integer userId);

    /**向redis中写入用户访问次数
     * @param userId
     * @return
     */
    int saveUserCount(Integer userId);
}
