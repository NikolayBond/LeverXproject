package by.nik.dao;

import by.nik.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Component
public class UserDAO {
//    @Autowired
//    private Jedis jedis;

    private User user;
    public UserDAO(User user) {
        this.user = user;
    }


    public boolean create(User user) {
        try (Jedis jedis = new Jedis("localhost")) {
            // check available server Redis
            if (!jedis.ping().equalsIgnoreCase("PONG")) {return false;}

            String originalPassword = user.getPassword();
            String generatedPasswordHash = BCrypt.hashpw(originalPassword, BCrypt.gensalt(12));

            //data structure for user
            jedis.hset("user:" + user.getId(), "first_name", user.getFirst_name());
            jedis.hset("user:" + user.getId(), "last_name", user.getLast_name());
            jedis.hset("user:" + user.getId(), "password", generatedPasswordHash);
            jedis.hset("user:" + user.getId(), "email", user.getEmail());
            jedis.hset("user:" + user.getId(), "created_at", user.getCreated_at().toString());
            jedis.hset("user:" + user.getId(), "role", user.getRole().toString());

            jedis.set("confirmRegistrationLink:" + user.getId(), user.getEmail());
            jedis.expire("confirmRegistrationLink:" + user.getId(), 10);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isConfirmedRegistration(Integer confirmationCode){
        Jedis jedis = new Jedis("localhost");
        if (jedis.exists("confirmRegistrationLink:" + confirmationCode)) {

            // set (and index) for active user (confirmed link) email <-> id
            jedis.set("email:" + jedis.get("confirmRegistrationLink:" + confirmationCode), confirmationCode.toString());
            return true;
        } else {
            return false;
        }
    }

    public boolean isExistEmailInActiveUserSet(String email){
        Jedis jedis = new Jedis("localhost");
        if (jedis.exists("email:" + email)) {
            return true;
        } else {
            return false;
        }
    }

    public void setPasswordRecoveryLink(int confirmationCode, String email){
        // isSetPasswordRecoveryLink - доработать ретурн при ошибке
        Jedis jedis = new Jedis("localhost");
        jedis.set("password_recovery:" + confirmationCode, email);
        jedis.expire("password_recovery:" + confirmationCode, 10);
    }

    public boolean isPasswordRecoveryLink(int confirmationCode){
        Jedis jedis = new Jedis("localhost");
        if (jedis.exists("password_recovery:" + confirmationCode)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isPasswordReset(int confirmationCode, String new_password){
        // ЗАМЕНИТЬ отдельное введение пароля в иьюшке на модель user

        String generatedPasswordHash = BCrypt.hashpw(new_password, BCrypt.gensalt(12));

        Jedis jedis = new Jedis("localhost");
        jedis.hset("user:" +
                        jedis.get("email:" + jedis.get("password_recovery:" + confirmationCode)),
                "password", generatedPasswordHash);

        return true; //добавить try
    }

}
