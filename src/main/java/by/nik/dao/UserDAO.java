package by.nik.dao;

import by.nik.models.Login;
import by.nik.models.User;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.List;
import java.util.UUID;

@Component
public class UserDAO {

    private User user;
    public UserDAO(User user) {
        this.user = user;
    }


    public String createConfirmationCode() {
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString();
    }

    public boolean create(User user, String confirmationCode) {
// add "url" to file par
        try (Jedis jedis = new Jedis("localhost")) {
            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}

            String originalPassword = user.getPassword();
            String generatedPasswordHash = BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));

            jedis.hset("user:" + confirmationCode, "first_name", user.getFirst_name());
            jedis.hset("user:" + confirmationCode, "last_name", user.getLast_name());
            jedis.hset("user:" + confirmationCode, "password", generatedPasswordHash);
            jedis.hset("user:" + confirmationCode, "email", user.getEmail());

            jedis.expire("user:" + confirmationCode, 10); // 86400

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean isConfirmedRegistrationLink(String confirmationCode, User user){
        try (Jedis jedis = new Jedis("localhost")) {
            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}
            if (jedis.exists("user:" + confirmationCode)) {
                user.setFirst_name(jedis.hget("user:" + confirmationCode, "first_name"));
                user.setLast_name(jedis.hget("user:" + confirmationCode, "last_name"));
                user.setPassword(jedis.hget("user:" + confirmationCode, "password"));
                user.setEmail(jedis.hget("user:" + confirmationCode, "email"));
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

    }

    public boolean save(User user) {
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            Transaction tx1 = session.beginTransaction();
                session.save(user);
            tx1.commit();
            session.close();
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public boolean isExistEmailInUserTable(String email){
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            if (session.createQuery("from User where email = '" + email + "'").list().size() > 0) {
                return true;
            }
            session.close();
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean setPasswordRecoveryLink(String confirmationCode, String email){
        try (Jedis jedis = new Jedis("localhost")) {
            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}
            jedis.set("password_recovery:" + confirmationCode, email);
            jedis.expire("password_recovery:" + confirmationCode, 25);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordRecoveryLink(String confirmationCode){
        try (Jedis jedis = new Jedis("localhost")) {
            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}
            if (jedis.exists("password_recovery:" + confirmationCode)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

// throws убрать если не будем возиться
    public boolean isPasswordReset(String confirmationCode, String new_password) throws Exception {
        // ЗАМЕНИТЬ отдельное введение пароля в иьюшке на модель user

        String generatedPasswordHash = BCrypt.hashpw(new_password, BCrypt.gensalt(12));

        try (Jedis jedis = new Jedis("localhost")) {
            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}

            String email = jedis.get("password_recovery:" + confirmationCode);

            try {
                Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
                Transaction tx1 = session.beginTransaction();
                    int rowsUpdated = session.createQuery("UPDATE User SET password = '"+ generatedPasswordHash +"' where email = '" + email + "'").executeUpdate();
                    if (rowsUpdated > 0) {
                        return true;
                    }
                tx1.commit();
                session.close();
                return false;
            } catch (HibernateException e) {
                return false;
            }
//            throw new JedisDataException("Jedis error");
        }
    }

    // Это нужно ТОЛЬКО ПОКА НЕТ СЕКУРИТИ - применяем в трейдерах
    //                return userId; при необходимости - доработать
    public Integer login(Login login){
        try {
            Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
            List<User> users = session.createQuery("from User where email = '" + login.getEmail() + "'").list();
            if (users.size() > 0) {
// это проверка на совпадение мыла и пароля в базе
// булин пока проверку пароля отключил BCrypt.checkpw(login.getPassword(), users.get(0).getPassword());
                return users.get(0).getId();
            }
            session.close();
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
