package by.nik.controllers;

import by.nik.dao.TraderDAO;
import by.nik.models.Trader;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/object")
public class TraderController {
    TraderDAO traderDAO;
    public TraderController(TraderDAO traderDAO) {
        this.traderDAO = traderDAO;
    }

    @GetMapping("/new")
    public String createForm(@ModelAttribute("trader") Trader trader) {
        return "traders/trader_create";
    }

    @PostMapping
    public String create(@ModelAttribute("trader") @Valid Trader trader, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "traders/trader_create";
        }
        traderDAO.create(trader);
        return "traders/trader_create";
    }

}
