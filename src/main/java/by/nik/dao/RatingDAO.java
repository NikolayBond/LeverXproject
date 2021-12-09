package by.nik.dao;

import by.nik.models.Comment;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RatingDAO {

    @SuppressWarnings("unchecked")
    public List<Comment> readAllByPostID(Integer postID) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Comment> comments = session.createQuery("from Comment where post_id = '" + postID + "'").list();
        session.close();
        return comments;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> readCommonRating() throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Integer> rating = session.createQuery("select c.post_id, COUNT(c) from Comment AS c GROUP BY c.post_id ORDER BY COUNT(c) desc ").list();
        session.close();
        return rating;
    }

}
