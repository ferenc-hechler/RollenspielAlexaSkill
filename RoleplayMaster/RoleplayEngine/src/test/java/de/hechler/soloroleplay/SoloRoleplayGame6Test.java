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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hechler.soloroleplay.data.Response;
import de.hechler.soloroleplay.data.Response.QuestionType;
import de.hechler.soloroleplay.data.SoloRoleplayData;
import de.hechler.soloroleplay.data.ValidationException;
import de.hechler.soloroleplay.parser.SoloRoleplayParser;
import de.hechler.soloroleplay.util.AudioUtil;
import de.hechler.soloroleplay.util.AudioUtil.AudioInfo;
import de.hechler.soloroleplay.util.audio.FFMpegExecutor;
import de.hechler.soloroleplay.util.TextUtil;

public class SoloRoleplayGame6Test {

	private String soloText;
	private SoloRoleplayData soloData; 
	
	@Before
	public void setup() throws ValidationException {
		soloText = TextUtil.readResource("beispiel6_solo.txt", StandardCharsets.ISO_8859_1);
		SoloRoleplayParser parser = new SoloRoleplayParser();
		soloData = parser.parse(soloText);
		soloData.validate();
	}
	
	
	@Test
	public void testExampleSolo6Output() {
		String expected = TextUtil.readResource("expected6_solotext_output.txt", StandardCharsets.ISO_8859_1);
		expected = TextUtil.makeEndl(expected);
		String actual = TextUtil.makeEndl(soloData.toString());
		System.out.println(soloData.toString());
		Assert.assertEquals(expected, actual);
	}

	// @Test
	public void testParseAudios() {
		
		try {
			String ffmpegBinary = "C:/DEV/bin/ffmpeg/bin/ffmpeg.exe";
			String baseFolder = "C:/DEV/workspace/MP3Editor/src/main/input/FFM-TEST";
			String inputFolder = "IN";
			String workingFolder = "WORK";
			String outputFolder = "OUT";
			
			Path basePath = Paths.get(baseFolder); 
			Path inputPath = basePath.resolve(inputFolder);  
			Path workingPath = basePath.resolve(workingFolder);  
			Path outputPath = basePath.resolve(outputFolder);  
			
			if (!Files.exists(inputPath)) {
				throw new RuntimeException("input folder '"+inputPath+"' does not exist!");
			}
			if (!Files.exists(workingPath)) {
				Files.createDirectories(workingPath);
			}
			if (!Files.exists(outputPath)) {
				Files.createDirectories(outputPath);
			}
			
			String fullText = TextUtil.makeEndl(soloData.toString());
			List<AudioInfo> audioInfos = AudioUtil.collectAudioInfo(fullText);
			for (AudioInfo audioInfo:audioInfos) {
	//			System.out.println(audioInfo);
				String ffmpegCmd = AudioUtil.processAudioCmdLine(audioInfo, inputFolder, workingFolder);
				audioInfo.setFfmpegCmd(ffmpegCmd);
			}
			
			FFMpegExecutor ffmpeg = new FFMpegExecutor(ffmpegBinary, baseFolder);
			for (AudioInfo audioInfo:audioInfos) {
				Files.deleteIfExists(workingPath.resolve(audioInfo.getTargetName()));
				String ffmpegCmd = audioInfo.getFfmpegCmd();
				System.out.println(ffmpegCmd);
				String result = ffmpeg.start(ffmpegCmd.replaceFirst("^ffmpeg\\s+", ""));
				if (result.contains("already exists")) {
					String queryCmd = AudioUtil.queryAudioCmdLine(workingFolder, audioInfo.getTargetName());
					System.out.println(queryCmd);
					result = ffmpeg.start(queryCmd.replaceFirst("^ffmpeg\\s+", ""));
				}
				String durationString = result.replace('\r', ' ').replace('\n', ' ').replaceFirst("^.*time[=]([0-9:.]*) .*$", "$1");
				audioInfo.setDurationString(durationString);
				copyWorkingToOutFile(audioInfo, workingPath, outputPath);
				System.out.println(audioInfo.getDuration());
			}
			
			StringBuilder outputText = new StringBuilder(fullText); 
			for (int i=audioInfos.size()-1; i>=0; i--) {
				AudioInfo audioInfo = audioInfos.get(i);
				String newAudio = "AUDIO("+audioInfo.getTargetName();
				if ((audioInfo.getText() != null) && !audioInfo.getText().isEmpty()) {
					newAudio += "|" + audioInfo.getText(); 
				}
				newAudio += ")"; 
				outputText.replace(audioInfo.getSrcStartPos(), audioInfo.getSrcEndPos(), newAudio);
			}
			System.out.println("[----------------------]");
			System.out.println(outputText.toString());
			System.out.println("[----------------------]");
			System.out.println("cd %~dp0"); 
			System.out.println("set PATH=C:/DEV/bin/ffmpeg/bin;%PATH%"); 
			System.out.println(""); 
	
			
			System.out.println(""); 
			System.out.println("pause"); 
			System.out.println("[----------------------]");
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void copyWorkingToOutFile(AudioInfo audioInfo, Path srcFolder, Path destFolder) {
		String filename = audioInfo.getTargetName();
		int duration = audioInfo.getDuration();
		String targetName = filename.replaceFirst("^(.*)([.][^.]*)$", "$1");
		String targetNameExt = filename.replaceFirst("^(.*)([.][^.]*)$", "$2");
		String outFilenname = targetName + "_D"+duration+targetNameExt;
		Path src = srcFolder.resolve(filename);
		Path dest = destFolder.resolve(outFilenname);
		if (!Files.exists(dest)) {
			try {
				Files.copy(src, dest);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		audioInfo.setTargetName(outFilenname);
	}


	@Test
	public void testExampleSolo6() {
		UserInput input = new UserInput(null, getClass().getResourceAsStream("/solo6_user_input.txt"), StandardCharsets.ISO_8859_1.name());
		StringBuilder result = new StringBuilder();
		
		try {
			SoloRoleplayGame game = new SoloRoleplayGame(soloData);
			result.append("Wilkommen zum Abenteuer '"+soloData.getTitle()+"' von "+soloData.getAuthor()).append(TextUtil.endl);
			System.out.println(result.toString());
			result.append(TextUtil.endl);
			boolean shortenTextActive = false;
			while (true) {
				Response response = game.executeFromLast(shortenTextActive);
				if (response.hasText()) {
					System.out.println(response.getText().toString());
					result.append(response.getText().toString()).append(TextUtil.endl);
				}
				if (response.isType(QuestionType.FINISHED)) {
					break;
				}
				String line;
				line = input.nextLine();
				System.out.println(" > "+line);
				System.out.println();
				result.append(" > ").append(line).append(TextUtil.endl);
				result.append(TextUtil.endl);
				response = game.processAnswer(line, shortenTextActive);
				if (response.hasText()) {
					System.out.println(response.getText().toString());
					result.append(response.getText().toString()).append(TextUtil.endl);
				}
				if (response.isType(QuestionType.FINISHED)) {
					break;
				}
			}
			String expected = TextUtil.readResource("expected6_solorun_output.txt", StandardCharsets.ISO_8859_1);
			expected = TextUtil.makeEndl(expected);
			String actual = TextUtil.makeEndl(result.toString());
			Assert.assertEquals(expected, actual);
		} catch (NoSuchElementException e) {
//			System.out.println(result.toString());
			Assert.fail("end of input reached at: "+result.toString());
		}
	}
}
