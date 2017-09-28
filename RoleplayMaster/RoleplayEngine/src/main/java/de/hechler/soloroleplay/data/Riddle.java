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

import de.hechler.soloroleplay.util.TextUtil;

public class Riddle extends Step {

	private String riddleText;
	private List<RiddleSolution> solutions;

	public Riddle(String riddleText) {
		this.riddleText = riddleText;
		this.solutions = new ArrayList<RiddleSolution>();
	}
	
	public String getRiddleText() {
		return riddleText;
	}
	
	public void addSolution(RiddleSolution solution) {
		solutions.add(solution);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("R"+TextUtil.UML_AE+"TSEL:").append(TextUtil.endl);
		result.append(riddleText).append(TextUtil.endl);
		for (RiddleSolution solution:solutions) {
			result.append(solution.toString());
		}
		result.append(TextUtil.endl);
		return result.toString();
	}

	@Override
	public void executeStep(Response response) {
		if (response.getSubStepCount() == 0) {
			response.addDescription(getRiddleText());
			response.addDescription("Sage die L"+TextUtil.UML_oe+"sung oder 'Wiederholen' um dir das R"+TextUtil.UML_ae+"tsel noch einmal anzuh"+TextUtil.UML_oe+"ren oder 'Aufgeben' um aufzugeben.");
		}
		response.setRiddleType();
	}

	@Override
	public void processAnswer(Response response, String answer) {
		String comp = TextUtil.normalizeForCompare(answer);
		if (comp.equals("WIEDERHOLEN")) {
			if (response.getSubStepCount() > 0) {
				response.addDescription(getRiddleText());
			}
			return;
		}
		for (RiddleSolution solution:getSolutions("ANTWORT")) {
			if (solution.isFulfilled(response, comp)) {
				solution.processAnswer(response);
				return;
			}
		}
		for (RiddleSolution solution:getSolutions("AUFGEBEN")) {
			if (solution.isFulfilled(response, comp)) {
				solution.processAnswer(response);
				return;
			}
		}
		for (RiddleSolution solution:solutions) {
			if (solution.isFulfilled(response, comp)) {
				solution.processAnswer(response);
				return;
			}
		}
		response.addDescription("Leider falsch, versuch es nochmal.");
		response.incrementSubStepCount();
	}
	
	private List<RiddleSolution> getSolutions(String solutionType) {
		List<RiddleSolution> result = new ArrayList<RiddleSolution>();
		for (RiddleSolution solution:solutions) {
			if (solutionType.equals(solution.getSolutionType())) {
				result.add(solution);
			}
		}
		return result;
	}

	public void validate() {
		try {
			for (RiddleSolution solution:solutions) {
				solution.validate();
			}
		}
		catch (RuntimeException e) {
			throw new RuntimeException("ERROR: R"+TextUtil.UML_AE+"TSEL '"+getRiddleText()+"': "+e.getMessage(), e);
		}
	}

	@Override
	public boolean alwaysRedirects() {
		return true;
	}

	@Override
	public void validate(SoloRoleplayData soloData, Chapter chapter) throws ValidationException {
		chapter.validateNotEmpty("R"+TextUtil.UML_AE+"TSEL", solutions);
		for (RiddleSolution solution:solutions) {
			solution.validate(soloData, chapter, this);
		}
	}

	@Override
	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (solutions != null) {
			for (RiddleSolution solution:solutions) {
				solution.collectAllChapterReferences(collectedRefs);
			}
		}
	}

}
