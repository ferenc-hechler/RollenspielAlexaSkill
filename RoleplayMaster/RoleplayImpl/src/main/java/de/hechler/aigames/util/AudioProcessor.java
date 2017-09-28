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
package de.hechler.aigames.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hechler.soloroleplay.util.AudioUtil;
import de.hechler.soloroleplay.util.AudioUtil.AudioInfo;
import de.hechler.soloroleplay.util.TextUtil;
import de.hechler.soloroleplay.util.audio.FFMpegExecutor;


public class AudioProcessor {

	private final static Logger logger = Logger.getLogger(AudioProcessor.class.getName());

	private String ffmpegBinary;
	private String baseFolder;
	private String inputFolder;
	private String workingFolder;
	private String outputFolder;

	public AudioProcessor() {
		readConfig();
	}
	
	private void readConfig() {
		try {
			Properties props = new Properties();
			String configFolder = System.getProperty("audioprocessor-conf", "/etc");
			File audioprocessorPropsFile = Paths.get(configFolder).resolve("calcboxaudioprocessor.properties").toFile().getAbsoluteFile();
			if (!audioprocessorPropsFile.exists()) {
				logger.info("using default properties, no file '"+audioprocessorPropsFile.toString()+"' found");
			}
			else {
				props.load(new FileInputStream(audioprocessorPropsFile));
			}
			if (isWindows()) {
				setWindowsDefaults();
			}
			else {
				setLinuxDefaults();
			}
			ffmpegBinary = props.getProperty("audioprocessor.ffmpegBinary", ffmpegBinary); 
			baseFolder = props.getProperty("audioprocessor.baseFolder", baseFolder);
			inputFolder = props.getProperty("audioprocessor.inputFolder", inputFolder); 
			workingFolder = props.getProperty("audioprocessor.workingFolder", workingFolder); 
			outputFolder = props.getProperty("audioprocessor.outputFolder", outputFolder); 
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name", "win").toLowerCase().contains("win");
	}

	private void setWindowsDefaults() {
		ffmpegBinary = "C:/DEV/bin/ffmpeg/bin/ffmpeg.exe"; 
		baseFolder = "E:/VAR/AUDIO";
		inputFolder = "IN"; 
		workingFolder = "WORK"; 
		outputFolder = "OUT"; 
	}

	private void setLinuxDefaults() {
		ffmpegBinary = "/usr/bin/avconv"; 
		baseFolder = "/var/AUDIO";
		inputFolder = "IN"; 
		workingFolder = "WORK"; 
		outputFolder = "OUT"; 
	}

	
	public String processAudio(String audioSoloText) {
		
		try {
			
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
			
			String fullText = TextUtil.makeEndl(audioSoloText);
			List<AudioInfo> audioInfos = AudioUtil.collectAudioInfo(fullText);
			int defaultVolumePercent = 100;
			for (AudioInfo audioInfo:audioInfos) {
				if ("DEFAULT".equals(audioInfo.getFilename())) {
					defaultVolumePercent = audioInfo.getVolumePercent();
					continue;
				}
				if (audioInfo.getVolumePercent() == -1) {
					audioInfo.setVolumePercent(defaultVolumePercent);
				}
				String ffmpegCmd = AudioUtil.processAudioCmdLine(audioInfo, inputFolder, workingFolder);
				audioInfo.setFfmpegCmd(ffmpegCmd);
			}
			
			FFMpegExecutor ffmpeg = new FFMpegExecutor(ffmpegBinary, baseFolder);
			for (AudioInfo audioInfo:audioInfos) {
				if ("DEFAULT".equals(audioInfo.getFilename())) {
					continue;
				}
				String output;
				if (exists(workingPath, audioInfo.getTargetName())) {
					String queryCmd = AudioUtil.queryAudioCmdLine(workingFolder, audioInfo.getTargetName());
					logger.log(Level.INFO, queryCmd);
					output = ffmpeg.start(queryCmd.replaceFirst("^ffmpeg\\s+", ""));
				}
				else {
					String ffmpegCmd = audioInfo.getFfmpegCmd();
					logger.log(Level.INFO, ffmpegCmd);
					output = ffmpeg.start(ffmpegCmd.replaceFirst("^ffmpeg\\s+", ""));
				}
				String durationString = output.replace('\r', ' ').replace('\n', ' ').replaceFirst("^.*time[=]([0-9:.]*) .*$", "$1");
				audioInfo.setDurationString(durationString);
				copyWorkingToOutFile(audioInfo, workingPath, outputPath);
			}
			
			StringBuilder result = new StringBuilder(fullText); 
			for (int i=audioInfos.size()-1; i>=0; i--) {
				AudioInfo audioInfo = audioInfos.get(i);
				String newAudio;
				if ("DEFAULT".equals(audioInfo.getFilename())) {
					newAudio = "";
				}
				else {
					newAudio = "AUDIO("+audioInfo.getTargetName();
					if ((audioInfo.getText() != null) && !audioInfo.getText().isEmpty()) {
						newAudio += "|" + audioInfo.getText(); 
					}
					newAudio += ")"; 
				}
				result.replace(audioInfo.getSrcStartPos(), audioInfo.getSrcEndPos(), newAudio);
			}
//			logger.log(Level.FINE, "[----------------------]");
//			logger.log(Level.FINE, result.toString());
//			logger.log(Level.FINE, "[----------------------]");
			return result.toString();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean exists(Path folder, String filename) {
		boolean result = Files.exists(folder.resolve(filename));
		return result;
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
				try {
					Files.setPosixFilePermissions(dest, PosixFilePermissions.fromString("rw-r--r--"));
				} 
				catch (UnsupportedOperationException e) { /* windows... */ }
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		audioInfo.setTargetName(outFilenname);
	}

	
}
