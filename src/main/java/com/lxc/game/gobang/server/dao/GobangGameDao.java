package com.lxc.game.gobang.server.dao;

import com.lxc.game.gobang.server.dao.pojo.GobangGame;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GobangGameDao extends CrudRepository<GobangGame, UUID> {

}
