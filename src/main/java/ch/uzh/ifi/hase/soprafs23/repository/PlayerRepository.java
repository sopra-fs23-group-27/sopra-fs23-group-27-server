package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("playerRepository")
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByPlayerName(String playerName);

    Player findById(long id);

    Player findByToken(String token);

    Player findByWsConnectionId(String wsConnectionId);

    boolean existsByToken(String token);

    List<Player> findByLobbyId(Long lobbyId);
}
