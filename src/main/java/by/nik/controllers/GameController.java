package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.models.Game;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/games")
public class GameController {
// добавить контроль за ошибками (return) из gameDAO
    final GameDAO gameDAO;
    public GameController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }


    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("games", gameDAO.readAll());
        return "games/games_show_all";
    }

    @GetMapping("/new")
    public String createForm(@ModelAttribute("game") Game game) {
        return "games/game_create";
    }

    @PostMapping
    public String create(@ModelAttribute("game") Game game){
        gameDAO.create(game);
        return "redirect:/games";
    }

    @GetMapping("/:{gameID}/edit")
    public String editForm(@PathVariable("gameID") Integer gameID, @ModelAttribute("game") Game game){
        game.setId(gameID);
        game.setName(gameDAO.read(gameID).getName());
        return "games/game_edit";
    }

    @PutMapping("/:{gameID}")
    public String update(@PathVariable("gameID") Integer gameID, @RequestParam("name") String name,
            @ModelAttribute("game") Game game){
        game.setId(gameID);
        game.setName(name);
        gameDAO.update(game);
        return "redirect:/games";
    }

}
