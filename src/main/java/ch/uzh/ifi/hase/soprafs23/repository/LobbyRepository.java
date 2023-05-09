package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("lobbyRepository")
public interface LobbyRepository extends JpaRepository<Lobby, Long> {

    // Queries related to ALL lobbies
    Lobby findByLobbyName(String lobbyName);

    Lobby findByLobbyId(Long id);

    List<Lobby> findAll();

    List<Lobby> findAllByIsPublicAndIsJoinable(boolean isPublic, boolean isJoinable);


    // Queries related to BASIC lobbies
    @Query("from BasicLobby")
    List<BasicLobby> findAllBasicLobbies();

    @Query("from BasicLobby")
    BasicLobby findBasicLobbyByLobbyName(String lobbyName);

    @Query("from BasicLobby")
    BasicLobby findBasicLobbyById(Long id);


    // Queries related to ADVANCED lobbies
    @Query("from AdvancedLobby")
    List<AdvancedLobby> findAllAdvancedLobbies();

    @Query("from AdvancedLobby")
    AdvancedLobby findAdvancedLobbyByLobbyName(String lobbyName);

    @Query("from AdvancedLobby")
    AdvancedLobby findAdvancedLobbyById(Long id);
}
