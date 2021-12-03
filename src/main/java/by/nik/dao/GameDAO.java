package by.nik.dao;

import by.nik.models.Game;
import by.nik.util.HibernateSessionFactoryUtil;
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

    public GameDAO() {
    }

    public boolean create(Game game) {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
                session.save(game);
            tx1.commit();
            session.close();
            return true;
            } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Game> readAll() {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            List<Game> games = session.createQuery("FROM Game").list();
            session.close();
            return games;
        } catch (Exception e) {
            return null;
        }
    }

    public Game read(Integer gameID) {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Game game = session.get(Game.class, gameID);
            session.close();
            return game;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean update(Game game){
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
                session.update(game);
            tx1.commit();
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
