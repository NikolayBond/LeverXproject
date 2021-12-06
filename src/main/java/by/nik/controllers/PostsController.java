package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.dao.PostDAO;
import by.nik.dao.UserDAO;
import by.nik.models.Post;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/object")
public class PostsController {
    final PostDAO postDAO;
    final GameDAO gameDAO;
    final UserDAO userDAO;

    public PostsController(PostDAO postDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.postDAO = postDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }


    //    PUT /object/:id - Редактирование объекта, может только создатель поста
    @PutMapping("/{postID}")
    public ResponseEntity<?> update(@PathVariable("postID") Integer postID, @RequestBody Post post) {
        try {
            Post originalPost = postDAO.read(postID);
            if (originalPost == null) {
                return new ResponseEntity<>("post (id) not found in database", HttpStatus.BAD_REQUEST);
            }
            if ((post.getTitle() == "") || (post.getTitle() == null)) {
                return new ResponseEntity<>("field 'title' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
            if ((post.getText() == "") || (post.getText() == null)) {
                return new ResponseEntity<>("field 'text' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
            originalPost.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            originalPost.setStatus(Post.Status.UNCHECKED);
// Автора берем из авторизации
//        if(userDAO.login(login) = ... ){ Если это тот же автор
            postDAO.update(originalPost);
            return new ResponseEntity<>(post, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    //    POST /object - Добавить объект
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Post post) {
        if ((post.getTitle() == "") || (post.getTitle() == null)) {
            return new ResponseEntity<>("field 'title' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        if ((post.getText() == "") || (post.getText() == null)) {
            return new ResponseEntity<>("field 'text' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        post.setStatus(Post.Status.UNCHECKED);
// Автора берем из авторизации
        if ((post.getAuthor_id() == 0) || (post.getAuthor_id() == null)) {
            return new ResponseEntity<>("field 'author_id' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
//        if(userDAO... !=){ Если пользователь существует
// автор до верха
        post.setCreated_at(new Timestamp(System.currentTimeMillis()));
        post.setUpdated_at(post.getCreated_at());
        if ((post.getGame_id() == 0) || (post.getGame_id() == null)) {
            return new ResponseEntity<>("field 'game_id' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        try {
            if (gameDAO.read(post.getGame_id()) == null) {
                return new ResponseEntity<>("game (id) not found in database", HttpStatus.BAD_REQUEST);
            }
            postDAO.create(post);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

//    GET /object - получить игровые объекты
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Post> posts = postDAO.readAll();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

//    GET /my - получить список постов авторизованного пользователя
    @GetMapping("/my")
    public ResponseEntity<?> getAllByUserID() {
// User берем из авторизации
        Integer author_id = 1;
        try {
            List<Post> posts = postDAO.readAllByUserID(author_id);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }

    }


//    DELETE /object/:id - удалить объект, удалить может только автор
    @DeleteMapping("/{postID}")
    public ResponseEntity<?> delete(@PathVariable("postID") Integer postID) {
// User берем из авторизации
        Integer author_id = 1;
        try {
            Post post = postDAO.read(postID);
            if (post == null) {
                return new ResponseEntity<>("post (id) not found in database", HttpStatus.BAD_REQUEST);
            }
            if (post.getAuthor_id() == author_id) {
                postDAO.delete(post);
                return new ResponseEntity<>("deleted", HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>("only author can delete it", HttpStatus.BAD_REQUEST);
            }
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
