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
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import de.hechler.soloroleplay.util.TextUtil;

public class Choice {

	private String name;
	private String text;
	private String chapterRef;
	private List<String> phrases;
	
	public Choice(String name, String text, String chapterRef) {
		this.name = name;
		this.text = text;
		this.chapterRef = chapterRef;
		this.phrases = null;
	}

	public String getName() {
		return name;
	}
	
	public String getText() {
		return text;
	}

	public String getChapterRef() {
		return chapterRef;
	}
	
	public void addPhrases(String phrase) {
		if (phrases == null) {
			phrases = new ArrayList<String>();
		}
		phrases.add(phrase.trim().toUpperCase(Locale.GERMAN));
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(getName()).append(":");
		if (getText() != null) {
			result.append(" ").append(getText());
		}
		if (getChapterRef() != null) {
			result.append(" [").append(getChapterRef()).append("]");
		}
		if (phrases != null) {
			String seperator = "= ";
			for (String phrase:phrases) {
				result.append(" ").append(seperator).append(phrase);
				seperator = "| ";
			}
		}
		result.append(TextUtil.endl);
		return result.toString();
	}

	public List<String> getPhrases() {
		if (phrases == null) {
			return Collections.emptyList();
		}
		return phrases;
	}

	public void validate(SoloRoleplayData soloData, Chapter chapter, Decision decision) throws ValidationException {
		chapter.validateMandatoryRef("Auswahl '"+getName()+"'", soloData, getChapterRef());
	}

	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (getChapterRef() != null) {
			collectedRefs.add(getChapterRef());
		}
	}

}
