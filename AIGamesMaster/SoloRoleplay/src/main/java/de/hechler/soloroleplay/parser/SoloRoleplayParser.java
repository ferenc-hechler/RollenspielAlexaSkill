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
package de.hechler.soloroleplay.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.hechler.soloroleplay.data.Action;
import de.hechler.soloroleplay.data.Audio;
import de.hechler.soloroleplay.data.Challenge;
import de.hechler.soloroleplay.data.Chapter;
import de.hechler.soloroleplay.data.Choice;
import de.hechler.soloroleplay.data.ChoicesWithReturn;
import de.hechler.soloroleplay.data.Condition;
import de.hechler.soloroleplay.data.Continue;
import de.hechler.soloroleplay.data.Definition;
import de.hechler.soloroleplay.data.Fight;
import de.hechler.soloroleplay.data.MultipleDecision;
import de.hechler.soloroleplay.data.Opponent;
import de.hechler.soloroleplay.data.Opponent.OpponentType;
import de.hechler.soloroleplay.data.Phonem;
import de.hechler.soloroleplay.data.RandomDecision;
import de.hechler.soloroleplay.data.Return;
import de.hechler.soloroleplay.data.Riddle;
import de.hechler.soloroleplay.data.RiddleSolution;
import de.hechler.soloroleplay.data.Rule;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.Voice;
import de.hechler.soloroleplay.data.YesNoDecision;
import de.hechler.soloroleplay.util.SetUtil;
import de.hechler.soloroleplay.util.TextUtil;

public class SoloRoleplayParser {

	private static final String COMMENT_LINERX = "^#.*$";
	private static final String CHAPTER_LINERX = "^\\[([0-9A-Z"+TextUtil.UML_AEOEUESZ+"_]+)\\]\\s*$";
	private static final String KEYWORD_LINERX = "^([A-Z"+TextUtil.UML_AEOEUESZ+"_]+)[:]\\s*(.*)$";
	/** "$1($2): $3" -> "BESUCHT(13): ....". */
	private static final String CONDITION_LINERX = "^([A-Z"+TextUtil.UML_AEOEUESZ+"_]+)\\(([0-9A-Za-z"+TextUtil.UML_aeoeueAEOEUESZ+"_]+)\\)[:]\\s*(.*)$";       
	/** "$1($2,$3): $4" -> "BESUCHT(13,3): ....". */
	private static final String CONDITION_2ARG_LINERX = "^([A-Z"+TextUtil.UML_AEOEUESZ+"_]+)\\(([0-9A-Za-z"+TextUtil.UML_aeoeueAEOEUESZ+"_]+)\\s*,\\s*([0-9A-Za-z"+TextUtil.UML_aeoeueAEOEUESZ+"_]+)\\)[:]\\s*(.*)$";   
	private static final String TEXT_REF_TEXT_RX = "^(.*)\\[([0-9A-Z"+TextUtil.UML_AEOEUESZ+"_]+)\\](.*)$";

	private final static Set<String> reservedStepWords = SetUtil.toSet(
			"JANEINENTSCHEIDUNG", 
			"ZUFALLSENTSCHEIDUNG", 
			"ENTSCHEIDUNG", 
			"AUSWAHLMITR"+TextUtil.UML_UE+"CKSPRUNG", 
			"KAMPF", 
			"PROBE", 
			"AKTION",  
			"REGEL", 
			"R"+TextUtil.UML_AE+"TSEL",
			"WEITER", 
			"R"+TextUtil.UML_UE+"CKSPRUNG"
	);
	
	
	enum TokenType {UNKNOWN, KEYWORD, TEXT, CHAPTER, REF, FUNC, FUNCPARAM, PHRASE, EOF}
	public static class Token {

		public static final Token EOFTOKEN = new Token(-1, TokenType.EOF, null);
		
		private int lineNum;
		private TokenType type;
		private String originalText;
		private String additionalText;
		public Token(int lineNum, TokenType type, String text) {
			this.lineNum = lineNum;
			this.type = type;
			this.originalText = text;
			this.additionalText = null;
		}
		public boolean isType(TokenType compType) {
			return type == compType;
		}
		public String getText() {
			if (additionalText == null) {
				return originalText;
			}
			else {
				return originalText + " " + additionalText;
			}
		}
		public String getOriginalText() {
			return originalText;
		}
		public String getAdditionalText() {
			return additionalText;
		}
		public void addText(String textToAdd) {
			if ((textToAdd == null) || textToAdd.trim().isEmpty()) {
				return;
			}
			if (additionalText == null) {
				additionalText = textToAdd.trim();
			}
			else {
				additionalText = additionalText  + " " + textToAdd;
			}
		}
		public boolean hasAdditionalText() {
			return getAdditionalText() != null;
		}
		@Override
		public String toString() {
			String text = getOriginalText();
			if (hasAdditionalText()) {
				text += " | "+getAdditionalText();
			}
			return type+":"+lineNum+"["+text+"]";
		}
	}


	public SoloRoleplayData parse(String input) {
		String inputNL = input.replace("\r\n", "\n").replace("\r", "\n");
		inputNL = inputNL.replace('\u2003', ' '); // allow word page breaks. Other unicode spaces: https://www.cs.tut.fi/~jkorpela/chars/spaces.html 
		List<Token> tokens = tokenize(inputNL);
		tokens = mergeTexts(tokens);
		
//		for (Token tok:tokens) {
//			System.out.println(tok);
//		}
//		System.out.println("---");

		SoloRoleplayData result = createSoloRoleplay(new TokenStream(tokens));
		result.setCharSize(input.length());
		
		return result;
	}
	
	
	public SoloRoleplayData createSoloRoleplay(TokenStream ts) {
		Token tok = ts.nextToken(TokenType.KEYWORD);
		if ((tok == null) || !tok.getText().equals("TITEL")) {
			throw new RuntimeException("missing title at "+tok);
		}
		Token tokText = ts.nextToken(TokenType.TEXT);
		String title = (tokText == null) ? null : tokText.getText();
		SoloRoleplayData result = new SoloRoleplayData(title);
		tok = ts.nextToken();
		while (!tok.isType(TokenType.EOF)) {
			if (tok.isType(TokenType.KEYWORD)) {
				tokText = ts.nextToken(TokenType.TEXT);
				result.setMetadata(tok, tokText);
			}
			else if (tok.isType(TokenType.CHAPTER)) {
				Chapter chapter = createChapter(tok, ts);
				result.addChapter(chapter);
			}
			else if (tok.isType(TokenType.FUNC)) {
				Definition definition = createDefinition(tok, ts);
				result.addDefinition(definition);
			}
			else {
				throw new RuntimeException("unewxpected token "+tok);
			}
			tok = ts.nextToken();
		}
		return result;
	}

	
	private Chapter createChapter(Token tokChapter, TokenStream ts) {
		Token tokText = ts.nextToken(TokenType.TEXT);
		String text = tokText == null ? null : tokText.getText();
		Chapter result = new Chapter(tokChapter.getText(), text);
		while (true) {
			Token tok = ts.nextToken();
			if (tok.isType(TokenType.EOF) || tok.isType(TokenType.CHAPTER)) {
				ts.backToPreviousToken();
				break;
			}
			if (tok.isType(TokenType.KEYWORD)) {
				switch (tok.getText()) {
				case "JANEINENTSCHEIDUNG": {
					YesNoDecision decision = createYesNoDecision(tok, ts);
					decision.validate();
					result.addStep(decision);
					break;
				}
				case "ENTSCHEIDUNG": {
					MultipleDecision decision = createMultipleDecision(tok, ts);
					decision.validate();
					result.addStep(decision);
					break;
				}
				case "ZUFALLSENTSCHEIDUNG": {
					RandomDecision decision = createRandomDecision(tok, ts);
					decision.validate();
					result.addStep(decision);
					break;
				}
				case "AUSWAHLMITR"+TextUtil.UML_UE+"CKSPRUNG": {
					ChoicesWithReturn choicesWithReturn = createChoicesWithReturn(tok, ts);
					choicesWithReturn.validate();
					result.addStep(choicesWithReturn);
					break;
				}
				case "PROBE": {
					Challenge challenge = createChallenge(tok, ts);
					challenge.validate();
					result.addStep(challenge);
					break;
				}
				case "AKTION": {
					Action action= createAction(tok, ts);
					result.addStep(action);
					break;
				}
				case "WEITER": {
					Continue cont = createContinue(tok, ts);
					result.addStep(cont);
					break;
				}
				case "R"+TextUtil.UML_UE+"CKSPRUNG": {
					Return ret = createReturn(tok, ts);
					result.addStep(ret);
					break;
				}
				case "REGEL": {
					Rule rule = createRule(tok, ts);
					result.addStep(rule);
					break;
				}
				case "KAMPF": {
					Fight fight = createFight(tok, ts);
					fight.validate();
					result.addStep(fight);
					break;
				}
				case "R"+TextUtil.UML_AE+"TSEL": {
					Riddle riddle= createRiddle(tok, ts);
					riddle.validate();
					result.addStep(riddle);
					break;
				}
				case "AUDIO": {
					Audio audio = createAudio(tok, ts);
					result.addStep(audio);
					break;
				}
				default:{
//					skipTokensUntil(TokenType.CHAPTER, ts);
//					continue;
					throw new RuntimeException("unexpected token "+tok);
				}
				}
			}
			else {
				throw new RuntimeException("unexpected token "+tok);
			}
		}
		return result;
	}



	private Fight createFight(Token tok, TokenStream ts) {
		try {
			String kampfIntroText = null;
			String notiereDatenText = null;
			String datenWiederholenText = null;
			String sageErgebnisText = null;
			String falscheAuswahlText = null;
			
			Token modifyTok = null; 
			while (true) {
				modifyTok = ts.nextToken(TokenType.KEYWORD);
				if (modifyTok == null) {
					break;
				}
				String modText = recognizeModifyBeforeKeyword(modifyTok, "KAMPFINTROTEXT", ts);
				if (modText != null) {
					kampfIntroText = modText;
					continue;
				}
				modText = recognizeModifyBeforeKeyword(modifyTok, "NOTIEREDATENTEXT", ts);
				if (modText != null) {
					notiereDatenText = modText;
					continue;
				}
				modText = recognizeModifyBeforeKeyword(modifyTok, "DATENWIEDERHOLENTEXT", ts);
				if (modText != null) {
					datenWiederholenText = modText;
					continue;
				}
				modText = recognizeModifyBeforeKeyword(modifyTok, "SAGEERGEBNISTEXT", ts);
				if (modText != null) {
					sageErgebnisText = modText;
					continue;
				}
				modText = recognizeModifyBeforeKeyword(modifyTok, "FALSCHEAUSWAHLTEXT", ts);
				if (modText != null) {
					falscheAuswahlText = modText;
					continue;
				}
				ts.backToPreviousToken();
				break;
			}
			
			Fight result = new Fight(kampfIntroText, notiereDatenText, datenWiederholenText, sageErgebnisText, falscheAuswahlText);
			Token oppTok = nextNotStepKeywordToken(ts);
			while ((oppTok != null) && (oppTok.getText().equals("FREUND") || oppTok.getText().equals("GEGNER"))) {
				Opponent opponent = createOpponent(oppTok, ts);
				result.addOpponent(opponent);
				oppTok = nextNotStepKeywordToken(ts);
			}
			Token choiceTok = oppTok;
			while (choiceTok != null) {
				if (!(choiceTok.getText().equals("GEWONNEN") || choiceTok.getText().equals("VERLOREN") || choiceTok.getText().equals("ABGEBROCHEN"))) {
					ts.backToPreviousToken();
					break;
				}
				Choice choice = createChoice(choiceTok, ts);
				result.addChoice(choice);
				choiceTok = nextNotStepKeywordToken(ts);
			}
			result.validate();
			return result;
		}
		catch (RuntimeException e) {
			throw new RuntimeException("Fehler bei Kampf "+tok+": "+e.getMessage()+", naechstes-Token: "+ts.nextToken(), e);
		}
	}


	private String recognizeModifyBeforeKeyword(Token modifyTok, String keyword, TokenStream ts) {
		if (!modifyTok.getText().equals(keyword)) {
			return null;
		}
		Token resultTextTok = ts.nextToken(TokenType.TEXT);
		if (resultTextTok == null) {
			throw new RuntimeException("Es fehlt der Text fuer "+modifyTok);
		}
		if (resultTextTok.hasAdditionalText()) {
			throw new RuntimeException("Unerwarteter Text bei: "+resultTextTok);
		}
		return resultTextTok.getOriginalText();
	}


	private Token recognizeModifyBeforeText(Token modifyTok, String keyword, TokenStream ts) {
		if (!modifyTok.getText().equals(keyword)) {
			return null;
		}
		Token resultTextTok = ts.nextToken(TokenType.TEXT);
		if (resultTextTok == null) {
			throw new RuntimeException("Es fehlt der Text fuer "+modifyTok);
		}
		return resultTextTok;
	}


	private Opponent createOpponent(Token oppTok, TokenStream ts) {
		OpponentType type = oppTok.getText().equals("FREUND") ? OpponentType.FRIEND : OpponentType.FOE; 
		Token opponentNameTok = ts.nextToken(TokenType.TEXT);
		if (opponentNameTok == null) {
			throw new RuntimeException("missing opponent name for "+oppTok);
		}
		String gegnerText = null;
		Token textKeywordTok = nextNotStepKeywordToken(ts);
		if (textKeywordTok != null) {
			Token textTok = recognizeModifyBeforeText(textKeywordTok, "TEXT", ts);
			if (textTok != null) {
				gegnerText = textTok.getText();
			}
			else {
				ts.backToPreviousToken();
			}
		}
		Opponent result = new Opponent(type, opponentNameTok.getText(), gegnerText);
		Token parameterNameTok = nextNotStepKeywordToken(ts);
		while (parameterNameTok != null) {
			String name = parameterNameTok.getText();
			if (!result.knowsParameter(name)) {
				ts.backToPreviousToken();
				break;
			}
			Token parameterValueTok = ts.nextToken(TokenType.TEXT);
			if (parameterValueTok == null) {
				throw new RuntimeException("Fehlender Wert fuer den Freund/Gegner Parameter "+parameterNameTok);
			}
			String value = parameterValueTok.getText();
			result.addParameter(name, value);
			parameterNameTok = nextNotStepKeywordToken(ts);
		}
		Token additionTok = nextNotStepKeywordToken(ts);
		while (additionTok != null) {
			String name = additionTok.getText();
			if (!"ZUSATZ".equals(name)) {
				ts.backToPreviousToken();
				break;
			}
			Token textTok = ts.nextToken(TokenType.TEXT);
			if (textTok == null) {
				throw new RuntimeException("missing text for opponent addition "+additionTok);
			}
			String text = textTok.getText();
			result.addAddition(text);
			additionTok = nextNotStepKeywordToken(ts);
		}
		return result;
	}


	private Rule createRule(Token tok, TokenStream ts) {
		Rule result = new Rule();
		Token condTok = ts.nextToken(TokenType.FUNC);
		while (condTok != null) {
			Condition cond = createCondition(condTok, ts);
			result.addCondition(cond);
			condTok = ts.nextToken(TokenType.FUNC);
		}
		Token sonstTok = nextNotStepKeywordToken(ts);
		if (sonstTok != null) {
			if (sonstTok.getText().equals("SONST")) {
				Condition sonstCond = createCondition(sonstTok, ts);
				result.addCondition(sonstCond);
			}
			else {
				ts.backToPreviousToken();
			}
		}
		return result;
	}


	private Condition createCondition(Token condTok, TokenStream ts) {
		String condName = condTok.getText();
		Token condRefTok = ts.nextToken(TokenType.FUNCPARAM);
		String condRef = (condRefTok==null) ? null : condRefTok.getText();
		Token condCntTok = ts.nextToken(TokenType.FUNCPARAM);
		String condCnt = (condCntTok==null) ? null : condCntTok.getText();
		Token textTok = ts.nextToken(TokenType.TEXT);
		String text = (textTok==null) ? null : textTok.getText();
		Token refTok = ts.nextToken(TokenType.REF);
		if (refTok == null) {
			throw new RuntimeException("missing target chapter reference in condition "+condTok);
		}
		String ref = (refTok==null) ? null : refTok.getText();
		Condition result = new Condition(condName, condRef, condCnt, text, ref);
		return result;
	}


	private Definition createDefinition(Token definitionTok, TokenStream ts) {
		String definitionName = definitionTok.getText();
		if (definitionName.equals("PHONEM")) {
			return createPhonem(definitionTok, ts);
		}
		else if (definitionName.equals("VOICE")) {
			return createVoice(definitionTok, ts);
		}
		else {
			throw new RuntimeException("unbekanntes Schluesselwort "+definitionTok);
		}
	}



	private Phonem createPhonem(Token phonemTok, TokenStream ts) {
		String phonemName = phonemTok.getText();
		if (!phonemName.equals("PHONEM")) {
			throw new RuntimeException("unbekanntes Schluesselwort "+phonemTok);
		}
		Token phonemWordTok = ts.nextToken(TokenType.FUNCPARAM);
		if (phonemWordTok == null) {
			throw new RuntimeException("fehlender Parameter (Wort) für "+phonemTok);
		}
		String phonemWord = phonemWordTok.getText();
		Token xsampaTok = ts.nextToken(TokenType.TEXT);
		if (xsampaTok == null) {
			throw new RuntimeException("fehlende Aussprache (xsampa) für "+phonemTok+"("+phonemWord+")");
		}
		String xsampa = xsampaTok.getText();
		Phonem result = new Phonem(phonemWord, xsampa);
		return result;
	}

	private Voice createVoice(Token voiceTok, TokenStream ts) {
		String voiceTagName = voiceTok.getText();
		if (!voiceTagName.equals("VOICE")) {
			throw new RuntimeException("unbekanntes Schluesselwort "+voiceTok);
		}
		Token voiceNameTok = ts.nextToken(TokenType.FUNCPARAM);
		if (voiceNameTok == null) {
			throw new RuntimeException("fehlender Parameter (Wort) für "+voiceTok);
		}
		String voiceName = voiceNameTok.getText();
		Token voiceAttribsTok = ts.nextToken(TokenType.TEXT);
		if (voiceAttribsTok == null) {
			throw new RuntimeException("fehlende Aussprache (xsampa) für "+voiceTok+"("+voiceName+")");
		}
		String voiceAttribs = voiceAttribsTok.getText();
		Voice result = new Voice(voiceName, voiceAttribs);
		return result;
	}



	private Action createAction(Token actionTok, TokenStream ts) {
		String sageOkText = null;
		String falscheAuswahlText = null;
		String actionText = null;
		Token modifyTok = ts.nextToken(TokenType.KEYWORD);
		while (modifyTok != null) {
			if (modifyTok.getText().equals("SAGEOKTEXT")) {
				Token sageOkTextTok = ts.nextToken(TokenType.TEXT);
				if (sageOkTextTok == null) {
					throw new RuntimeException("Es fehlt der Text fuer "+modifyTok);
				}
				sageOkText = sageOkTextTok.getOriginalText();
				if (sageOkTextTok.hasAdditionalText()) {
					actionText = sageOkTextTok.getAdditionalText();
					break;
				}
			}
			else if (modifyTok.getText().equals("FALSCHEAUSWAHLTEXT")) {
				Token falscheAuswahlTextTok = ts.nextToken(TokenType.TEXT);
				if (falscheAuswahlTextTok == null) {
					throw new RuntimeException("Es fehlt der Text fuer "+falscheAuswahlTextTok);
				}
				falscheAuswahlText = falscheAuswahlTextTok.getOriginalText();
				if (falscheAuswahlTextTok.hasAdditionalText()) {
					actionText = falscheAuswahlTextTok.getAdditionalText();
					break;
				}
			}
			else {
				throw new RuntimeException("unerwartetes Schluesselwort "+modifyTok+" fuer Aktion "+actionText);
			}
			modifyTok = ts.nextToken(TokenType.KEYWORD);
		}
		if (actionText == null) {
			Token actionTextTok = ts.nextToken(TokenType.TEXT);
			if (actionTextTok == null) { 
				throw new RuntimeException("Missing text for action "+actionTok);
			}
			actionText = actionTextTok.getText();
		}
		Action result = new Action(actionText, sageOkText, falscheAuswahlText);
		
		return result;
	}

	private Riddle createRiddle(Token riddleTok, TokenStream ts) {
		Token riddleTextTok = ts.nextToken(TokenType.TEXT);
		if (riddleTextTok == null) { 
			throw new RuntimeException("Missing text for riddle "+riddleTok);
		}
		Riddle result = new Riddle(riddleTextTok.getText());
		Token solutionTok = ts.nextToken(TokenType.FUNC);
		while (solutionTok != null) {
			RiddleSolution solution = createRiddleSolution(solutionTok, ts);
			result.addSolution(solution);
			solutionTok = ts.nextToken(TokenType.FUNC);
		}
		Token aufgebenTok = nextNotStepKeywordToken(ts);
		if (aufgebenTok != null) {
			if (aufgebenTok.getText().equals("AUFGEBEN")) {
				RiddleSolution aufgebenSolution = createRiddleSolution(aufgebenTok, ts);
				result.addSolution(aufgebenSolution);
			}
			else {
				ts.backToPreviousToken();
			}
		}
		result.validate();
		return result;
	}



	private RiddleSolution createRiddleSolution(Token solutionTok, TokenStream ts) {
		String solutionType = solutionTok.getText();
		Token solutionParamTok = ts.nextToken(TokenType.FUNCPARAM);
		String solutionParam = (solutionParamTok==null) ? null : solutionParamTok.getText();
		Token textTok = ts.nextToken(TokenType.TEXT);
		String text = (textTok==null) ? null : textTok.getText();
		Token refTok = ts.nextToken(TokenType.REF);
		String ref = (refTok==null) ? null : refTok.getText();
		RiddleSolution result = new RiddleSolution(solutionType, solutionParam, text, ref);
		return result;
	}


	private Continue createContinue(Token continueTok, TokenStream ts) {
		Token continueTextTok = ts.nextToken(TokenType.TEXT);
		String continueText = (continueTextTok == null) ? null : continueTextTok.getText(); 
		Token continueRefTok = ts.nextToken(TokenType.REF);
		String continueRef = (continueRefTok == null) ? null : continueRefTok.getText(); 
		Continue result = new Continue(continueText, continueRef);
		return result;
	}
	
	private Audio createAudio(Token tok, TokenStream ts) {
		Token audioFilenameTok = ts.nextToken(TokenType.TEXT);
		if (audioFilenameTok == null) {
			throw new RuntimeException("MP3-Dateiname nach "+tok+" fehlt.");
		}
		String audioFilenameText = audioFilenameTok.getText(); 
		String audioFilename = audioFilenameText; 
		String continueText = null; 
		if (audioFilenameText.matches("^([^\\s]+)\\s+(.*)")) {
			audioFilename = audioFilenameText.replaceFirst("^([^\\s]+)\\s+(.*)", "$1"); 
			continueText = audioFilenameText.replaceFirst("^([^\\s]+)\\s+(.*)", "$2"); 
		}
		Audio result = new Audio(audioFilename, continueText);
		return result;
	}


	
	private Return createReturn(Token tok, TokenStream ts) {
		Return result = new Return();
		return result;
	}

	private MultipleDecision createMultipleDecision(Token decisiontok, TokenStream ts) {
		String auswahlText = null;
		String falscheAuswahlText = null;
		Token modifyTok = null; 
		String question = null;
		while (true) {
			modifyTok = ts.nextToken(TokenType.KEYWORD);
			if (modifyTok == null) {
				break;
			}
			Token textTok = recognizeModifyBeforeText(modifyTok, "AUSWAHLTEXT", ts);
			if (textTok != null) {
				auswahlText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			textTok = recognizeModifyBeforeText(modifyTok, "FALSCHEAUSWAHLTEXT", ts);
			if (textTok != null) {
				falscheAuswahlText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			ts.backToPreviousToken();
			break;
		}
		if (question == null) {
			Token tokQuestion = ts.nextToken(TokenType.TEXT);
			if (tokQuestion == null) {
				throw new RuntimeException("Fehlende Frage: "+decisiontok);
			}
			question = tokQuestion.getText();
		}
		MultipleDecision result = new MultipleDecision(question, auswahlText, falscheAuswahlText);
		Token choiceNameTok = nextNotStepKeywordToken(ts);
		while (choiceNameTok != null) {
			Choice choice = createChoice(choiceNameTok, ts);
			result.addChoice(choice);
			choiceNameTok = nextNotStepKeywordToken(ts);
		}
		return result;
	}
	
	private RandomDecision createRandomDecision(Token decisiontok, TokenStream ts) {
		RandomDecision result = new RandomDecision();
		Token choiceNameTok = nextNotStepKeywordToken(ts);
		while (choiceNameTok != null) {
			Choice choice = createChoice(choiceNameTok, ts);
			result.addChoice(choice);
			choiceNameTok = nextNotStepKeywordToken(ts);
		}
		return result;
	}
	
	private ChoicesWithReturn createChoicesWithReturn(Token choiceWithReturnTok, TokenStream ts) {
		String sageWeiterText = null; 
		String auswahlText = null;
		String falscheAuswahlText = null;
		
		String question = null;
		while (true) {
			Token modifyTok = ts.nextToken(TokenType.KEYWORD);
			if (modifyTok == null) {
				break;
			}
			Token textTok = recognizeModifyBeforeText(modifyTok, "SAGEWEITERTEXT", ts);
			if (textTok != null) {
				sageWeiterText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			textTok = recognizeModifyBeforeText(modifyTok, "AUSWAHLTEXT", ts);
			if (textTok != null) {
				auswahlText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			textTok = recognizeModifyBeforeText(modifyTok, "FALSCHEAUSWAHLTEXT", ts);
			if (textTok != null) {
				falscheAuswahlText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			ts.backToPreviousToken();
			break;
		}
		if (question == null) {
			Token tokQuestion = ts.nextToken(TokenType.TEXT);
			if (tokQuestion == null) {
				throw new RuntimeException("Fehlende Frage: "+choiceWithReturnTok);
			}
			question = tokQuestion.getText();
		}
		ChoicesWithReturn result = new ChoicesWithReturn(question, sageWeiterText, auswahlText, falscheAuswahlText);
		Token choiceNameTok = nextNotStepKeywordToken(ts);
		while (choiceNameTok != null) {
			Choice choice = createChoice(choiceNameTok, ts);
			result.addChoice(choice);
			choiceNameTok = nextNotStepKeywordToken(ts);
		}
		return result;
	}





	void skipTokensUntil(TokenType type, TokenStream ts) {
		while (true) {	
			Token tok = ts.nextToken();
			if (tok.isType(type)) {
				ts.backToPreviousToken();
				return;
			}
			if (tok.isType(TokenType.EOF)) {
				return;
			}
		}
	}


	private Challenge createChallenge(Token challengeTok, TokenStream ts) {
		
		String macheProbeText = null;
		String sageErgebnisText = null;
		String falscheAuswahlText = null;
		
		String question = null;
		while (true) {
			Token modifyTok = ts.nextToken(TokenType.KEYWORD);
			if (modifyTok == null) {
				break;
			}
			Token textTok = recognizeModifyBeforeText(modifyTok, "MACHEPROBETEXT", ts);
			if (textTok != null) {
				macheProbeText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			textTok = recognizeModifyBeforeText(modifyTok, "SAGEERGEBNISTEXT", ts);
			if (textTok != null) {
				sageErgebnisText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			textTok = recognizeModifyBeforeText(modifyTok, "FALSCHEAUSWAHLTEXT", ts);
			if (textTok != null) {
				falscheAuswahlText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			ts.backToPreviousToken();
			break;
		}
		if (question == null) {
			Token tokQuestion = ts.nextToken(TokenType.TEXT);
			if (tokQuestion == null) {
				throw new RuntimeException("Fehlende Frage: "+challengeTok);
			}
			question = tokQuestion.getText();
		}
		Challenge result = new Challenge(question, macheProbeText, sageErgebnisText, falscheAuswahlText);
		Token choiceNameTok = nextNotStepKeywordToken(ts);
		while (choiceNameTok != null) {
			Choice choice = createChoice(choiceNameTok, ts);
			result.addChoice(choice);
			choiceNameTok = nextNotStepKeywordToken(ts);
		}
		return result;
	}


	private Token nextNotStepKeywordToken(TokenStream ts) {
		Token result = ts.nextToken(TokenType.KEYWORD);
		if (result != null) {
			String choiceName = result.getText();
			if (reservedStepWords .contains(choiceName)) {
				ts.backToPreviousToken();
				result = null;
			}
		}
		return result;
	}


	private YesNoDecision createYesNoDecision(Token yndTok, TokenStream ts) {
		
		String falscheAuswahlText = null;
		
		String question = null;
		while (true) {
			Token modifyTok = ts.nextToken(TokenType.KEYWORD);
			if (modifyTok == null) {
				break;
			}
			Token textTok = recognizeModifyBeforeText(modifyTok, "FALSCHEAUSWAHLTEXT", ts);
			if (textTok != null) {
				falscheAuswahlText = textTok.getOriginalText();
				question = textTok.getAdditionalText();
				continue;
			}
			ts.backToPreviousToken();
			break;
		}
		if (question == null) {
			Token tokQuestion = ts.nextToken(TokenType.TEXT);
			if (tokQuestion == null) {
				throw new RuntimeException("Fehlende Frage: "+yndTok);
			}
			question = tokQuestion.getText();
		}
		YesNoDecision result = new YesNoDecision(question, falscheAuswahlText);
		Token choiceNameTok = nextNotStepKeywordToken(ts);
		while (choiceNameTok != null) {
			Choice choice = createChoice(choiceNameTok, ts);
			result.addChoice(choice);
			choiceNameTok = nextNotStepKeywordToken(ts);
		}
		return result;
	}


	private Choice createChoice(Token choiceNameTok, TokenStream ts) {
		Token textTok = ts.nextToken(TokenType.TEXT);
		String text = (textTok == null) ? null : textTok.getText();
		Token refTok = ts.nextToken(TokenType.REF);
		if (refTok == null) {
			throw new RuntimeException("Fehlende Kapitel Referenz für Auswahl: '"+choiceNameTok+"' statt dessen gefunden: '"+ts.peekToken()+"'");
		}
		Choice result = new Choice(choiceNameTok.getText(), text, refTok.getText());
		Token phraseTok = ts.nextToken(TokenType.PHRASE);
		while (phraseTok != null) {
			result.addPhrases(phraseTok.getText());
			phraseTok = ts.nextToken(TokenType.PHRASE);
		}
		return result;
	}

	private List<Token> mergeTexts(List<Token> tokens) {
		List<Token> result = new ArrayList<Token>();
		Token lastToken = null;
		for (Token tok:tokens) {
			if (tok.isType(TokenType.TEXT)) {
				if (tok.getText().isEmpty()) {
					continue;
				}
				if ((lastToken != null) && lastToken.isType(TokenType.TEXT)) {
					lastToken.addText(tok.getText());
				}
				else {
					result.add(tok);
					lastToken = tok;
				}
			}
			else {
				result.add(tok);
				lastToken = tok;
			}
		}
		return result;
	}

	public List<Token> tokenize(String input) {
		List<Token> result = new ArrayList<Token>();
		String[] lines = input.replace("\r\n", "\n").replace("\r", "\n").split("\n");
		int cntLine = 0;
		for (String line:lines) {
			cntLine += 1;
			if (line.matches(COMMENT_LINERX)) {
				// skip comments
			}
			else if (line.matches(CONDITION_LINERX)) {
				String keyWord = line.replaceFirst(CONDITION_LINERX, "$1");
				String reftext = line.replaceFirst(CONDITION_LINERX, "$2");
				String followText = line.replaceFirst(CONDITION_LINERX, "$3").trim();
				result.add(new Token(cntLine, TokenType.FUNC, keyWord));
				result.add(new Token(cntLine, TokenType.FUNCPARAM, reftext));
				tokenizeFollowingText(result, cntLine, followText);
			}
			else if (line.matches(CONDITION_2ARG_LINERX)) {
				String keyWord = line.replaceFirst(CONDITION_2ARG_LINERX, "$1");
				String reftext = line.replaceFirst(CONDITION_2ARG_LINERX, "$2");
				String counttext = line.replaceFirst(CONDITION_2ARG_LINERX, "$3");
				String followText = line.replaceFirst(CONDITION_2ARG_LINERX, "$4").trim();
				result.add(new Token(cntLine, TokenType.FUNC, keyWord));
				result.add(new Token(cntLine, TokenType.FUNCPARAM, reftext));
				result.add(new Token(cntLine, TokenType.FUNCPARAM, counttext));
				tokenizeFollowingText(result, cntLine, followText);
			}
			else if (line.matches(KEYWORD_LINERX)) {
				String keyWord = line.replaceFirst(KEYWORD_LINERX, "$1");
				String followText = line.replaceFirst(KEYWORD_LINERX, "$2").trim();
				result.add(new Token(cntLine, TokenType.KEYWORD, keyWord));
				tokenizeFollowingText(result, cntLine, followText);
			}
			else if (line.matches(CHAPTER_LINERX)) {
				String chapter = line.replaceFirst(CHAPTER_LINERX, "$1");
				result.add(new Token(cntLine, TokenType.CHAPTER, chapter));
			}
			else {
				line = line.trim();
				if (!line.isEmpty()) {
					result.add(new Token(cntLine, TokenType.TEXT, line));
				}
			}
		}
		return result;
	}


	private void tokenizeFollowingText(List<Token> result, int cntLine, String followText) {
		if (followText.matches(TEXT_REF_TEXT_RX)) {
			String text1 = followText.replaceFirst(TEXT_REF_TEXT_RX, "$1").trim();
			String ref = followText.replaceFirst(TEXT_REF_TEXT_RX, "$2");
			String text2 = followText.replaceFirst(TEXT_REF_TEXT_RX, "$3").trim();
			if (!text1.isEmpty()) {
				result.add(new Token(cntLine, TokenType.TEXT, text1));
			}
			result.add(new Token(cntLine, TokenType.REF, ref));
			if (!text2.isEmpty()) {
				if (text2.startsWith("=")) {
					String[] phrases = text2.substring(1).trim().split("\\s*\\|\\s*");
					for (String phrase:phrases) {
						result.add(new Token(cntLine, TokenType.PHRASE, phrase));
					}
				}
				else {
					result.add(new Token(cntLine, TokenType.TEXT, text2));
				}
			}
		}
		else if (!followText.isEmpty()) {
			result.add(new Token(cntLine, TokenType.TEXT, followText));
		}
	}
}
