##秒杀业务（缓存、限流&降级&熔断&隔离）
### 基本的秒杀业务
```java
缓存：缓存的目的是提升系统访问速度和增大系统处理容量
降级：降级是当服务器压力剧增的情况下，根据当前业务情况及流量对一些服务和页面有策略的降级，以此释放服务器资源以保证核心任务的正常运行
限流：限流的目的是通过对并发访问/请求进行限速，或者对一个时间窗口内的请求进行限速来保护系统，一旦达到限制速率则可以拒绝服务、排队或等待、降级等处理
```
1、解决秒杀“超卖”问题  
(1)通过synchronized悲观锁进行加锁控制，注意synchronized代码块控制，不要直接锁住当前方法，性能很差并发低
(2)通过乐观锁进行控制，主要是数据库的自带的更新加锁，通过添加一个version版本字段控制，并发度高。乐观锁核心更新语句为：
  ```mysql
 update stock set sale = sale + 1,version = version +1 where id = #{id} and version = #{version}
```
2、解决请求的“限流”问题
(1)漏斗算法限流
特点：漏嘴的流水速率代表着系统允许该行为的最大频率，缺点是达到最大后直接拒绝请求
(2)令牌桶算法限流
特点：提前生成好指定数量的令牌，拿到令牌的请求才可以进行后续的秒杀操作；拿不到的可以拒绝也可以进行特定时间的等待
```java
      <!-- google 开源工具类 RateLimiter令牌桶-->
        <dependency>
            <groupId>com.google.guava</groupId>
            <artifactId>guava</artifactId>
            <version>28.2-jre</version>
        </dependency>
```
3、解决限时抢购问题
redis缓存预热，通过提前设置redis中key的失效时间
redis-cli进入命令行交互
keys * 查看所有的key值
set key value  EX 时间  
ttl key 查询key的剩余时间
4、解决秒杀接口隐藏问题
(1)所谓的“接口隐藏”：本质是通过携带一个提前约定好的签名进行访问，并对改签名进行校验，只有合法的签名才能通过进行后续的操作；
(2)通过md5签名（hash)，需要用户先访问md5接口，根据特定的规则拿到一个md5签名值，然后再访问秒杀接口
```java
 //生成md5签名
   //根据用户id和商品id来生成hashkey
         String hashKey = "KEY_" + userId + "_" + id;
         //生成md5签名,"!Q*jS#"是一个随机盐。应该抽象为一个工具类生成
         String key = DigestUtils.md5DigestAsHex((userId + id + "!Q*jS#").getBytes());
         stringRedisTemplate.opsForValue().set(hashKey, key, 120, TimeUnit.SECONDS);
         log.info("redis 写入：[{}] [{}]", hashKey, key);
       
```
```java
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
```
5、解决单用户访问频率限制
思想：通过redis存储用户的访问次数，每次请求前先取出该用户的访问次数值，进行判断。如果超过了访问次数直接返回
```java
 String key = "LIMIT" + "_" + userId;
        int limit = 1;
        String limitNum = stringRedisTemplate.opsForValue().get(key);
        if (limitNum == null) {
            //第一次调用设置为0
            stringRedisTemplate.opsForValue().set(key, "1", 3600, TimeUnit.SECONDS);
        } else {
            //非第一次调用+1
            limit = Integer.parseInt(limitNum) + 1;
            stringRedisTemplate.opsForValue().set(key, String.valueOf(limit), 3600, TimeUnit.SECONDS);
        }
        return limit;
```
