package by.nik.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;


@Configuration
public class RedisConfig {

    @Bean
    public Jedis initJedis() {
        // try
        Jedis jedis = new Jedis("localhost");
        return jedis;
    }

}
