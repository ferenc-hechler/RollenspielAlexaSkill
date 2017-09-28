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
package de.hechler.aigames.ai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import de.hechler.aigames.persist.GameRepository;
import de.hechler.aigames.persist.GameRepository.GameState;

public class GameRepositoryTest {

	@Test
	public void testGameRepository() {
		GameRepository repo = new GameRepository();
		GameState gameState1 = repo.createNewGame();
		GameState gameState2 = repo.createNewGame();
		String game1Id = gameState1.getGameId();
		String game2Id = gameState2.getGameId();
		assertNotEquals(game1Id, game2Id);
		GameState actual = repo.getGameStateByGameId(game2Id);
		assertEquals(gameState2, actual);
		actual = repo.getGameStateByGameId(game1Id);
		assertEquals(gameState1, actual);
		actual = repo.getGameStateByGameId(game2Id);
		assertEquals(gameState2, actual);
		actual = repo.getGameStateByGameId("x");
		assertNull(actual);
		
		repo.removeGame(game1Id);
		actual = repo.getGameStateByGameId(game2Id);
		assertEquals(gameState2, actual);
		actual = repo.getGameStateByGameId(game1Id);
		assertNull(actual);
		
	}


	@Test
	public void testRepoTimeout() {
		GameRepository repo = new GameRepository();
		GameState gameState1 = repo.createNewGame(-1000);
		GameState gameState2 = repo.createNewGame();
		String game1Id = gameState1.getGameId();
		String game2Id = gameState2.getGameId();
		assertNotEquals(game1Id, game2Id);
		GameState actual = repo.getGameStateByGameId(game2Id);
		assertEquals(gameState2, actual);
		actual = repo.getGameStateByGameId(game1Id);
		assertNull(actual);
		actual = repo.getGameStateByGameId(game2Id);
		assertEquals(gameState2, actual);
	}


	@Test
	public void testRepoClose() {
		GameRepository repo = new GameRepository();
		GameState gameState1 = repo.createNewGame();
		GameState gameState2 = repo.createNewGame();
		String game1Id = gameState1.getGameId();
		String game2Id = gameState2.getGameId();
		repo.close();
		GameState actual = repo.getGameStateByGameId(game1Id);
		assertNull(actual);
		actual = repo.getGameStateByGameId(game2Id);
		assertNull(actual);
	}

}
