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
package de.hechler.aigames.ai.battleships;



import de.hechler.aigames.ai.GameRepository;
import de.hechler.aigames.ai.GameRepository.GameState;
import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.GameAPI;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.GetGameDataResult;
import de.hechler.aigames.api.GetGameParameterResult;
import de.hechler.aigames.api.NewGameResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.fieldview.BattleshipsFieldView;
import de.hechler.aigames.api.move.BattleshipsMove;


public class BattleshipsImpl implements GameAPI<BattleshipsFieldView, BattleshipsMove> {

	private static GameRepository<BattleshipsGame> gameRepository = new GameRepository<BattleshipsGame>(BattleshipsGame.class);
	
	@Override
	public NewGameResult createNewGame(int aiLevel) {
		return createNewGame(aiLevel, false);
	}

	@Override
	public NewGameResult createNewGame(int aiLevel, boolean weak) {
		GameState<BattleshipsGame> newGameState = gameRepository.createNewGame();
		newGameState.getGame().setAILevel(aiLevel);
		newGameState.getGame().setWeak(weak);
		NewGameResult result = new NewGameResult(ResultCodeEnum.S_OK, newGameState.getGameId());
		return result;
	}

	@Override
	public GenericResult setPlayerNames(String gameId, String player1Name, String player2Name) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		gameState.getGame().setPlayerNames(player1Name, player2Name);
		gameState.update();
		return GenericResult.genericOkResult;
	}

	@Override
	public GenericResult setAILevel(String gameId, int aiLevel) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		gameState.getGame().setAILevel(aiLevel);
		gameState.update();
		return GenericResult.genericOkResult;
	}

	@Override
	public GenericResult hasChanges(String gameId, int lastChange) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		if (gameState.getVersion() == lastChange) {
			return GenericResult.genericNoChangeResult;
		}
		return GenericResult.genericChangesExistResult;
	}

	@Override
	public GetGameDataResult<BattleshipsFieldView> getGameData(String gameId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GetGameDataResult<BattleshipsFieldView>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		BattleshipsGame game = gameState.getGame(); 
		GetGameDataResult<BattleshipsFieldView> result = new GetGameDataResult<BattleshipsFieldView>(ResultCodeEnum.S_OK);
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
		return result;
	}

	@Override
	public GetGameDataResult<BattleshipsFieldView> getGameDataByUserId(String userId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByUserId(userId);
		if (gameState == null) {
			return new GetGameDataResult<BattleshipsFieldView>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		BattleshipsGame game = gameState.getGame(); 
		GetGameDataResult<BattleshipsFieldView> result = new GetGameDataResult<BattleshipsFieldView>(ResultCodeEnum.S_OK);
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
		return result;
	}

	@Override
	public DoMoveResult<BattleshipsMove> doMove(String gameId, BattleshipsMove move) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		DoMoveResult<BattleshipsMove> result = gameState.getGame().doMove(move);
		gameState.update();
		return result;
	}

	@Override
	public DoMoveResult<BattleshipsMove> doAIMove(String gameId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new DoMoveResult<BattleshipsMove>(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		DoMoveResult<BattleshipsMove> result = gameState.getGame().doAIMove();
		gameState.update();
		return result;
	}

	@Override
	public GenericResult switchPlayers(String gameId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		boolean ok = gameState.getGame().switchPlayers();
		if (!ok) {
			return GenericResult.genericUnknownCommandResult;
		}
		gameState.update();
		return GenericResult.genericOkResult;
	}

	@Override
	public GenericResult restart(String gameId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		gameState.getGame().restartGame();
		gameState.update();
		return GenericResult.genericOkResult;
	}

	@Override
	public GenericResult closeGame(String gameId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		gameState.deactivate();
		return GenericResult.genericOkResult;
	}

	public void startup() {
	}
	public void shutdown() {
		gameRepository.close();
	}

	public GameState<BattleshipsGame> findGameId(String gameId) {
		return gameRepository.getGameStateByGameId(gameId);
	}

	@Override
	public GenericResult activateGame(String gameId, String userId) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		gameState.activate();
		gameRepository.connectUser(gameId, userId);
		return new NewGameResult(ResultCodeEnum.S_OK, gameId);
	}

	
	@Override
	public GenericResult changeGameParameter(String gameId, String gpName, String gpValue) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return GenericResult.genericUnknownGameId;
		}
		if (!gameState.getGame().changeGameParameter(gpName, gpValue)) {
			return GenericResult.genericInvalidParameterResult;
		}
		gameState.update();
		return GenericResult.genericOkResult;
	}

	@Override
	public GetGameParameterResult getGameParameter(String gameId, String gpName) {
		GameState<BattleshipsGame> gameState = gameRepository.getGameStateByGameId(gameId);
		if (gameState == null) {
			return new GetGameParameterResult(ResultCodeEnum.E_UNKNOWN_GAMEID);
		}
		return gameState.getGame().getGameParameter(gpName);
	}

	
}
