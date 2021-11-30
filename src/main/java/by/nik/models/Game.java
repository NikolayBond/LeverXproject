package by.nik.models;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Game {
    private String id; //UID
    private String name;

    public Game() {
        UUID uniqueKey = UUID.randomUUID();
//        this.id = uniqueKey.hashCode();
        this.id = uniqueKey.toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    //        ("CS:GO", "Fifa", "Dota", "Team Fortress"));
}
