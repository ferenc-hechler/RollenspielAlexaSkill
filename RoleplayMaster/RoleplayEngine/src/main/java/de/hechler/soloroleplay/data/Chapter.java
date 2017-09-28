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
import java.util.Collection;
import java.util.List;

import de.hechler.soloroleplay.util.TextUtil;

public class Chapter {

    private String name;
	private String text;
	private List<Step> steps;
	
	public Chapter(String name, String text) {
		this.name = name;
		this.text = text;
		this.steps = null;
	}
	
	public String getName() {
		return name;
	}
	public String getText() {
		return text;
	}

	public void addStep(Step step) {
		if (steps == null) {
			steps = new ArrayList<Step>();
		}
		steps.add(step);
	}



	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(TextUtil.endl).append("[").append(getName()).append("]").append(TextUtil.endl);
		if (text != null) {
			result.append(getText()).append(TextUtil.endl);
		}
		if (steps != null) {
			result.append(TextUtil.endl);
			for (Step step:steps) {
				result.append(step.toString());
			}
		}
		result.append(TextUtil.endl);
		return result.toString();
	}

	public Step getStep(int index) {
		if (steps == null) {
			return null;
		}
		if ((index < 1) || (index > steps.size())) {
			return null;
		}
		return steps.get(index-1);
	}

	public void execute(Response response, int stepNum) {
		if (stepNum == 0) {
			response.addDescription(getText());
			response.stepFinished();
			stepNum += 1;
		}
		Step step = getStep(stepNum);
		if (step == null) {
			if (getName().equals("ENDE")) {
				response.setFinished();
				return;
			}
			throw new RuntimeException("Last step is incomplete in chapter "+getName());
		}
		step.executeStep(response);
	}

	public void validate(SoloRoleplayData soloData) throws ValidationException {
		if ((steps == null) || steps.isEmpty()) {
			if ("ENDE".equals(getName())) {
				return;
			}
			throw new ValidationException("Das Kapitel '"+getName()+"' endet ohne Weiterleitung!");
		}
		for (Step step:steps) {
			try {
				step.validate(soloData, this);
			}
			catch (ValidationException e) {
				throw new ValidationException(e.getMessage()+" in Kapitel '"+getName()+"'.");
			}
		}
		Step lastStep = steps.get(steps.size()-1);
		if (!lastStep.alwaysRedirects()) {
			if ("ENDE".equals(getName())) {
				return;
			}
			throw new ValidationException("Das Kapitel '"+getName()+"' endet ohne Weiterleitung!");
		}
	}

	public void validateNotEmpty(String errMsg, String str) throws ValidationException {
		if ((str == null) || str.trim().isEmpty()) {
			throw new ValidationException(errMsg+" in Kapitel '"+getName()+"'");
		}
	}

	public void validateNotEmpty(String errMsg, Collection<?> list) throws ValidationException {
		if ((list == null) || list.isEmpty()) {
			throw new ValidationException(errMsg+" in Kapitel '"+getName()+"'");
		}
	}


	public void validateOptionalRef(String stepInfo, SoloRoleplayData soloData, String ref) throws ValidationException {
		if (ref == null) {
			return;
		}
		if (soloData.getChapter(ref) == null) {
			throw new ValidationException(stepInfo+": Unbekannte Refernez ["+ref+"]");
		}
	}

	public void validateMandatoryRef(String stepInfo, SoloRoleplayData soloData, String ref) throws ValidationException {
		validateNotEmpty(stepInfo+": fehlende Referenz", ref);
		if (soloData.getChapter(ref) == null) {
			throw new ValidationException(stepInfo+": Unbekannte Referenz ["+ref+"]");
		}
	}

	public List<String> collectAllChapterReferences() {
		List<String> result = new ArrayList<String>();
		if (steps != null) {
			for (Step step:steps) {
				step.collectAllChapterReferences(result);
			}
		}
		return result; 
	}


}
