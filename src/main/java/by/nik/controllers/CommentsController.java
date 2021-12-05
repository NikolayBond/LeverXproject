package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.models.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentsController {

//    @GetMapping("/articles/comments/new")
//    public String createForm(@ModelAttribute("comment") Comment comment) {
//        return "comments/comment_create";
//    }

/*    @PostMapping("/:{objectID}/comments")
    public String create(@PathVariable("objectID") Integer objectID,
                         @RequestParam("message") String message,
                         @RequestParam("author_id") Integer author_id,
                         @ModelAttribute("comment") Comment comment){
        // проверить есть ли objectID в БД
        // добавить в модель эти 3 параметра (objectID - это пост, мессаг, и юзера коммента)
        // записать модель в базу
        gameDAO.create(game);
        return "redirect:";
    }
*/

    // POST /articles/:id/comments

    @Autowired
    private final GameDAO gameDAO;
    public CommentsController(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }
    @GetMapping(value = "/7")
       public ResponseEntity<List<Game>> read() {
       final List<Game> games = gameDAO.readAll();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(games);
String gf = games.toString();
  return new ResponseEntity<>(games, HttpStatus.OK);

 //   return games != null &&  !games.isEmpty()
//            ? new ResponseEntity<>(games, HttpStatus.OK)
//            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

    @PostMapping(value = "/7")
    @ResponseBody
    public ResponseEntity<?> create(@RequestBody Game game) {
        gameDAO.create(game);
        return new ResponseEntity<>(game, HttpStatus.CREATED);
    }

//    POST /articles/:id/comments добавление

}
