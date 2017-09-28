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
package de.hechler.soloroleplay.util.audio;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FFMpegExecutor {

	private String binary;
	private File workingFolder;
	private Process proc;
	
	public FFMpegExecutor(String ffmpegBinary, String workingDir) {
		this.workingFolder = new File(workingDir);
		if (!workingFolder.exists()) {
			throw new RuntimeException("working dir not found: "+workingFolder.getAbsolutePath());
		}
		Path ffmpegBinaryPath = Paths.get(ffmpegBinary);
		if (!Files.exists(ffmpegBinaryPath)) {
			throw new RuntimeException("binary not found: "+ffmpegBinaryPath.toAbsolutePath().toString());
		}
		binary = ffmpegBinaryPath.toAbsolutePath().toString();
	}
	
	public String start(String params) {
		try {
			proc = Runtime.getRuntime().exec(binary+" "+params, null, workingFolder);
			String output = readOutput();
			String error = readError();
			return output + error;
		} catch (IOException e) { 
			e.printStackTrace();
			return e.toString();
		}
	}

	private String readOutput() throws IOException {
		InputStream in = proc.getInputStream();
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}

	private String readError() throws IOException {
		InputStream in = proc.getErrorStream();
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString(StandardCharsets.UTF_8.name());
	}

	
}
