package ch.uzh.ifi.hase.soprafs23.websocket;

import java.security.Principal;

class StompPrincipal implements Principal {
    private String name;

    StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
