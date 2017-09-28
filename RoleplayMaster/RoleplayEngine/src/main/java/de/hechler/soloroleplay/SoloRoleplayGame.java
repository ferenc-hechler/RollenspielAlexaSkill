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
package de.hechler.soloroleplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hechler.soloroleplay.data.Chapter;
import de.hechler.soloroleplay.data.Response;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.Step;

public class SoloRoleplayGame {

	private final SoloRoleplayData solo;
	
	private List<String> history;
	private String returnChapter;
	private int returnStep;
	private String lastChapter;
	private int lastStep;
	private int lastSubStep;
	
	
	private int lastHistorySize;
	private Chapter currentChapter;
	private int currentStep;
	private int currentSubStep;
	private int currentStepHelp;

	public SoloRoleplayGame(SoloRoleplayData solo) {
		this.solo = solo;
		restart();
	}

	public void restart() {
		this.lastChapter = "BEGINN";
		this.lastStep = 0;
		this.lastSubStep = 0;
		this.returnChapter = lastChapter;
		this.returnStep = 0;
		this.history = new ArrayList<String>();
		this.history.add(lastChapter);
		this.lastHistorySize = this.history.size();
		this.currentChapter = null;
		this.currentStep = -1;
		this.currentSubStep = 0;
		this.currentStepHelp = 0;
	}

	
	private Chapter getChapter(String chapterName) {
		return solo.getChapter(chapterName);
	}

	public Chapter getChapter(int chapterNumber) {
		return solo.getChapter(chapterNumber);
	}

	public boolean isFinished() {
		if (currentChapter == null) {
			return false;
		}
		return currentChapter.getName().equals("ENDE");
	}

	public Response executeFromLast() {
		return executeFromLast(false);
	}
	
	public Response executeFromLast(boolean shortenTextActive) {
		Response result = new Response(this, shortenTextActive);
		currentChapter = getChapter(lastChapter);
		currentStep = lastStep;
		currentSubStep = lastSubStep;
		while(history.size() > lastHistorySize) {
			history.remove(history.size()-1);
		}
		executeFromCurrent(result);
		return result;
	}
	
	public int saveLastStepForRepeat() {
		int result = lastStep;
		if (lastStep == 1) {
			lastStep = 0;
		}
		return result;
	}
	public void resetLastStepAfterRepeat(int savedLastStep) {
		lastStep = savedLastStep;
	}

	public int saveLastSubStepForRepeat() {
		int result = lastSubStep;
		lastSubStep = 0;
		return result;
	}
	public void resetLastSubStepAfterRepeat(int savedLastSubStep) {
		currentSubStep = savedLastSubStep;
	}

	
	public void executeFromCurrent(Response response) {
		currentChapter.execute(response, currentStep);
	}

	public Response processAnswer(String answer) {
		return processAnswer(answer, false);
	}
	
	public Response processAnswer(String answer, boolean shortenTextActive) {
		Response result = new Response(this, shortenTextActive);
		answer = answer.toUpperCase(Locale.GERMAN).trim();
		Step step = currentChapter.getStep(currentStep);
		if (step != null) {
			step.processAnswer(result, answer);
		}
		if (!result.hasError()) {
			lastChapter = currentChapter.getName();
			lastStep = currentStep;
			lastSubStep = currentSubStep;
			lastHistorySize = history.size();
		}
		return result;
	}

	public void currentStepFinished() {
		currentStep += 1;
		currentSubStep = 0;
	}

	public Chapter jumpToChapter(String chapterName) {
		Chapter chapter = solo.getChapter(chapterName);
		if (chapter == null) {
			throw new RuntimeException("UNKNWON CHAPTER REFERENCE '"+chapterName+"' in "+currentChapter.getName());
		}
		currentChapter = chapter;
		currentStep = 0;
		currentSubStep = 0;
		history.add(currentChapter.getName());
		return currentChapter;
	}

	public void directJump(String chapterName, int step) {
		Chapter chapter = solo.getChapter(chapterName);
		if (chapter == null) {
			throw new RuntimeException("UNKNWON CHAPTER REFERENCE '"+chapterName+"' in "+currentChapter.getName());
		}
		currentChapter = chapter;
		currentStep = step;
		currentSubStep = 0;
		history.add(currentChapter.getName());

		lastChapter = currentChapter.getName();
		lastStep = currentStep;
		lastSubStep = currentSubStep;
		lastHistorySize = history.size();
		
	}	
	
	
	public void setReturnChapterAndStep() {
		returnChapter = currentChapter.getName();
		returnStep = currentStep;
	}

	public void jumpToReturn() {
		Chapter chapter = solo.getChapter(returnChapter);
		currentChapter = chapter;
		currentStep = returnStep;
		currentSubStep = 0;
		history.add(currentChapter.getName());
	}
	


	
	public boolean checkChapterVisited(String chapterName, int minVisitCount) {
		int cnt = 0;
		for (String historyChapterName:history) {
			if (historyChapterName.equals(chapterName)) {
				cnt += 1;
				if (cnt >= minVisitCount) {
					return true;
				}
			}
		}
		return false;
	}


	public int incrementCurrentSubStep() {
		currentSubStep += 1;
		return currentSubStep;
	}
	public int decrementCurrentSubStep() {
		currentSubStep -= 1;
		return currentSubStep;
	}
	public int getCurrentSubStep() {
		return currentSubStep;
	}

	public void resetCurrentStepHelp() {
		currentStepHelp = 0;
	}
	public void setCurrentStepHelp(int newValue) {
		currentStepHelp = newValue;
	}
	public int getCurrentStepHelp() {
		return currentStepHelp;
	}

	public PersistentDataSoloRoleplayGame getPersistentData() {
		PersistentDataSoloRoleplayGame result = new PersistentDataSoloRoleplayGame();
		result.setChapter(lastChapter);
		result.setStep(lastStep);
		result.setReturnChapter(returnChapter);
		result.setReturnStep(returnStep);
		List<String> historyCopy = new ArrayList<String>(history);
		while(historyCopy.size() > lastHistorySize) {
			historyCopy.remove(historyCopy.size()-1);
		}
		result.setHistory(historyCopy);
		return result; 
	}
	
	public void restoreFromPersistentData(PersistentDataSoloRoleplayGame persistentData) {
		this.lastChapter = persistentData.getChapter();
		this.lastStep = persistentData.getStep();
		this.returnChapter = persistentData.getReturnChapter();
		this.returnStep = persistentData.getReturnStep();
		this.history = new ArrayList<String>(persistentData.getHistory());
		this.lastHistorySize = this.history.size();
		this.currentChapter = getChapter(lastChapter);
		this.currentStep = lastStep;
		this.currentSubStep = lastSubStep;
		this.currentStepHelp = 0;
	}

	public String handlePhonems(String text) {
		if ((text == null) || (solo == null)) {
			return text;
		}
		return solo.handlePhonems(text);
	}

	public String handleVoices(String text) {
		if ((text == null) || (solo == null)) {
			return text;
		}
		return solo.handleVoices(text);
	}

	public String handleAudio(String text) {
		if ((text == null) || (solo == null)) {
			return text;
		}
		return solo.handleAudio(text);
	}

}
