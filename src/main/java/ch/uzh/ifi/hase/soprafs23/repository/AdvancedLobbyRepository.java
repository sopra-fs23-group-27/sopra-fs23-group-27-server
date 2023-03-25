package ch.uzh.ifi.hase.soprafs23.repository;


import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvancedLobbyRepository extends JpaRepository<AdvancedLobby, Long> {

    @Query("from AdvancedLobby")
    public List<AdvancedLobby> findAll();
}
