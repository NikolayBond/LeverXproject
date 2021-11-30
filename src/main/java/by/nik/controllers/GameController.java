package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.models.Game;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/games")
public class GameController {
    final GameDAO gameDAO;

    public GameController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

//    @GetMapping
//    public String getAll(Model model) {
//        model.addAttribute("games", gameDAO.readAll());
//        return "users/games_show_all";
//    }

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Game> getAll() {
        return gameDAO.readAll();
    }


    @GetMapping("/new")
    public String createForm(@ModelAttribute("game") Game game) {
        return "users/game_create";
    }

    @PostMapping
    @ResponseBody
    public void create(@ModelAttribute("game") Game game){
        gameDAO.create(game);
    }

    @GetMapping("/:{gameID}/edit")
    public String editeForm(@PathVariable("gameID") String gameID, @ModelAttribute("game") Game game){
        game.setId(gameID);
        return "users/game_edit";
    }

    @PutMapping("/:{gameID}")
    @ResponseBody
    public void update(@PathVariable("gameID") String gameID, @RequestParam("name") String name){
        gameDAO.update(gameID, name);
    }

}
