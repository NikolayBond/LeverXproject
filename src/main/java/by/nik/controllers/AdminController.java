package by.nik.controllers;

import by.nik.dao.CommentDAO;
import by.nik.dao.PostDAO;
import by.nik.models.Comment;
import by.nik.models.Post;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    final PostDAO postDAO;
    final CommentDAO commentDAO;

    public AdminController(PostDAO postDAO, CommentDAO commentDAO) {
        this.postDAO = postDAO;
        this.commentDAO = commentDAO;
    }

    // show all unchecked posts
    @GetMapping("/posts")
    public ResponseEntity<?> showPostsNotApproved() {
        try {
            List<Post> posts = postDAO.readAllUnchecked();
            return new ResponseEntity<>(posts, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // set unchecked post as checked
    @PutMapping("/posts/{postID}")
    public ResponseEntity<?> setPostsAsChecked(@PathVariable("postID") Integer postID) {
        try {
            Post originalPost = postDAO.read(postID);
            if (originalPost == null) {
                return new ResponseEntity<>("post (id) not found in database", HttpStatus.BAD_REQUEST);
            }
            originalPost.setStatus(Post.Status.CHECKED);
            postDAO.update(originalPost);
            return new ResponseEntity<>(originalPost, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // show all unchecked comments
    @GetMapping("/comments")
    public ResponseEntity<?> showCommentsNotApproved() {
        try {
            List<Comment> comments = commentDAO.readAllUnchecked();
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    // set comments as approved
    @PutMapping("/comments/{commentID}")
    public ResponseEntity<?> setCommentsAsApproved(@PathVariable("commentID") Integer commentID) {
        try {
            Comment originalComment = commentDAO.read(commentID);
            if (originalComment == null) {
                return new ResponseEntity<>("Comment (id) not found in database", HttpStatus.BAD_REQUEST);
            }
            originalComment.setApproved(true);
            commentDAO.update(originalComment);
            return new ResponseEntity<>(originalComment, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
