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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hechler.soloroleplay.parser.SoloRoleplayParser.Token;
import de.hechler.soloroleplay.util.TextUtil;

public class SoloRoleplayData {

	private RestrictedParameterMap metadata;
	
	private String shortName;
	private String title;
	private List<Chapter> chapters;
	private List<Phonem> phonems;
	private List<Voice> voices;
	private String audioBaseUrl;
	private Map<String, Integer> chapterNames2Index;

	private int charSize;

	
	
	public SoloRoleplayData(String title) {
		this.title = title;
		this.metadata = new RestrictedParameterMap(new String[]{"ERFAHRUNGSSTUFEN", "REGELWERK", "DAUER", "ORT", "AUTOR", "ERSCHIENEN", "LETZTEAKTUALISIERUNG", "LIZENZ", "LIZENZURL", "EMAIL", "WEBSEITE", "KURZBESCHREIBUNG", "ANMERKUNG", "AUDIOBASEURL"});
		this.chapters = new ArrayList<Chapter>();
		this.phonems = new ArrayList<Phonem>();
		this.voices = new ArrayList<Voice>();
		this.chapterNames2Index = new HashMap<String, Integer>();
	}
	
	public String getTitle() {
		return title;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getShortName() {
		return shortName;
	}
	
	public String getLevel() {
		return metadata.getValue("ERFAHRUNGSSTUFEN");
	}
	public String getRuleset() {
		return metadata.getValue("REGELWERK");
	}
	public String getDuration() {
		return metadata.getValue("DAUER");
	}
	public String getLocation() {
		return metadata.getValue("ORT");
	}
	public String getAuthor() {
		return metadata.getValue("AUTOR");
	}
	public String getPublishingDate() {
		return metadata.getValue("ERSCHIENEN");
	}
	public String getLastUpdate() {
		return metadata.getValue("LETZTEAKTUALISIERUNG");
	}
	public String getLicense() {
		return metadata.getValue("LIZENZ");
	}
	public String getLicenseUrl() {
		return metadata.getValue("LIZENZURL");
	}
	public String getContactMail() {
		return metadata.getValue("EMAIL");
	}
	public String getWebSite() {
		return metadata.getValue("WEBSEITE");
	}
	public String getDescription() {
		return metadata.getValue("KURZBESCHREIBUNG");
	}
	public String getPhonemDescription() {
		return handlePhonems(metadata.getValue("KURZBESCHREIBUNG"));
	}
	public String getNotes() {
		return metadata.getValue("ANMERKUNG");
	}
	public String getAudioBaseUrl() {
		return metadata.getValue("AUDIOBASEURL");
	}
	
	
	public void setMetadata(Token tokKey, Token tokText) {
		String value = (tokText == null) ? null : tokText.getText();
		String key = (tokKey == null) ? null : tokKey.getText();
		metadata.addParameter(key, value);
	}


	public void addChapter(Chapter chapter) {
		if (chapters.contains(chapter.getName())) {
			throw new RuntimeException("duplicate chapter name '"+chapter.getName()+"'");
		}
		chapterNames2Index.put(chapter.getName(), chapters.size());
		chapters.add(chapter);
	}


	public void addDefinition(Definition definition) {
		if (definition == null) {
			return;
		}
		if (definition instanceof Phonem) {
			addPhonem((Phonem) definition);
		}
		else if (definition instanceof Voice) {
			addVoice((Voice) definition);
		}
		else {
			throw new RuntimeException("unexpected definition type: "+definition.getClass().getSimpleName());
		}
	}
	
	public void addPhonem(Phonem phonem) {
		if (phonems.contains(phonem.getWord())) {
			throw new RuntimeException("duplicate phonem word '"+phonem.getWord()+"'");
		}
		phonems.add(phonem);
	}

	public void addVoice(Voice voice) {
		if (voices.contains(voice.getVoiceName())) {
			throw new RuntimeException("duplicate voice name '"+voice.getVoiceName()+"'");
		}
		voices.add(voice);
	}

	public Chapter getChapter(String chapterName) {
		Integer index = chapterNames2Index.get(chapterName);
		if (index == null) {
			return null;
		}
		return chapters.get(index);
	}

	public Chapter getChapter(int n) {
		if ((n < 0) || (n > chapters.size())) {
			return null;
		}
		return chapters.get(n);
	}


	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("Solo-Abenteuer '").append(getTitle()).append("'").append(TextUtil.endl).append(TextUtil.endl);
		result.append(metadata.toString());
		if (!phonems.isEmpty()) {
			result.append(TextUtil.endl);
			for (Phonem phonem:phonems) {
				result.append(phonem.toString());
			}
		}
		if (!voices.isEmpty()) {
			result.append(TextUtil.endl);
			for (Voice voice:voices) {
				result.append(voice.toString());
			}
		}
		for (Chapter chapter:chapters) {
			result.append(chapter.toString());
		}
		return result.toString();
	}

	public void validate() throws ValidationException {
		audioBaseUrl = getAudioBaseUrl();
		if ((audioBaseUrl != null) && !audioBaseUrl.trim().endsWith("/")) {
			audioBaseUrl = audioBaseUrl.trim() + "/";
		}
		validateNotNull("Es muss ein Kapitel mit dem Namen 'BEGINN' geben", getChapter("BEGINN"));
		validateNotNull("Es muss ein Kapitel mit dem Namen 'ENDE' geben", getChapter("ENDE"));
		for (Chapter chapter:chapters) {
			chapter.validate(this);
		}
		validateChapterWalk();
	}

	private void validateChapterWalk() throws ValidationException {
		Set<String> visitedChapters = new HashSet<String>();
		List<String> unvisitedChapters = new ArrayList<String>();
		unvisitedChapters.add("BEGINN");
		while (!unvisitedChapters.isEmpty()) {
			String chapterRefToVisit = unvisitedChapters.remove(0);
			if (visitedChapters.contains(chapterRefToVisit)) {
				continue;
			}
			visitedChapters.add(chapterRefToVisit);
			Chapter chapterToVisit = getChapter(chapterRefToVisit);
			if (chapterRefToVisit == null) {
				throw new ValidationException("Das referenzierte Kapitel '"+chapterRefToVisit+"' fehlt!");
			}
			List<String> referencedChapters = chapterToVisit.collectAllChapterReferences();
			unvisitedChapters.addAll(referencedChapters);
		}
		for (String chapterName:chapterNames2Index.keySet()) {
			if (!visitedChapters.contains(chapterName)) {
				throw new ValidationException("Das Kapitel '"+chapterName+"' kann nicht erreicht werden.");
			}
		}
	}

	void validateNotNull(String errMsg, Object obj) throws ValidationException {
		if (obj == null) {
			throw new ValidationException(errMsg);
		}
	}

	public void setCharSize(int charSize) {
		this.charSize = charSize;
	}
	public int getCharSize() {
		return charSize;
	}
	public int getCountChapters() {
		return chapters.size();
	}

	public List<String> getChapterNames() {
		List<String> result = new ArrayList<String>();
		for (Chapter chapter:chapters) {
			result.add(chapter.getName());
		}
		return result;
	}

	public String handlePhonems(String text) {
		String result = text;
		for (Phonem phonem:phonems) {
			if (result.indexOf(phonem.getWord()) != -1) {
				result = result.replaceAll("\\b"+phonem.getWord()+"\\b", "PHONEM("+phonem.getWord()+"="+phonem.getXsampa()+")");
			}
		}
		return result;
	}
	
	public String handleVoices(String text) {
		String result = text;
		for (Voice voice:voices) {
			String search = "V_"+voice.getVoiceName()+"(";
			if (result.indexOf(search) != -1) {
				String searchRX = "V[_]"+voice.getVoiceName()+"[(]";
				result = result.replaceAll("\\b"+searchRX+"([^)]*)[)]", "VOICE("+voice.getVoiceAttribs()+"|"+"$1)");
			}
		}
		return result;
	}
	
	public String handleAudio(String text) {
		String result = text;
		if (audioBaseUrl != null) {
			if (result.indexOf("AUDIO(") != -1) {
				result = result.replaceAll("\\bAUDIO[(]([^)]*)[)]", "AUDIO("+audioBaseUrl+"$1)");
			}
		}
		return result;
	}
	
}
