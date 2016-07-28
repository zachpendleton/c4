package c4.game;

import c4.game.exception.BoardException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Match {
    private static final AtomicInteger idSequence = new AtomicInteger(0);

    private static final Logger logger = LoggerFactory.getLogger(Match.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final SimpleTimeLimiter timeLimiter = new SimpleTimeLimiter();

    @JsonProperty
    private final int id;

    @JsonProperty
    private final Player[] players;

    @JsonProperty
    @JsonUnwrapped
    protected Board board;

    private int currentPlayerIndex = 0;

    private MatchState state;

    private Player winner;

    public Match(Player playerOne, Player playerTwo) {
        board = new Board();
        players = new Player[]{playerOne, playerTwo};
        state = MatchState.CREATED;
        id = idSequence.incrementAndGet();
    }

    @JsonIgnore
    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    @JsonIgnore
    public Player getNextPlayer() {
        int nextPlayerIndex = currentPlayerIndex == 1 ? 0 : 1;
        return players[nextPlayerIndex];
    }

    public MatchState getState() {
        return state;
    }

    public Player getWinner() {
        return winner;
    }

    @JsonIgnore
    public boolean isWon() {
        return state != MatchState.CREATED &&
               state != MatchState.IN_PROGRESS;
    }

    public void play() {
        try {
            state = MatchState.IN_PROGRESS;
            logger.info(toJSON());

            timeLimiter.callWithTimeout(() -> {
                playFor(getCurrentPlayer());
                return true;
            }, 8, TimeUnit.SECONDS, true);

            swapPlayers();
        } catch (BoardException e) {
            forfeit(MatchState.ERR_INVALID_MOVE);
        } catch (NoSuchElementException e) {
            forfeit(MatchState.ERR_NO_MOVE);
        } catch (IOException e) {
            forfeit(MatchState.ERR_PLAYER_DISCONNECTED);
        } catch (UncheckedTimeoutException e) {
            forfeit(MatchState.ERR_TIMEOUT);
        } catch (Exception e) {
            forfeit(MatchState.ERR_UNKNOWN);
        }
    }

    public Player[] getPlayers() {
        return Arrays.copyOf(players, 2);
    }

    public String toJSON() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage());
            return "{}";
        }
    }

    private void finish() {
        state = MatchState.FINISHED;
        winner = getCurrentPlayer();
    }

    private void forfeit(MatchState matchState) {
        state = matchState;
        winner = getNextPlayer();
    }

    public int getId() {
        return id;
    }

    @JsonProperty(value = "currentPlayer")
    private Integer getCurrentPlayerToken() {
        if (isWon()) {
            return null;
        }
        return currentPlayerIndex + 1;
    }

    private void playFor(Player player) throws BoardException, IOException, NoSuchElementException {
        player.sendMessage(toJSON());
        board.play(getCurrentPlayerToken(), player.getNextMove());
        if (board.hasWinner()) {
            finish();
        }
    }

    private void swapPlayers() {
        currentPlayerIndex = currentPlayerIndex == 1 ? 0 : 1;
    }

}
