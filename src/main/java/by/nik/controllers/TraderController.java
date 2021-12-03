package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.dao.TraderDAO;
import by.nik.dao.UserDAO;
import by.nik.models.Login;
import by.nik.models.Trader;
import by.nik.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/object")
public class TraderController {
    final TraderDAO traderDAO;
    final GameDAO gameDAO;
    final UserDAO userDAO;

    public TraderController(TraderDAO traderDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.traderDAO = traderDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("traders", traderDAO.readAll());
        return "traders/show_all";
    }

    @GetMapping("/new")
    public String createForm(@ModelAttribute("trader") Trader trader, @ModelAttribute("user") User user, Model model) {
        model.addAttribute("games", gameDAO.readAll());
        return "traders/trader_create";
    }

    @PostMapping
    public String create(@ModelAttribute("trader") @Valid Trader trader, BindingResult bindingResultTrader,
                         @ModelAttribute("user") @Valid Login login, BindingResult bindingResultUser,
                         @RequestParam("gameId") Integer gameId, Model model){
        model.addAttribute("games", gameDAO.readAll());

        if (bindingResultTrader.hasErrors() || bindingResultUser.hasErrors()) {
            return "traders/trader_create";
        }
        trader.setGame_id(gameId);
// что ниже - Это нужно ТОЛЬКО ПОКА НЕТ СЕКУРИТИ
        trader.setAuthor_id(userDAO.login(login));
        traderDAO.save(trader, login);
        return "home/index";
    }

}
