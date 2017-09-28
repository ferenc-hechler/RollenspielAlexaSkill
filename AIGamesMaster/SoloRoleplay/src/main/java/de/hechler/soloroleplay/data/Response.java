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
package de.hechler.soloroleplay.data;

import java.util.ArrayList;
import java.util.List;

import de.hechler.soloroleplay.SoloRoleplayGame;
import de.hechler.soloroleplay.util.TextUtil;

public class Response {

	
	public enum QuestionType {
		UNKNOWN, CONFIRMATION, YESNO, PHRASES, RIDDLE, FINISHED
	}
	
	private SoloRoleplayGame game;
	private StringBuilder text;
	private boolean error;
	private QuestionType type;
	private List<String> phrases;
	private boolean shortenTextActive = false; 
	
	public Response(SoloRoleplayGame game, boolean shortenTextActive) {
		this.game = game;
		this.text = new StringBuilder();
		this.type = QuestionType.UNKNOWN;
		this.phrases = null;
		this.error = false;
		this.shortenTextActive = shortenTextActive;
	}
	
	public StringBuilder getText() {
		return text;
	}

	public boolean isType(QuestionType compType) {
		return type == compType;
	}

	public Response addDescription(String descText) {
		TextUtil.addNotNull(text, game.handleVoices(game.handlePhonems(game.handleAudio(descText))));
		return this;
	}

	public Response addAudio(String audioUrl) {
		TextUtil.addNotNull(text, "AUDIO("+audioUrl+")");
		return this;
	}


	public void unknownChoice(List<String> possibleAnswers, String answer) {
		this.error = true;
		addDescription("Die Auswahl '"+answer+"' ist hier nicht möglich. Ich kenne: "+possibleAnswers);
	}

	public void unknownAnswer(String pleaseRepeatmessage) {
		this.error = true;
		addDescription(pleaseRepeatmessage);
	}

	public void setConfirmationType() {
		addPhrase("OK");
		type = QuestionType.CONFIRMATION;
	}

	public void setRiddleType() {
		type = QuestionType.RIDDLE;
	}

	private void addPhrase(String phrase) {
		if (phrases == null) {
			phrases = new ArrayList<String>();
		}
		phrases.add(phrase);
	}

	public boolean hasError() {
		return error;
	}

	public void stepFinished() {
		shortenText();
		game.currentStepFinished();
	}

	public void jumpToChapter(String chapterName) {
		shortenText();
		game.jumpToChapter(chapterName);
	}

	public void continueChapter(Response response, String chapterName) {
		jumpToChapter(chapterName);
		game.executeFromCurrent(response);
	}

	public void continueNextStep(Response response) {
		stepFinished();
		game.executeFromCurrent(response);
	}

	public void returnToChoice(Response response) {
		game.jumpToReturn();
		game.executeFromCurrent(response);
	}


	private void shortenText() {
		if (isShortenTextActive()) {
			text.setLength(0);
		}
	}

	private boolean isShortenTextActive() {
		return shortenTextActive;
	}

	
	public void setPossibleAnswers(List<String> possibleAnswers) {
		type = QuestionType.PHRASES;
		phrases = possibleAnswers;
	}

	
	public boolean checkChapterVisited(String chapterName, int minVisitCount) {
		return game.checkChapterVisited(chapterName, minVisitCount);
	}

	public boolean hasText() {
		return getText().length() > 0;
	}

	public void setFinished() {
		type = QuestionType.FINISHED;
	}

	public int incrementSubStepCount() {
		return game.incrementCurrentSubStep();
	}
	public int decrementSubStepCount() {
		return game.decrementCurrentSubStep();
	}
	public int getSubStepCount() {
		return game.getCurrentSubStep();
	}
	
	public void resetCurrentStepHelp() {
		game.resetCurrentStepHelp();
	}
	public void setCurrentStepHelp(int newValue) {
		game.setCurrentStepHelp(newValue);
	}
	public int getCurrentStepHelp() {
		return game.getCurrentStepHelp();
	}

	public void setReturnChapterAndStep() {
		game.setReturnChapterAndStep();
	}

	public void continueFromCurrent(Response response) {
		game.executeFromCurrent(response);
	}

}
