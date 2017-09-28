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

public class Rule extends Step {

	private List<Condition> conditions;

	public Rule() {
		conditions = new ArrayList<Condition>();
	}
	
	public void addCondition(Condition condition) {
		conditions.add(condition);
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("REGEL:").append(TextUtil.endl);
		for (Condition condition:conditions) {
			result.append(condition.toString());
		}
		return result.toString();
	}

	@Override
	public void executeStep(Response response) {
		for (Condition cond:conditions) {
			if (cond.isFulfilled(response)) {
				cond.execute(response);
				return;
			}
		}
		response.continueNextStep(response);
	}

	@Override
	public void processAnswer(Response result, String answer) {
	}

	@Override
	public boolean alwaysRedirects() {
		if ((conditions == null) || conditions.isEmpty()) {
			return false;
		}
		return conditions.get(conditions.size()-1).alwaysRedirects();
	}

	@Override
	public void validate(SoloRoleplayData soloData, Chapter chapter) throws ValidationException {
		chapter.validateNotEmpty("REGEL", conditions);
		for (Condition condition:conditions) {
			condition.validate(soloData, chapter, this);
		}
	}

	@Override
	public void collectAllChapterReferences(List<String> collectedRefs) {
		if (conditions != null) {
			for (Condition condition:conditions) {
				condition.collectAllChapterReferences(collectedRefs);
			}
		}
	}

}
