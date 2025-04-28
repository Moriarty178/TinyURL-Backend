package tiny_url.app.backend.entity;

import tiny_url.app.backend.annotation.MyColumn;
import tiny_url.app.backend.annotation.MyEntity;

@MyEntity(tableName = "users")
public class User {

    @MyColumn(name = "user_id", nullable = false)
    private Long id;

    @MyColumn(name = "user_name")
    private String name;

    @MyColumn(name = "email")
    private String email;

    public User(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User setValueEmail(String email) {
        this.email = email;
        return this;
    }
}
