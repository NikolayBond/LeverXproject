package by.nik.controllers;

import by.nik.dao.RatingDAO;
import by.nik.models.Comment;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingController {
    final RatingDAO ratingDAO;
    public RatingController(RatingDAO ratingDAO) {
        this.ratingDAO = ratingDAO;
    }

    // get individual trader's rating
    @GetMapping("/{postID}")
    public ResponseEntity<?> getRatingByTraders(@PathVariable("postID") Integer postID) {
        try {
            List<Comment> comments = ratingDAO.readAllByPostID(postID);
            return new ResponseEntity<>(comments.size(), HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping
    public ResponseEntity<?> getCommonRating() {
        try {
            List<Integer> rating = ratingDAO.readCommonRating();
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } catch (HibernateException h) {
            return new ResponseEntity<>("Database error", HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
