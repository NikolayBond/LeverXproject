package by.nik.dao;

import by.nik.models.Comment;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentDAO {
    Comment comment;
    public CommentDAO(Comment comment) {
        this.comment = comment;
    }

    public void create(Comment comment) throws HibernateException {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
                session.save(comment);
            tx.commit();
            session.close();
    }

    @SuppressWarnings("unchecked")
    public List<Comment> readAll(Integer userID) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Comment> comments = session.createQuery("from Comment where author_id = '" + userID + "'").list();
        session.close();
        return comments;
    }

    public Comment read(Integer commentID) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Comment comment = session.get(Comment.class, commentID);
        session.close();
        return comment;
    }

    public void delete(Comment comment) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.delete(comment);
        tx.commit();
        session.close();
    }

    public void update(Comment comment) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.update(comment);
        tx.commit();
        session.close();
    }

}
