package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.models.Game;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/games")
public class GameController {
    final GameDAO gameDAO;
    public GameController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    // GET /games - в результате поиска должны быть игры
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Game> gameList = gameDAO.readAll();
            return new ResponseEntity<>(gameList, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // POST /games – добавить игру
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Game game) {
        if ((game.getName() == "") || (game.getName() == null)) {
            return new ResponseEntity<>("field 'name' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        if (gameDAO.isDuplicate(game.getName())){
            return new ResponseEntity<>("Duplicate - this game already present", HttpStatus.BAD_REQUEST);
        }
        try {
            gameDAO.create(game);
            return new ResponseEntity<>(game, HttpStatus.CREATED);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // PUT /games/:id – обновить игру
    @PutMapping("/{gameID}")
    public ResponseEntity<?> update(@PathVariable("gameID") Integer gameID, @RequestBody Game game) {
        try {
            if (gameDAO.read(gameID) == null) {
                return new ResponseEntity<>("Game (id) not found in database", HttpStatus.BAD_REQUEST);
            }
            if ((game.getName() == "") || (game.getName() == null)) {
                return new ResponseEntity<>("field 'name' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
            if (gameDAO.isDuplicate(game.getName())){
                return new ResponseEntity<>("Duplicate - this game already present", HttpStatus.BAD_REQUEST);
            }
            game.setId(gameID); // take from @PathVariable, not from body
            gameDAO.update(game);
            return new ResponseEntity<>(game, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
