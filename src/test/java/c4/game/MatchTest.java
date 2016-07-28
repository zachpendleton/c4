package c4.game;

import c4.game.exception.BoardException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MatchTest {

    private Player mockPlayerOne;
    private Player mockPlayerTwo;
    private Match match;

    @Before
    public void createMatch() {
        mockPlayerOne = mock(Player.class);
        mockPlayerTwo = mock(Player.class);
        match = new Match(mockPlayerOne, mockPlayerTwo);

        when(mockPlayerOne.getName()).thenReturn("playerOne");
        when(mockPlayerTwo.getName()).thenReturn("playerTwo");
    }

    @Test
    public void playerOneStartsTheGame() {
        assertEquals(mockPlayerOne, match.getCurrentPlayer());
    }

    @Test
    public void playerTwoIsTheNextPlayer() {
        assertEquals(mockPlayerTwo, match.getNextPlayer());
    }

    @Test
    @Ignore
    public void itSendsMatchStateToCurrentPlayer() throws IOException {
        match.play();

        verify(mockPlayerOne).sendMessage(match.toJSON());
    }

    @Test
    public void itReadsNextMoveFromCurrentPlayer() {
        match.play();

        verify(mockPlayerOne).getNextMove();
    }

    @Test
    public void itPrintsJSON() {
        System.out.println(
        match.toJSON());
    }

    @Test()
    public void itForfeitsOnBoardException() throws BoardException {
        Board mockBoard = mock(Board.class);
        match.board = mockBoard;
        Mockito.doThrow(new BoardException()).when(mockBoard).play(any(Integer.class), any(Integer.class));

        match.play();

        assertEquals(MatchState.ERR_INVALID_MOVE, match.getState());
    }

    @Test
    public void itForfeitsOnIOException() throws IOException {
        when(mockPlayerOne.getNextMove()).thenThrow(IOException.class);

        match.play();

        assertEquals(MatchState.ERR_PLAYER_DISCONNECTED, match.getState());
    }

    @Test
    public void itAlternatesPlayersEachTimePlayIsCalled() {
        match.play();
        match.play();

        verify(mockPlayerOne).getNextMove();
        verify(mockPlayerTwo).getNextMove();
    }
}
