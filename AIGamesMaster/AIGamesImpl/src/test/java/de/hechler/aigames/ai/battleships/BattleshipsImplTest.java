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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.gson.Gson;

import de.hechler.aigames.api.DoMoveResult;
import de.hechler.aigames.api.GetGameDataResult;
import de.hechler.aigames.api.NewGameResult;
import de.hechler.aigames.api.ResultCodeEnum;
import de.hechler.aigames.api.fieldview.BattleshipsFieldView;
import de.hechler.aigames.api.move.BattleshipsMove;
import de.hechler.utils.RandUtils;

public class BattleshipsImplTest {

	@Test
	public void testAI1() {
		RandUtils.setPRNG(1);
		BattleshipsImpl bsi = new BattleshipsImpl();
		NewGameResult newGameResult = bsi.createNewGame(1);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId1 = newGameResult.gameId;
		bsi.activateGame(gameId1, null);
		bsi.changeGameParameter(gameId1, BattleshipsMove.FIELD_SIZE_PARAMETER, "4");
		bsi.switchPlayers(gameId1);

		
		newGameResult = bsi.createNewGame(1);
		assertEquals(newGameResult.code, ResultCodeEnum.S_OK);
		String gameId2 = newGameResult.gameId;
		bsi.activateGame(gameId2, null);
		bsi.changeGameParameter(gameId2, BattleshipsMove.FIELD_SIZE_PARAMETER, "4");

		DoMoveResult<BattleshipsMove> dmResult;
		
		RandUtils.setPRNG(4);

		String moves = "";
		int player = 1;
		for (int i=0; i<1000; i++) {
			if (player == 1) {
				DoMoveResult<BattleshipsMove> aiMove1 = bsi.doAIMove(gameId1);
				assertEquals(ResultCodeEnum.S_OK, aiMove1.code);
				DoMoveResult<BattleshipsMove> answer2 = bsi.doMove(gameId2, aiMove1.move);
				moves += "A:"+shortMove(answer2.move) + " ";
				dmResult = bsi.doMove(gameId1, answer2.move);
				if (dmResult.code == ResultCodeEnum.S_OK) {
					player = 3-player;
				}
				else if (dmResult.code != ResultCodeEnum.S_CONTINUE) {
					break;
				}
			}
			else {
				DoMoveResult<BattleshipsMove> aiMove2 = bsi.doAIMove(gameId2);
				DoMoveResult<BattleshipsMove> answer1 = bsi.doMove(gameId1, aiMove2.move);
				moves += "B:"+shortMove(answer1.move) + " ";
				dmResult = bsi.doMove(gameId2, answer1.move);
				if (dmResult.code == ResultCodeEnum.S_OK) {
					player = 3-player;
				}
				else if (dmResult.code != ResultCodeEnum.S_CONTINUE) {
					break;
				}
			}
		}
		assertEquals("A:D1=WA B:C1=WA A:D4=WA B:B1=WA A:A4=SH A:B4=SU A:D3=WA B:D1=WA A:A1=WA B:B3=WA A:C2=WA B:C4=WA A:B1=SH A:B2=SU ", moves);
		               
		GetGameDataResult<BattleshipsFieldView> gameData1 = bsi.getGameData(gameId1);
		Gson gson = new Gson();
		String fielView1p1 = gson.toJson(gameData1.fieldView.p1Field);
		assertEquals("["
				+ "[1,4,4,3],"
				+ "[3,9,4,3],"
				+ "[4,1,4,4],"
				+ "[1,0,1,1]]", fielView1p1);
		String fielView1p2 = gson.toJson(gameData1.fieldView.p2Field);
		assertEquals("["
				+ "[0,0,0,0],"
				+ "[1,0,1,0],"
				+ "[1,0,0,7],"
				+ "[1,0,0,0]]", fielView1p2);
		               
		GetGameDataResult<BattleshipsFieldView> gameData2 = bsi.getGameData(gameId2);
		String fielView2p1 = gson.toJson(gameData2.fieldView.p1Field);
		assertEquals("["
				+ "[0,0,0,0],"
				+ "[1,0,1,0],"
				+ "[1,0,0,7],"
				+ "[1,0,0,0]]", fielView2p1);
		String fielView2p2 = gson.toJson(gameData2.fieldView.p2Field);
		assertEquals("["
				+ "[1,4,4,3],"
				+ "[3,9,4,3],"
				+ "[4,1,4,4],"
				+ "[1,0,1,1]]", fielView2p2);
		               
	}

	private String shortMove(BattleshipsMove move) {
		if (move == null) {
			return null;
		}
		return Character.toString(move.row)+move.col+"="+move.content.toString().substring(0, 2);
	}
}
