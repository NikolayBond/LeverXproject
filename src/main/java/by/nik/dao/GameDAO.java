package by.nik.dao;

import by.nik.models.Game;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameDAO {

    Game game;
    public GameDAO(Game game) {
        this.game = game;
    }

    public void create(Game game) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.save(game);
        tx.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public List<Game> readAll() throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<Game> games = session.createQuery("FROM Game").list();
        session.close();
        return games;
    }

    public Game read(Integer gameID) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Game game = session.get(Game.class, gameID);
        session.close();
        return game;
    }

    @SuppressWarnings("unchecked")
    public boolean isDuplicate(String name) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            List<Game> games = session.createQuery("from Game where name = '" + name + "'").list();
        session.close();
        return games.size() > 0;
    }

    public void update(Game game) throws HibernateException{
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.update(game);
        tx.commit();
        session.close();
    }

}
