package com.lxc.game.gobang.server.dao;

import com.lxc.game.gobang.server.dao.pojo.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Integer> {

    boolean existsByUsernameAndPassword(String username, String password);

    User findByUsernameAndPassword(String username, String password);

    @Query(value = "update user_base set user_password = ?2 where user_name = ?1", nativeQuery = true)
    void updatePasswordByUsername(String username, String password);

}
