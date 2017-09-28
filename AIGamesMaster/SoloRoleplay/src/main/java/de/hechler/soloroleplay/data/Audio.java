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

import java.util.List;

import de.hechler.soloroleplay.util.TextUtil;

public class Audio extends Step {

	private String audioFilename;
	private String audioUrl;

	private String continueText;

	public Audio(String audioFilename, String continueText) {
		this.audioFilename = audioFilename;
		this.continueText = continueText;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("AUDIO: ").append(audioFilename).append(TextUtil.endl);
		if (continueText != null) {
			result.append(continueText).append(TextUtil.endl);
		}
		return result.toString();
	}

	@Override
	public void executeStep(Response response) {
		response.addAudio(audioUrl);
		response.addDescription(continueText);
		response.continueNextStep(response);
	}

	@Override
	public void processAnswer(Response result, String answer) {
		throw new RuntimeException("NO ANSWER EXPECTED at "+toString());
	}
	

	@Override
	public boolean alwaysRedirects() {
		return false;
	}

	@Override
	public void validate(SoloRoleplayData soloData, Chapter chapter) throws ValidationException {
		audioUrl = soloData.getAudioBaseUrl();
		if ((audioUrl == null) || audioUrl.trim().isEmpty()) {
			throw new ValidationException("Keine AUDIOBASEURL im Header definiert.");
		}
		if (!audioUrl.endsWith("/")) {
			audioUrl += "/";
		}
		audioUrl += audioFilename;
		// TODO: check existence of audioUrl?
	}

	@Override
	public void collectAllChapterReferences(List<String> collectedRefs) {
	}
	
}
