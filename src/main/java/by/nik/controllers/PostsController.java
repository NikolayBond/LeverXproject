package by.nik.controllers;

import by.nik.dao.GameDAO;
import by.nik.dao.PostDAO;
import by.nik.dao.UserDAO;
import by.nik.models.Post;
import by.nik.models.User;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@RestController
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
    @PutMapping("/object/{postID}")
    public ResponseEntity<?> update(@PathVariable("postID") Integer postID, @RequestBody Post post,
                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
        try {
            Post originalPost = postDAO.read(postID);
            if (originalPost == null) {
                return new ResponseEntity<>("post (id) not found in database", HttpStatus.BAD_REQUEST);
            }
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }
            if (!Objects.equals(originalPost.getAuthor_id(), users.get(0).getId())) {
                return new ResponseEntity<>("Request rejected. Only author can update", HttpStatus.BAD_REQUEST);
            }

            if ((post.getTitle() == "") || (post.getTitle() == null)) {
                return new ResponseEntity<>("field 'title' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
            if ((post.getText() == "") || (post.getText() == null)) {
                return new ResponseEntity<>("field 'text' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
            originalPost.setTitle(post.getTitle());
            originalPost.setText(post.getText());
            originalPost.setUpdated_at(new Timestamp(System.currentTimeMillis()));
            originalPost.setStatus(Post.Status.UNCHECKED);
            postDAO.update(originalPost);
            return new ResponseEntity<>(originalPost, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    //    POST /object - Добавить объект
    @PostMapping("/object")
    public ResponseEntity<?> create(@RequestBody Post post,
                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
        if ((post.getTitle() == "") || (post.getTitle() == null)) {
            return new ResponseEntity<>("field 'title' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        if ((post.getText() == "") || (post.getText() == null)) {
            return new ResponseEntity<>("field 'text' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        if ((post.getGame_id() == 0) || (post.getGame_id() == null)) {
            return new ResponseEntity<>("field 'game_id' shouldn't be empty", HttpStatus.BAD_REQUEST);
        }
        try {
            if (gameDAO.read(post.getGame_id()) == null) {
                return new ResponseEntity<>("game (id) not found in database", HttpStatus.BAD_REQUEST);
            }
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }
            post.setAuthor_id(users.get(0).getId());

            post.setStatus(Post.Status.UNCHECKED);
            post.setCreated_at(new Timestamp(System.currentTimeMillis()));
            post.setUpdated_at(post.getCreated_at());
            postDAO.create(post);
            return new ResponseEntity<>(post, HttpStatus.CREATED);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


//    GET /object - получить игровые объекты
    @GetMapping("/object")
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
    public ResponseEntity<?> getAllByUserID(@AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
        try {
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }

            List<Post> posts = postDAO.readAllByUserID(users.get(0).getId());
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }

    }


//    DELETE /object/:id - удалить объект, удалить может только автор
    @DeleteMapping("/object/{postID}")
    public ResponseEntity<?> delete(@PathVariable("postID") Integer postID,
                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
// User берем из авторизации
        Integer author_id = 1;
        try {
            Post post = postDAO.read(postID);
            if (post == null) {
                return new ResponseEntity<>("post (id) not found in database", HttpStatus.BAD_REQUEST);
            }
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }
            if (!Objects.equals(post.getAuthor_id(), users.get(0).getId())) {
                return new ResponseEntity<>("Request rejected. Only author can delete", HttpStatus.BAD_REQUEST);
            }
            postDAO.delete(post);
            return new ResponseEntity<>("deleted", HttpStatus.OK);

        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
