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

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.Gson;

import de.hechler.aigames.ai.connectfour.ConnectFourImpl;
import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.GenericResult;
import de.hechler.aigames.api.GetGameDataResult;
import de.hechler.aigames.api.NewGameResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.fieldview.ConnectFourFieldView;
import de.hechler.aigames.api.move.ConnectFourMove;
import de.hechler.utils.RandUtils;

public class ConnectFourImplTest {

	@Test
	public void testAI1() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(1);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_AI_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("4433552", moves);
	}

	@Test
	public void testAI2() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(2);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_AI_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("2446554455345541122211221177777733", moves);
	}

	@Test
	public void testAI3() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(3);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_AI_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("44354521532342233415143111755777772266", moves);
	}

	@Test
	public void testAI4() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_AI_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("4265454453225543322345133211171177766", moves);
	}

	@Test
	public void testAI5() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(5);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_AI_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("333444433676141663646777772211221122", moves);
	}

	@Test
	@Ignore
	public void testAI6() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(6);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("433334464566446371515", moves);
	}

	@Test
	@Ignore
	public void testAI7() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(7);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		moves = moves + doAIMoveResult.move.slot;
		for (int i=0; i<100; i++) {
			doAIMoveResult = cfi.doAIMove(gameId);
			moves = moves + doAIMoveResult.move.slot;
			if (doAIMoveResult.code == ResultCodeEnum.S_PLAYER_WINS) {
				break;
			}
		}
		assertEquals("4333344467614143375566515", moves);
	}

	@Test
	public void testPlayAI2AgainstAI5() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(2);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String ai2GameId = newGameResult.gameId;
		newGameResult = cfi.createNewGame(5);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String ai5GameId = newGameResult.gameId;
		String moves = "";
		GenericResult genericResult;
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(ai2GameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		for (int i=0; i<100; i++) {
			moves = moves + doAIMoveResult.move.slot;
			genericResult = cfi.doMove(ai5GameId, new ConnectFourMove(doAIMoveResult.move.slot));
			assertEquals(genericResult.code, ResultCodeEnum.S_OK);
			doAIMoveResult = cfi.doAIMove(ai5GameId);
			if (doAIMoveResult.code != ResultCodeEnum.S_OK) {
				break;
			}
			moves = moves + doAIMoveResult.move.slot;
			genericResult = cfi.doMove(ai2GameId, new ConnectFourMove(doAIMoveResult.move.slot));
			assertEquals(genericResult.code, ResultCodeEnum.S_OK);
			doAIMoveResult = cfi.doAIMove(ai2GameId);
			if (doAIMoveResult.code != ResultCodeEnum.S_OK) {
				break;
			}
		}
		assertEquals("244426323367555", moves);
	}

	@Test
	public void testPlayChangingAI2andAI5() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(2);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId = newGameResult.gameId;
		String moves = "";
		GenericResult genericResult;
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(gameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		int currentAI = 2;
		for (int i=0; i<100; i++) {
			moves = moves + doAIMoveResult.move.slot;
			currentAI = 7 - currentAI;
			genericResult = cfi.setAILevel(gameId, currentAI);
			assertEquals(genericResult.code, ResultCodeEnum.S_OK);
			doAIMoveResult = cfi.doAIMove(gameId);
			if (doAIMoveResult.code != ResultCodeEnum.S_OK) {
				assertEquals(doAIMoveResult.code, ResultCodeEnum.S_AI_PLAYER_WINS);
				break;
			}
		}
		assertEquals("244426323367555", moves);
	}

	@Test
	public void testPlayAI5AgainstAI5() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(5);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String ai5aGameId = newGameResult.gameId;
		newGameResult = cfi.createNewGame(5);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String ai5bGameId = newGameResult.gameId;
		String moves = "";
		GenericResult genericResult;
		DoMoveResult<ConnectFourMove> doAIMoveResult = cfi.doAIMove(ai5aGameId);
		assertEquals(doAIMoveResult.code, ResultCodeEnum.S_OK);
		int cnt = 6*7;
		for (int i=0; i<100; i++) {
			moves = moves + doAIMoveResult.move.slot;
			cnt -=1;
			if (cnt < 1) {
				System.out.println("END");
			}
			genericResult = cfi.doMove(ai5bGameId, new ConnectFourMove(doAIMoveResult.move.slot));
			assertEquals(genericResult.code, ResultCodeEnum.S_OK);
			doAIMoveResult = cfi.doAIMove(ai5bGameId);
			if (doAIMoveResult.code != ResultCodeEnum.S_OK) {
				break;
			}
			moves = moves + doAIMoveResult.move.slot;
			cnt -=1;
			if (cnt < 1) {
				System.out.println("END");
			}
			genericResult = cfi.doMove(ai5aGameId, new ConnectFourMove(doAIMoveResult.move.slot));
			assertEquals(genericResult.code, ResultCodeEnum.S_OK);
			doAIMoveResult = cfi.doAIMove(ai5aGameId);
			if (doAIMoveResult.code != ResultCodeEnum.S_OK) {
				break;
			}
		}
		assertEquals("33344443367614166364677777221122112", moves);
	}

	@Test
	public void testDrawPlayer() {
		String moves = "12345612345612345671234561234561234567777";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length(); i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		
		// the last move!
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(7));
		assertEquals(ResultCodeEnum.S_DRAW, result.code);

		// game already finished
		result = cfi.doMove(gameId, new ConnectFourMove(7));
		assertEquals(ResultCodeEnum.E_GAME_FINISHED, result.code);

	}
	

	@Test
	public void testDrawAI() {
		String moves = "1234561234561234567123456123456123456";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length(); i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			if (result.code != ResultCodeEnum.S_OK) {
				System.out.println(fieldToString(cfi.getGameData(gameId).fieldView.field));
			}
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		
		// let the ai calc the last moves
		for (int i=0; i<4; i++) {
			DoMoveResult<ConnectFourMove> result = cfi.doAIMove(gameId);
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}

		// the last move!
		DoMoveResult<ConnectFourMove> result = cfi.doAIMove(gameId);
		assertEquals(ResultCodeEnum.S_AI_DRAW, result.code);

		// game already finished
		result = cfi.doAIMove(gameId);
		assertEquals(ResultCodeEnum.E_GAME_FINISHED, result.code);

	}


	@Test
	public void testSetPlayerNames() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(3);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		
		GenericResult result = cfi.setPlayerNames(gameId, "Tim", "Tom");
		assertEquals(ResultCodeEnum.S_OK, result.code);
		assertEquals("Tim", cfi.getGameData(gameId).player1Name);
		assertEquals("Tom", cfi.getGameData(gameId).player2Name);

		result = cfi.setPlayerNames(gameId, "Mit", "Mot");
		assertEquals(ResultCodeEnum.S_OK, result.code);
		assertEquals("Mit", cfi.getGameData(gameId).player1Name);
		assertEquals("Mot", cfi.getGameData(gameId).player2Name);
	}

	
	@Test
	public void testSetAILevel() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(5);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		
		assertEquals(5, cfi.getGameData(gameId).aiLevel);

		GenericResult result = cfi.setAILevel(gameId, 1);
		assertEquals(ResultCodeEnum.S_OK, result.code);
		assertEquals(1, cfi.getGameData(gameId).aiLevel);
	}

	
	@Test
	public void testCloseGame() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(3);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;

		GenericResult result = cfi.activateGame(gameId, null);
		assertEquals(ResultCodeEnum.S_OK, result.code);
		
		GetGameDataResult<ConnectFourFieldView> ggdResult = cfi.getGameData(gameId);
		assertEquals(ResultCodeEnum.S_OK, ggdResult.code);
		assertEquals(true, ggdResult.active);
		assertEquals(gameId, ggdResult.gameId);

		result = cfi.closeGame(gameId);
		assertEquals(ResultCodeEnum.S_OK, result.code);
		
		GetGameDataResult<ConnectFourFieldView> gdResult = cfi.getGameData(gameId);
		assertEquals(ResultCodeEnum.S_OK, gdResult.code);
		assertEquals(false, gdResult.active);
	}

	
	@Test
	public void testHasChanges() {
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(3);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		GetGameDataResult<ConnectFourFieldView> getGameDataresult = cfi.getGameData(gameId);
		int verion = getGameDataresult.version;
		GenericResult genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		
		cfi.setAILevel(gameId, 2);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		verion = cfi.getGameData(gameId).version;
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);

		cfi.setPlayerNames(gameId, "Tim", "Tom");
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		verion = cfi.getGameData(gameId).version;
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);

		cfi.doMove(gameId, new ConnectFourMove(4));
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		verion = cfi.getGameData(gameId).version;
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);

		cfi.doAIMove(gameId);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		verion = cfi.getGameData(gameId).version;
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);

		cfi.restart(gameId);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_CHANGES_EXIST, genericResult.code);
		verion = cfi.getGameData(gameId).version;
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);

		cfi.getGameData(gameId);
		genericResult = cfi.hasChanges(gameId, verion);
		assertEquals(ResultCodeEnum.S_NO_CHANGES, genericResult.code);


	}

	
	private String fieldToString(int[][] field) {
		StringBuilder result = new StringBuilder();
		for (int y=0; y<6; y++) {
			for (int x=0; x<7; x++) {
				result.append(Integer.toString(field[x][y]));
			}
			result.append("\n");
		}
		return result.toString();
	}


	@Test
	public void testField() {
		String moves = "1234567123456717263512322";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length(); i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,3,0,0,0,0,0],"
				+ "[0,2,0,0,0,0,0],"
				+ "[1,2,1,0,0,0,0],"
				+ "[1,1,1,0,2,2,2],"
				+ "[2,1,2,1,2,1,2],"
				+ "[1,2,1,2,1,2,1]]";
		assertEquals(expected, actual);

		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(1));
		assertEquals(ResultCodeEnum.S_OK, result.code);
		actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		expected = "["
				+ "[0,1,0,0,0,0,0],"
				+ "[4,2,0,0,0,0,0],"
				+ "[1,2,1,0,0,0,0],"
				+ "[1,1,1,0,2,2,2],"
				+ "[2,1,2,1,2,1,2],"
				+ "[1,2,1,2,1,2,1]]";
		assertEquals(expected, actual);

	}

	@Test
	public void testMarkConnectedFour1() {
		String moves = "1212121";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[3,0,0,0,0,0,0],"
				+ "[3,2,0,0,0,0,0],"
				+ "[3,2,0,0,0,0,0],"
				+ "[3,2,0,0,0,0,0]]";
				
		assertEquals(expected, actual);
	}

	@Test
	public void testMarkConnectedFour2() {
		String moves = "121231212121";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[4,0,0,0,0,0,0],"
				+ "[4,1,0,0,0,0,0],"
				+ "[4,1,0,0,0,0,0],"
				+ "[4,1,0,0,0,0,0],"
				+ "[1,2,0,0,0,0,0],"
				+ "[1,2,1,0,0,0,0]]";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMarkConnectedFour3() {
		String moves = "4455667";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,2,2,2,0],"
				+ "[0,0,0,3,3,3,3]]";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMarkConnectedFour4() {
		String moves = "334466775";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,2,2,0,2,2],"
				+ "[0,0,3,3,3,3,3]]";
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMarkConnectedFour5() {
		String moves = "12234334454";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,3,0,0,0],"
				+ "[0,0,3,1,0,0,0],"
				+ "[0,3,2,2,0,0,0],"
				+ "[3,2,2,1,2,0,0]]";
		assertEquals(expected, actual);
	}
	
	
	@Test
	public void testMarkConnectedFour6() {
		String moves = "76654554434";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,0,0,0,0],"
				+ "[0,0,0,3,0,0,0],"
				+ "[0,0,0,1,3,0,0],"
				+ "[0,0,0,2,2,3,0],"
				+ "[0,0,2,1,2,2,3]]";
		assertEquals(expected, actual);
	}

	
	
	@Test
	public void testMarkConnectedFour7() {
		String moves = "67677676767";
		RandUtils.setPRNG(1);
		ConnectFourImpl cfi = new ConnectFourImpl();
		NewGameResult newGameResult = cfi.createNewGame(4);
		assertEquals(ResultCodeEnum.S_OK, newGameResult.code);
		String gameId = newGameResult.gameId;
		for (int i=0; i<moves.length()-1; i++) {
			int slot = moves.charAt(i) - '0';
			GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
			assertEquals(ResultCodeEnum.S_OK, result.code);
		}
		int slot = moves.charAt(moves.length()-1) - '0';
		GenericResult result = cfi.doMove(gameId, new ConnectFourMove(slot));
		assertEquals(ResultCodeEnum.S_PLAYER_WINS, result.code);
		
		Gson gson = new Gson();
		String actual = gson.toJson(cfi.getGameData(gameId).fieldView.field);
		String expected = "["
				+ "[0,0,0,0,0,0,3],"
				+ "[0,0,0,0,0,2,3],"
				+ "[0,0,0,0,0,2,3],"
				+ "[0,0,0,0,0,2,3],"
				+ "[0,0,0,0,0,1,2],"
				+ "[0,0,0,0,0,1,2]]";
		assertEquals(expected, actual);
	}
}
