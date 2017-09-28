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

import java.util.List;

public class PersistentDataSoloRoleplayGame {

	private String chapter;
	private int step;
	private int subStep;
	
	private String answerChapter;
	private int answerStep;
	private int answerSubStep;
	
	private String returnChapter;
	private int returnStep;
	private List<String> history;
	

	public String getChapter() {
		return chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getSubStep() {
		return subStep;
	}

	public void setSubStep(int subStep) {
		this.subStep = subStep;
	}

	public String getAnswerChapter() {
		return answerChapter;
	}

	public void setAnswerChapter(String answerChapter) {
		this.answerChapter = answerChapter;
	}

	public int getAnswerStep() {
		return answerStep;
	}

	public void setAnswerStep(int answerStep) {
		this.answerStep = answerStep;
	}

	public int getAnswerSubStep() {
		return answerSubStep;
	}

	public void setAnswerSubStep(int answerSubStep) {
		this.answerSubStep = answerSubStep;
	}

	public String getReturnChapter() {
		return returnChapter;
	}

	public void setReturnChapter(String returnChapter) {
		this.returnChapter = returnChapter;
	}

	public int getReturnStep() {
		return returnStep;
	}

	public void setReturnStep(int returnStep) {
		this.returnStep = returnStep;
	}

	public List<String> getHistory() {
		return history;
	}

	public void setHistory(List<String> history) {
		this.history = history;
	}

	public String serialize() {
		return serialize(new Serializer()).getResult();
	}
	public boolean deserialize(String serializedData) {
		return !deserialize(new Deserializer(serializedData)).hasNext();
	}
	public static PersistentDataSoloRoleplayGame createInstance(String serializedData) {
		return createInstance(new Deserializer(serializedData));
	}

	public Serializer serialize(Serializer serializer) {
		serializer.addString("v2")
				.addString(getChapter())
				.addInt(getStep())
				.addInt(getSubStep())
				.addStringList(getHistory());
		return serializer;
	}

	public Deserializer deserialize(Deserializer deserializer) {
		String firstString = deserializer.nextString();
		if (!firstString.startsWith("v")) {
			deserializeV1(deserializer, firstString);
		}
		else if (firstString .equals("v2")) {
			deserializeV2(deserializer);
		}
		else {
			throw new RuntimeException("invalid version number '"+firstString+"' for persistentdata.");
		}
		return deserializer;
	}
	
	public Deserializer deserializeV1(Deserializer deserializer, String firstString) {
		setChapter(firstString);
		setStep(deserializer.nextInt());

		// initialize v2 fields with defaults:
		setSubStep(0);
		setAnswerChapter(getChapter());
		setAnswerStep(getStep());
		setAnswerSubStep(getSubStep());

		setHistory(deserializer.nextStringList());
		
		return deserializer;
	}
	
	public Deserializer deserializeV2(Deserializer deserializer) {
		setChapter(deserializer.nextString());
		setStep(deserializer.nextInt());
		setSubStep(deserializer.nextInt());
		setAnswerChapter(deserializer.nextString());
		setAnswerStep(deserializer.nextInt());
		setAnswerSubStep(deserializer.nextInt());
		setHistory(deserializer.nextStringList());
		return deserializer;
	}
	
	public static PersistentDataSoloRoleplayGame createInstance(Deserializer deserializer) {
		PersistentDataSoloRoleplayGame result = new PersistentDataSoloRoleplayGame();
		result.deserialize(deserializer);
		return result;
	}
	
}
