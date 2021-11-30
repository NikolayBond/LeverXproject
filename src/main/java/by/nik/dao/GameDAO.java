package by.nik.dao;

import by.nik.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class GameDAO {
//    Jedis jedis = new Jedis("localhost");
    @Autowired
    private Jedis jedis;

    @PreDestroy
    private void preDestroy() {
        jedis.close();
    }


    Game game;
    public GameDAO(Game game) {
        this.game = game;
    }

    public GameDAO() {
    }

    public void create(Game game) {
        // try -> boolean
        jedis.set("gameID:" + game.getId(), game.getName());
        jedis.sadd("games", game.getId());
    }

    public List<Game> readAll() {
        Set<String> gamesSet = jedis.smembers("games");
        List<Game> games = new ArrayList<>();
        for (String id : gamesSet) {
            Game game = new Game();
            game.setId(id);
            game.setName(jedis.get("gameID:" + id));
            games.add(game);
        }
        return games;
    }

    public void update(String gameId, String name){
        jedis.set("gameID:" + gameId, name);
        jedis.sadd("games", gameId);
    }

}
