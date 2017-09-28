/**
 * Diese Datei ist Teil des Alexa Skills Rollenspiel Soloabenteuer.
 * Copyright (C) 2016-2017 Ferenc Hechler (github@fh.anderemails.de)
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer ist Freie Software: 
 * Sie koennen es unter den Bedingungen
 * der GNU General Public License, wie von der Free Software Foundation,
 * Version 3 der Lizenz oder (nach Ihrer Wahl) jeder spaeteren
 * veroeffentlichten Version, weiterverbreiten und/oder modifizieren.
 *
 * Der Alexa Skills Rollenspiel Soloabenteuer wird in der Hoffnung, 
 * dass es nuetzlich sein wird, aber
 * OHNE JEDE GEWAEHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 * Gewaehrleistung der MARKTFAEHIGKEIT oder EIGNUNG FUER EINEN BESTIMMTEN ZWECK.
 * Siehe die GNU General Public License fuer weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.hechler.aigames.ai.connectfour;



import de.hechler.aigames.ai.GameRepository;
import de.hechler.aigames.ai.GameRepository.GameState;
import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.GameAPI;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.GetGameDataResult;
import de.hechler.aigames.api.GetGameParameterResult;
import de.hechler.aigames.api.NewGameResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.fieldview.ConnectFourFieldView;
import de.hechler.aigames.api.move.BattleshipsMove;
import de.hechler.aigames.api.move.ConnectFourMove;


public class ConnectFourImpl implements GameAPI<ConnectFourFieldView, ConnectFourMove> {

	private static GameRepository<ConnectFourGame> gameRepository = new GameRepository<ConnectFourGame>(ConnectFourGame.class);
	
	@Override
	public NewGameResult createNewGame(int aiLevel) {
		return createNewGame(aiLevel, false);
	}

	@Override
	public NewGameResult createNewGame(int aiLevel, boolean weak) {
		GameState<ConnectFourGame> newGameState = gameRepository.createNewGame();
		newGameState.getGame().setAILevel(aiLevel);
		newGameState.getGame().setWeak(weak);
		NewGameResult result = new NewGameResult(ResultCodeEnum.S_OK, newGameState.getGameId());
		return result;
	}

	@Override
	public GenericResult setPlayerNames(String gameId, String player1Name, String player2Name) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		gameState.getGame().setPlayerNames(player1Name, player2Name);
		gameState.update();
		return new GenericResult(ResultCodeEnum.S_OK);
	}

	@Override
	public GenericResult setAILevel(String gameId, int aiLevel) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		gameState.getGame().setAILevel(aiLevel);
		gameState.update();
		return new GenericResult(ResultCodeEnum.S_OK);
	}

	@Override
	public GenericResult hasChanges(String gameId, int lastChange) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		if (gameState.getVersion() == lastChange) {
			return new GenericResult(ResultCodeEnum.S_NO_CHANGES);
		}
		return new GenericResult(ResultCodeEnum.S_CHANGES_EXIST);
	}

	@Override
	public GetGameDataResult<ConnectFourFieldView> getGameData(String gameId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GetGameDataResult<ConnectFourFieldView>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		ConnectFourGame game = gameState.getGame(); 
		GetGameDataResult<ConnectFourFieldView> result = new GetGameDataResult<ConnectFourFieldView>(ResultCodeEnum.S_OK);
		result.aiLevel = game.getAILevel();
		result.player1Name = game.getPlayer1Name();
		result.player2Name = game.getPlayer2Name();
		result.currentPlayer = game.getCurrentPlayer();
		result.gamePhase = game.getGamePhase();
		result.fieldView = game.getField();
		result.version = gameState.getVersion();
		result.active= gameState.isActive();
		result.gameId = gameState.getGameId();
		result.winner = game.getWinner();
		if (result.winner > 0) {
			markConnectedFour(result.fieldView.field);
		}
		return result;
	}
	
	@Override
	public GetGameDataResult<ConnectFourFieldView> getGameDataByUserId(String userId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByUserId(userId);
		if (gameState == null) {
			return new GetGameDataResult<ConnectFourFieldView>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		ConnectFourGame game = gameState.getGame(); 
		GetGameDataResult<ConnectFourFieldView> result = new GetGameDataResult<ConnectFourFieldView>(ResultCodeEnum.S_OK);
		result.aiLevel = game.getAILevel();
		result.player1Name = game.getPlayer1Name();
		result.player2Name = game.getPlayer2Name();
		result.currentPlayer = game.getCurrentPlayer();
		result.gamePhase = game.getGamePhase();
		result.fieldView = game.getField();
		result.version = gameState.getVersion();
		result.active= gameState.isActive();
		result.gameId = gameState.getGameId();
		result.winner = game.getWinner();
		if (result.winner > 0) {
			markConnectedFour(result.fieldView.field);
		}
		return result;
	}



	void markConnectedFour(int[][] field) {
		for (int y=0; y<6; y++) {
			for (int x=0; x<7; x++) {
				if (x<4) {
					check(field, x,y, +1,  0 );
				}
				if (y<3) {
					check(field, x,y,  0, +1 );
				}
				if ((x<4) && (y<3)) {
					check(field, x,y, +1, +1 );
				}
				if ((x>2) && (y<3)) {
					check(field, x,y, -1, +1 );
				}
			}
		}
	}

	private void check(int[][] field, int x, int y, int dx, int dy) {
		int p = trunc(field[y][x]); 
		if ((p > 0) && 
			(p == trunc(field[y+  dy][x+  dx])) &&
			(p == trunc(field[y+2*dy][x+2*dx])) &&
			(p == trunc(field[y+3*dy][x+3*dx]))) 
		{
			field[y     ][x     ] = p + 2;
			field[y+  dy][x+  dx] = p + 2;
			field[y+2*dy][x+2*dx] = p + 2;
			field[y+3*dy][x+3*dx] = p + 2;
		}
	}

	private int trunc(int p) {
		return (p > 2) ? (p-2) : p;
	}

	@Override
	public DoMoveResult<ConnectFourMove> doMove(String gameId, ConnectFourMove move) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		DoMoveResult<ConnectFourMove> result = gameState.getGame().doMove(move);
		gameState.update();
		return result;
	}

	@Override
	public DoMoveResult<ConnectFourMove> doAIMove(String gameId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new DoMoveResult<ConnectFourMove>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		DoMoveResult<ConnectFourMove> result = gameState.getGame().doAIMove();
		gameState.update();
		return result;
	}

	@Override
	public GenericResult switchPlayers(String gameId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		boolean ok = gameState.getGame().switchPlayers();
		if (!ok) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_COMMAND);
		}
		gameState.update();
		return new GenericResult(ResultCodeEnum.S_OK);
	}

	@Override
	public GenericResult restart(String gameId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		gameState.getGame().restartGame();
		gameState.update();
		return new GenericResult(ResultCodeEnum.S_OK);
	}

	@Override
	public GenericResult closeGame(String gameId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		gameState.deactivate();
		return new GenericResult(ResultCodeEnum.S_OK);
	}

	public void startup() {
	}
	public void shutdown() {
		gameRepository.close();
	}


	public GameState<ConnectFourGame> findGameId(String gameId) {
		return gameRepository.getGameStateByGameId(gameId);
	}

	@Override
	public GenericResult activateGame(String gameId, String userId) {
		GameState<ConnectFourGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GenericResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		gameState.activate();
		gameRepository.connectUser(gameId, userId);
		return new NewGameResult(ResultCodeEnum.S_OK, gameId);
	}

	@Override
	public GenericResult changeGameParameter(String gameId, String gpName, String gpValue) {
		return GenericResult.genericInvalidParameterResult;
	}

	@Override
	public GetGameParameterResult getGameParameter(String gameId, String paramName) {
		return new GetGameParameterResult(ResultCodeEnum.E_INVALID_PARAMETER);
	}


}
