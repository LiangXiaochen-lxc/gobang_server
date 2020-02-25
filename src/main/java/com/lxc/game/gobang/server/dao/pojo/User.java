package com.lxc.game.gobang.server.dao.pojo;

import javax.persistence.*;

@Entity
@Table(name = "user_base")
public class User {

    @Id
    @GeneratedValue
    @Column(updatable = false)
    private Integer uid;

    @Column(name = "user_name", unique = true, updatable = false)
    private String username;

    @Column(name = "user_password", updatable = false)
    private String password;

    protected User(){

    }

    public User(String username, String password){
        this.password = password;
        this.username = username;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
