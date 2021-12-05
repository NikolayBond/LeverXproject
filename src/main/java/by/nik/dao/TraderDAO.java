package by.nik.dao;

import by.nik.models.Game;
import by.nik.models.Login;
import by.nik.models.Trader;
import by.nik.models.User;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PreDestroy;
import java.util.List;


@Component
public class TraderDAO {

    Trader trader;
    UserDAO userDAO;
    public TraderDAO(Trader trader, UserDAO userDAO) {
        this.trader = trader;
        this.userDAO = userDAO;
    }


    public boolean save(Trader trader, Login login) {
//        if(userDAO.login(login)!=""){ Если пользователь существует
        System.out.println(">>>?>>" + trader.getAuthor_id()+trader.getGame_id()+trader.getId()+trader.getText()+trader.getTitle());
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
                session.save(trader);
            tx1.commit();
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }
//        }

    }

    @SuppressWarnings("unchecked")
    public List<Trader> readAll() {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            List<Trader> traders = session.createQuery("FROM Trader ").list();
            session.close();
            return traders;
        } catch (Exception e) {
            return null;
        }
    }

    public Trader read(Integer traderID) {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Trader trader = session.get(Trader.class, traderID);
            session.close();
            return trader;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean update(Trader trader){
//        if(userDAO.login(login) = ... ){ Если это тот же автор
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
                session.update(trader);
            tx1.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

    public boolean delete(Integer traderID){
//        if(userDAO.login(login) = ... ){ Если это тот же автор
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
                Trader trader = session.get(Trader.class, traderID);
                session.delete(trader);
            tx1.commit();
            session.close();
            return true;
        } catch (HibernateException e) {
            return false;
        }
    }

}
