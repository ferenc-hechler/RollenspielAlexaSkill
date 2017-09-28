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

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import de.hechler.soloroleplay.data.Chapter;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.ValidationException;
import de.hechler.soloroleplay.parser.SoloRoleplayParser;
import de.hechler.soloroleplay.util.TextUtil;

public class WalkChapters {

	@Test
	public void walkChapters() throws ValidationException {
		String soloText = TextUtil.readResource("der_hauptmann_von_punin.txt", StandardCharsets.ISO_8859_1);
		SoloRoleplayParser parser = new SoloRoleplayParser();
		SoloRoleplayData soloData = parser.parse(soloText);
		soloData.validate();
		Set<String> allChapters = new LinkedHashSet<>();
		Set<String> visitedChapters = new LinkedHashSet<>();
//		visitedChapters.add("BEGINN");
//		visitedChapters.add("201");
		visitedChapters.add("79");
		allChapters.addAll(visitedChapters);
		Set<String> newChapters = walkChapters(visitedChapters, allChapters, soloData);
		while (!newChapters.isEmpty()) {
			System.out.println(newChapters);
			allChapters.addAll(newChapters);
			newChapters = walkChapters(newChapters, allChapters, soloData);
		}
	}

	private static class WayToChapter {
		private WayToChapter parentWay;
		private String chapter;
		public WayToChapter(String chapter, WayToChapter parentWay) { 
			this.chapter = chapter;
			this.parentWay = parentWay;
		}
		private String getChapter() {
			return chapter;
		}
		@SuppressWarnings("unused")
		private WayToChapter getParentWay() {
			return parentWay;
		}
		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("[").append(chapter).append("]");
			WayToChapter parent = parentWay;
			while (parent != null) {
				result.insert(0, "["+TextUtil.padL(parent.getChapter(), 3)+"]->");
				parent = parent.parentWay;
			}
			return result.toString();
		}
	}
	
	@Test
	public void wayToChapter() throws ValidationException {
		waysToChapter("72");
//		waysToChapter("69");
	}
	
	
	public void waysToChapter(String reachChapter) throws ValidationException {
		String soloText = TextUtil.readResource("der_hauptmann_von_punin.txt", StandardCharsets.ISO_8859_1);
		SoloRoleplayParser parser = new SoloRoleplayParser();
		SoloRoleplayData soloData = parser.parse(soloText);
		soloData.validate();

		Set<String> visitedChapters = new HashSet<String>();

		Set<WayToChapter> currentWays = new LinkedHashSet<>();
		Set<WayToChapter> nextWays = new LinkedHashSet<>();
		nextWays.add(new WayToChapter("BEGINN", null));
		visitedChapters.add("BEGINN");

		while (!nextWays.isEmpty()) {
			currentWays = nextWays;
			nextWays = new LinkedHashSet<>();
			Set<String> nextVisitedChapters = new HashSet<String>();
			for (WayToChapter way:currentWays) {
				List<String> nextChapters = soloData.getChapter(way.getChapter()).collectAllChapterReferences();
				for (String nextChapter:nextChapters) {
					WayToChapter nextWay = new WayToChapter(nextChapter, way);
					if (nextChapter.equals(reachChapter)) {
						System.out.println(nextWay.toString());
					}
					if (!visitedChapters.contains(nextChapter)) {
						nextWays.add(nextWay);
						nextVisitedChapters.add(nextChapter);
					}
				}
			}
			visitedChapters.addAll(nextVisitedChapters);
			nextVisitedChapters.clear();
		}

	}


	
	private Set<String> walkChapters(Set<String> chapterNames, Set<String> visitedChapters, SoloRoleplayData solo) {
		Set<String> result = new LinkedHashSet<String>();
		for (String chapterName:chapterNames) {
			Chapter chapter = solo.getChapter(chapterName);
			List<String> chapterRefs = chapter.collectAllChapterReferences();
			for (String chapterRef:chapterRefs) {
				if (result.contains(chapterRef) || visitedChapters.contains(chapterRef)) {
					continue;
				}
				result.add(chapterRef);
			}
		}
		return result;
	}		

}
