package by.nik.dao;

import by.nik.models.Trader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;


@Component
public class TraderDAO {
    @Autowired
    private Jedis jedis;

    Trader trader;
    public TraderDAO(Trader trader) {
        this.trader = trader;
    }

    public void create(Trader trader) {
        //data structure for trader
        jedis.hset("trader:" + trader.getId(), "title", trader.getTitle());
        jedis.hset("trader:" + trader.getId(), "text", trader.getText());
        jedis.hset("trader:" + trader.getId(), "status", trader.getStatus().toString());
        jedis.hset("trader:" + trader.getId(), "author_id", trader.getAuthor_id());
        jedis.hset("trader:" + trader.getId(), "created_at", trader.getCreated_at().toString());
        jedis.hset("trader:" + trader.getId(), "updated_at", trader.getUpdated_at().toString());
        jedis.hset("trader:" + trader.getId(), "game_id", trader.getGame_id());
    }

}
