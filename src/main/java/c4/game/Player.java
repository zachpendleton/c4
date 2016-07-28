package c4.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.IOException;

public interface Player {
    @JsonProperty
    String getName();

    @JsonIgnore
    int getNextMove();

    void sendMessage(String message) throws IOException;
}
