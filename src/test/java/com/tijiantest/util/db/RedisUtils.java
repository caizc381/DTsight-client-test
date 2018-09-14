package com.tijiantest.util.db;

import com.tijiantest.base.ConfDefine;
import com.tijiantest.util.ConfParser;
import redis.clients.jedis.Jedis;

public class RedisUtils {

    public static Jedis jedis = null;
    /**
     * 获取redis客户端
     * @return
     */
    static {
        ConfParser cparser = new ConfParser(ConfDefine.ENV_CONFIG);
        String host = cparser.getValue(ConfDefine.REDIS, ConfDefine.REDIS_HOST);
        String port = cparser.getValue(ConfDefine.REDIS, ConfDefine.REDIS_PORT);
        String password = cparser.getValue(ConfDefine.REDIS, ConfDefine.REDIS_PASSWORD);
        String redisTimeout = cparser.getValue(ConfDefine.REDIS, ConfDefine.REDIS_TIMEOUT);

        jedis = new Jedis(host,Integer.parseInt(port),Integer.parseInt(redisTimeout));
        jedis.auth(password);
    }


    public static void  main(String ...args){
        Jedis jedis = RedisUtils.jedis;
        String key = "parent_card_number_balance_33049";
        System.out.println(jedis.get(key));
    }

}
