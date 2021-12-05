package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.dao.TraderDAO;
import by.nik.dao.UserDAO;
import by.nik.models.Login;
import by.nik.models.Trader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.Timestamp;

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
    public String createForm(@ModelAttribute("trader") Trader trader, @ModelAttribute("user") Login login, Model model) {
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

    @GetMapping("/:{traderID}/edit")
    public String editForm(@PathVariable("traderID") Integer traderID,
                           @ModelAttribute("trader") Trader trader, @ModelAttribute("user") Login login, Model model) {
        model.addAttribute("games", gameDAO.readAll());

        Trader traderOriginal = traderDAO.read(traderID);
        trader.setId(traderID);
        trader.setTitle(traderOriginal.getTitle());
        trader.setText(traderOriginal.getText());
        return "traders/edit";
    }

    @PutMapping("/:{traderID}")
    public String update(@PathVariable("traderID") Integer traderID,
                         @ModelAttribute("trader") @Valid Trader trader, BindingResult bindingResultTrader,
                         @ModelAttribute("user") @Valid Login login, BindingResult bindingResultUser,
                         @RequestParam("gameId") Integer gameId, Model model){
        model.addAttribute("games", gameDAO.readAll());

        if (bindingResultTrader.hasErrors() || bindingResultUser.hasErrors()) {
            return "traders/edit";
        }

//        if(userDAO.login(login) = ... ){ Если это тот же автор

        trader.setId(traderID);
        trader.setGame_id(gameId);
        trader.setUpdated_at(new Timestamp(System.currentTimeMillis()));

        Trader traderOriginal = traderDAO.read(traderID);
        trader.setCreated_at(traderOriginal.getCreated_at());
        trader.setStatus(traderOriginal.getStatus());

        // что ниже - Это нужно ТОЛЬКО ПОКА НЕТ СЕКУРИТИ
        trader.setAuthor_id(userDAO.login(login));

        traderDAO.update(trader);
        return "redirect:/object";
    }

    @DeleteMapping("/:{traderID}")
    public String delete(@PathVariable("traderID") Integer traderID) {
        traderDAO.delete(traderID);
        return "redirect:/object";
    }

}
