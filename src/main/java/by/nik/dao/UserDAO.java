package by.nik.dao;

import by.nik.models.User;
import by.nik.util.HibernateSessionFactoryUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import org.springframework.security.crypto.bcrypt.BCrypt;
import redis.clients.jedis.exceptions.JedisException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

@Component
public class UserDAO {
    final
    PasswordEncoder passwordEncoder;

    private User user;
    public UserDAO(User user, PasswordEncoder passwordEncoder) {
        this.user = user;
        this.passwordEncoder = passwordEncoder;
    }


    String redisUrl;
    {
        try {
            String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
            String defaultConfigPath = rootPath + "redis.properties";

            Properties properties = new Properties();
            try (FileInputStream fileInputStream = new FileInputStream(defaultConfigPath)) {
                properties.load(fileInputStream);
                this.redisUrl = properties.getProperty("redis.url", "localhost");
            } catch (IOException e) {
                this.redisUrl = "localhost";
            }
        } catch (NullPointerException n) {
            this.redisUrl = "localhost";
        }
    }


    public boolean create(User user, String confirmationCode) {
        try (Jedis jedis = new Jedis(redisUrl)) {
//            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}

            String originalPassword = user.getPassword();
//            String generatedPasswordHash = BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));
            String generatedPasswordHash = passwordEncoder.encode(originalPassword);

            jedis.hset("user:" + confirmationCode, "first_name", user.getFirst_name());
            jedis.hset("user:" + confirmationCode, "last_name", user.getLast_name());
            jedis.hset("user:" + confirmationCode, "password", generatedPasswordHash);
            jedis.hset("user:" + confirmationCode, "email", user.getEmail());

            jedis.expire("user:" + confirmationCode, 25); // 86400

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isPresent(String email) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<User> users = session.createQuery("from User where email = '" + email + "'").list();
        session.close();
        return users.size() > 0;
    }


    public boolean isConfirmedRegistrationLink(String confirmationCode, User user) throws JedisException {
        Jedis jedis = new Jedis(redisUrl);
        if (jedis.exists("user:" + confirmationCode)) {
            user.setFirst_name(jedis.hget("user:" + confirmationCode, "first_name"));
            user.setLast_name(jedis.hget("user:" + confirmationCode, "last_name"));
            user.setPassword(jedis.hget("user:" + confirmationCode, "password"));
            user.setEmail(jedis.hget("user:" + confirmationCode, "email"));
            jedis.close();
            return true;
        } else {
            jedis.close();
            return false;
        }
    }


    public void save(User user) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            session.save(user);
        tx.commit();
        session.close();
    }


    @SuppressWarnings("unchecked")
    public List<User> readByEmail(String email) throws HibernateException {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        List<User> users = session.createQuery("from User where email = '" + email + "'").list();
        session.close();
        return users;
    }


    public void setPasswordRecoveryLink(String confirmationCode, String email) throws JedisException {
        Jedis jedis = new Jedis(redisUrl);
        jedis.set("password_recovery:" + confirmationCode, email);
        jedis.expire("password_recovery:" + confirmationCode, 25);
        jedis.close();
    }


    public boolean isPasswordRecoveryLink(String code) throws JedisException {
        Jedis jedis = new Jedis(redisUrl);
        if (jedis.exists("password_recovery:" + code)) {
            jedis.close();
            return true;
        } else {
            jedis.close();
            return false;
        }
    }


    public boolean isPasswordReset(String code, String new_password) throws JedisException, HibernateException {
        String generatedPasswordHash = passwordEncoder.encode(new_password);

        Jedis jedis = new Jedis(redisUrl);
        String email = jedis.get("password_recovery:" + code);
        jedis.close();

        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
            int rowsUpdated = session.createQuery("UPDATE User SET password = '"+ generatedPasswordHash +"' where email = '" + email + "'").executeUpdate();
        tx.commit();
        session.close();
        if (rowsUpdated > 0) {
            return true;
        }
        return false;
    }

}
