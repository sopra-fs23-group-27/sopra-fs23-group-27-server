package ch.uzh.ifi.hase.soprafs23.repository;


import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicLobbyRepository extends JpaRepository<BasicLobby, Long> {

    @Query("from BasicLobby")
    public List<BasicLobby> findAll();

    @Query("from BasicLobby")
    public BasicLobby findBasicLobbyByLobbyName(String lobbyName);

    @Query("from BasicLobby")
    public BasicLobby findBasicLobbyById(Long id);
}
