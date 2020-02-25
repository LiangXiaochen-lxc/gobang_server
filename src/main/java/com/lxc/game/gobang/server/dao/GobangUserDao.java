package com.lxc.game.gobang.server.dao;

import com.lxc.game.gobang.server.dao.pojo.GobangUser;
import com.lxc.game.gobang.server.dao.pojo.User;
import org.springframework.data.repository.CrudRepository;

public interface GobangUserDao extends CrudRepository<GobangUser, Integer> {

    GobangUser findByBase(User base);

}
