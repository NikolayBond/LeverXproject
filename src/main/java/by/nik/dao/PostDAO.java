package by.nik.dao;

import by.nik.models.*;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class PostDAO {

    Post post;
    UserDAO userDAO;
    public PostDAO(Post post, UserDAO userDAO) {
        this.post = post;
        this.userDAO = userDAO;
    }

    public Post read(Integer postID) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Post post = session.get(Post.class, postID);
        session.close();
        return post;
    }

    public void create(Post post) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.save(post);
        tx.commit();
        session.close();
    }


    @SuppressWarnings("unchecked")
    public List<Post> readAll() throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Post> posts = session.createQuery("FROM Post ").list();
        session.close();
        return posts;
    }

    @SuppressWarnings("unchecked")
    public List<Post> readAllByUserID(Integer author_id) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Post> posts = session.createQuery("FROM Post where author_id = '" + author_id + "'").list();
        session.close();
        return posts;
    }

    //for admin
    @SuppressWarnings("unchecked")
    public List<Post> readAllUnchecked() throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Post> posts = session.createQuery("FROM Post where status = 'UNCHECKED'").list();
        session.close();
        return posts;
    }

    public void update(Post post) throws HibernateException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.update(post);
        tx.commit();
        session.close();
    }

    public void delete(Post post) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.delete(post);
        tx.commit();
        session.close();
    }

}
