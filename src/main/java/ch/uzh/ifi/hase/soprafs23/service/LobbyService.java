package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.entity.AdvancedLobby;
import ch.uzh.ifi.hase.soprafs23.entity.BasicLobby;
import ch.uzh.ifi.hase.soprafs23.entity.Lobby;
import ch.uzh.ifi.hase.soprafs23.repository.LobbyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LobbyService {

    private final Logger log = LoggerFactory.getLogger(LobbyService.class);

    private final LobbyRepository lobbyRepository;

    @Autowired
    public LobbyService(@Qualifier("lobbyRepository") LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public BasicLobby createBasicLobby(Lobby basicLobby, String token) {

        basicLobby.setLobbyCreatorPlayerToken(token);
        Lobby savedLobby = lobbyRepository.save(basicLobby);
        lobbyRepository.flush();
        return (BasicLobby) savedLobby;
    }

    public AdvancedLobby createAdvancedLobby(Lobby advancedLobby, String token) {

        advancedLobby.setLobbyCreatorPlayerToken(token);
        Lobby savedLobby = lobbyRepository.save(advancedLobby);
        lobbyRepository.flush();
        return (AdvancedLobby) savedLobby;
    }
    

    public List<Lobby> getAllPublicLobbies() {
        return this.lobbyRepository.findAllByIsPublic(true);
    }

    public Lobby getLobbyById(long lobbyId) {
        return this.lobbyRepository.findByLobbyId(lobbyId);
    }
}
