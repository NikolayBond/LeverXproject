package by.nik.controllers;

import by.nik.dao.CommentDAO;
import by.nik.dao.PostDAO;
import by.nik.dao.UserDAO;
import by.nik.models.Comment;
import by.nik.models.Post;
import by.nik.models.User;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@RestController
public class CommentsController {
    @Autowired
    private final PostDAO postDAO;
    private final UserDAO userDAO;
    private final CommentDAO commentDAO;

    public CommentsController(PostDAO postDAO, UserDAO userDAO, CommentDAO commentDAO) {
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.commentDAO = commentDAO;
    }


    // POST /articles/:id/comments - - добавить со ссылкой на пост и пользователя
    @PostMapping("/articles/{postID}/comments")
    public ResponseEntity<?> create(@PathVariable("postID") Integer postID, @RequestBody Comment comment, @AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
        try {
            Post post = postDAO.read(postID);
            if (post == null) {
                return new ResponseEntity<>("post not found in database", HttpStatus.BAD_REQUEST);
            }
            comment.setPost_id(post.getId()); // take from @PathVariable, not from body
            if ((comment.getMessage() == "") || (comment.getMessage() == null)) {
                return new ResponseEntity<>("field 'message' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }
            comment.setAuthor_id(users.get(0).getId());
//

            comment.setCreated_at(new Timestamp(System.currentTimeMillis()));
            comment.setApproved(false);
            commentDAO.create(comment);
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    //    GET /users/:id/comments - список отзывов трейдера
    @GetMapping("/users/{userID}/comments")
    public ResponseEntity<?> getAll(@PathVariable("userID") Integer userID) {
        try {
            List<Comment> comments = commentDAO.readAll(userID);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    //    GET /users/:id/comments/:id - просмотр отзыва, ID юзера излишний ???
    @GetMapping("/users/{userID}/comments/{commentID}")
    public ResponseEntity<?> get(@PathVariable("commentID") Integer commentID) {
        try {
            Comment comment = commentDAO.read(commentID);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    // DELETE /users /:id/comments/:id - удалить, удалить может только автор отзыва
    @DeleteMapping("/users/{userID}/comments/{commentID}")
    public ResponseEntity<?> delete(@PathVariable("commentID") Integer commentID,
                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
        try {
            Comment comment = commentDAO.read(commentID);
            if (comment == null) {
                return new ResponseEntity<>("comment id not found in database", HttpStatus.BAD_REQUEST);
            }
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }
            if (!Objects.equals(comment.getAuthor_id(), users.get(0).getId())) {
                return new ResponseEntity<>("Request rejected. Only author can delete", HttpStatus.BAD_REQUEST);
            }

            commentDAO.delete(comment);
            return new ResponseEntity<>("deleted", HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }


    // PUT /articles/:id/comments – обновить отзыв
    @PutMapping("/articles/{commentID}/comments")
    public ResponseEntity<?> update(@PathVariable("commentID") Integer commentID, @RequestBody Comment comment,
                                    @AuthenticationPrincipal org.springframework.security.core.userdetails.User userFromAuthentication) {
        try {
            Comment originalComment = commentDAO.read(commentID);
            if (originalComment == null) {
                return new ResponseEntity<>("comment not found in database", HttpStatus.BAD_REQUEST);
            }
            if ((comment.getMessage() == "") || (comment.getMessage() == null)) {
                return new ResponseEntity<>("field 'message' shouldn't be empty", HttpStatus.BAD_REQUEST);
            }
// Автора берем из аутентификации
            List<User> users = userDAO.readByEmail(userFromAuthentication.getUsername());
            if (users.size() != 1) {
                return new ResponseEntity<>("nonsense! -> author (from login) not found in database", HttpStatus.BAD_REQUEST);
            }
            if (!Objects.equals(comment.getAuthor_id(), users.get(0).getId())) {
                return new ResponseEntity<>("Request rejected. Only author can update", HttpStatus.BAD_REQUEST);
            }

            originalComment.setMessage(comment.getMessage());
            originalComment.setApproved(false);
            commentDAO.update(originalComment);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
