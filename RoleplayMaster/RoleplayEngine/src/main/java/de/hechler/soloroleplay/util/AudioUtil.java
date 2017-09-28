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
package de.hechler.soloroleplay.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AudioUtil {

	public static class AudioInfo {
		private String filename;
		private int startMilli;
		private int endMilli;
		private String text;
		private int duration;
		private String ffmpegCmd;
		
		private int volumePercent;
		
		private int srcStartPos;
		private int srcEndPos;
		
		public String targetName;
		
		public AudioInfo(String filename, int startMilli, int endMilli, String text, int volumePercent) {
			this.filename = filename;
			this.startMilli = startMilli;
			this.endMilli = endMilli;
			this.text = text;
			this.volumePercent = volumePercent;
			this.ffmpegCmd = null;
		}
		public String getFilename() 	{ return filename; }
		public int getStartMilli()  	{ return startMilli; }
		public int getEndMilli()    	{ return endMilli; }
		public int getDurationMilli()	{ return (endMilli == -1) ? -1 : ((startMilli == -1) ? endMilli : (endMilli-startMilli)); }
		public int getVolumePercent()  	{ return volumePercent; }
		public String getText()     	{ return text; }
		public String getStartSecString()		{ return (startMilli==-1)		  ? "" : milli2secString(startMilli); 		  }
		public String getEndSecString() 		{ return (endMilli==-1)   		  ? "" : milli2secString(endMilli);   		  }
		public String getDurationSecString()	{ return (getDurationMilli()==-1) ? "" : milli2secString(getDurationMilli()); }
		public String getTargetName()			{ return targetName; }

		public void setVolumePercent(int volumePercent) { this.volumePercent = volumePercent; }
		public void setTargetName(String targetName) 	{ this.targetName = targetName; }

		public int getSrcStartPos()  	{ return srcStartPos; }
		public int getSrcEndPos()  	{ return srcEndPos; }

		public void setFfmpegCmd(String ffmpegCmd) { this.ffmpegCmd = ffmpegCmd; }
		public String getFfmpegCmd() { return ffmpegCmd; }



		
		private final static String NO_SEPERATOR = "[^|()\\[\\]]";
//		private final static String NUMRANGE = "[0-9.]*[-][0-9.]*";
		private final static String AUDIO_FORMAT_RX = 
				"AUDIO[(]"
						+ "("    + NO_SEPERATOR+"+" + ")"   	// "filename.mp3"
						+ "(\\[" + NO_SEPERATOR+"+" + "\\])?"	// "[1.327-3.790,vol=120]"
						+ "([|]" + NO_SEPERATOR+"*" + ")?"		// "|das ist der Text."
				+ "[)]";
		private final static String OPTIONS_RX = "\\[(" + NO_SEPERATOR+"+" + ")\\]";
		private final static String RANGE_RX = "([0-9.]*)[-]([0-9.]*)";
		private final static String VOLUME_RX = "vol[=]([0-9]+)";
		
		public static AudioInfo parseAudioString(String audioString) {
			if (!audioString.matches(AUDIO_FORMAT_RX)) {
				throw new RuntimeException("invalid audio format '"+audioString+"'");
			}
			String filename = audioString.replaceFirst(AUDIO_FORMAT_RX, "$1");
			String options = audioString.replaceFirst(AUDIO_FORMAT_RX, "$2");
			String text = audioString.replaceFirst(AUDIO_FORMAT_RX, "$3");
			
			int startMillis = -1;
			int endMillis = -1;
			int volume = -1;
			
			options = options.replaceFirst(OPTIONS_RX, "$1");
			if (!options.isEmpty()) {
				String[] audioOptionsArray = options.split("[,]");
				for (String audioOption:audioOptionsArray) {
					if (audioOption.matches(RANGE_RX)) {
						String startSecString = audioOption.replaceFirst(RANGE_RX, "$1");
						String endSecString = audioOption.replaceFirst(RANGE_RX, "$2");
						startMillis = (startSecString.isEmpty()) ? -1 : (int) Math.round(Double.parseDouble(startSecString)*1000.0 + 0.00001);
						endMillis = (endSecString.isEmpty()) ? -1 : (int) Math.round(Double.parseDouble(endSecString)*1000.0 + 0.00001);
					}
					else if (audioOption.matches(VOLUME_RX)) {
						String volStr = audioOption.replaceFirst(VOLUME_RX, "$1");
						volume = Integer.parseInt(volStr);
					}
					else {
						throw new RuntimeException("invalid audio option '"+audioOption+"'");
					}
				}
			}
			if (!text.isEmpty()) {
				text = text.substring(1);
			}
			return new AudioInfo(filename, startMillis, endMillis, text, volume);
		}

		@Override
		public String toString() {
			StringBuilder result = new StringBuilder();
			result.append("AUDIO(").append(filename);
			if ((startMilli != -1) || (endMilli != -1) || (volumePercent != -1)) {
				result.append("[");
				if ((startMilli != -1) || (endMilli != -1)) {
					result.append(getStartSecString()).append("-").append(getEndSecString());
					if (volumePercent != -1) {
						result.append(",");
					}
				}
				if (volumePercent != -1) {
					result.append("vol=").append(Integer.toString(volumePercent));
				}
				result.append("]");
			}
			if ((text != null) && !text.isEmpty()) {
				result.append("|").append(text);
			}
			result.append(")");
			return result.toString();
		}
		public void setSrcPos(int srcStartPos, int srcEndPos) {
			this.srcStartPos = srcStartPos;
			this.srcEndPos = srcEndPos;
		}
		public void setDurationString(String durationString) {
			duration = time2hundreds(durationString);
		}
		public int getDuration() { return duration; }
		public String getDurationString() { return hundreds2time(duration); }
		
	}

	

	
	private final static Pattern audioPattern = Pattern.compile("AUDIO[(]([^)]*)[)]"); 
	
	public static List<AudioInfo> collectAudioInfo(String text) {
		List<AudioInfo> result = new ArrayList<AudioInfo>();
		Matcher matcher = audioPattern.matcher(text);
		while (matcher.find()) {
			String audioString = matcher.group(0);
			try {
				AudioInfo audioInfo = AudioInfo.parseAudioString(audioString);
				audioInfo.setSrcPos(matcher.start(), matcher.end());
				result.add(audioInfo);
			}
			catch (RuntimeException e) {
				int pos = matcher.start();
				int line = TextUtil.pos2line(text, pos);
				int lineStartPos = TextUtil.line2pos(text, line);
				String info = "Zeile:"+line+", Spalte:"+(pos-lineStartPos+1);
				info += " \"" + audioString.replaceFirst("^(.*?)\\|.*$", "$1") +"\"";
				throw new RuntimeException(info+" - "+e.getMessage(), e);
			}
		}
		return result;
	}
	
	public static String processAudioCmdLine(AudioInfo audioInfo, String inputFolder, String outputFolder) {
		String targetName = audioInfo.getFilename().replaceFirst("^(.*)([.][^.]*)$", "$1");
		String targetNameExt =    audioInfo.getFilename().replaceFirst("^(.*)([.][^.]*)$", "$2");
		String seek = "";
		if (audioInfo.getStartMilli() != -1) {
			seek = " -ss " + audioInfo.getStartSecString();
			targetName = targetName+"_S"+audioInfo.getStartMilli();
		}
		String duration = "";
		if (audioInfo.getDurationMilli() != -1) {
			duration = " -t " + audioInfo.getDurationSecString();
			targetName = targetName+"_E"+audioInfo.getEndMilli();
		}
		String volume = "";
		if (audioInfo.getVolumePercent() != -1) {
			volume = " -vol "+((int) (audioInfo.getVolumePercent()*256/100));
			targetName = targetName+"_V"+audioInfo.getVolumePercent();
		}
//		String noOverwrite = " -n";
		
		String inFilename = audioInfo.getFilename();
		if ((inputFolder != null) && !inputFolder.isEmpty()) {
			inFilename = inputFolder + "/" + inFilename; 
		}
		String input = " -i "+inFilename;
		String encoding = " -ac 2 -codec:a libmp3lame -b:a 48k -ar 16000";
		targetName = "alx_"+targetName + targetNameExt;
		audioInfo.setTargetName(targetName);
		String output;
		if ((outputFolder != null) && !outputFolder.isEmpty()) {
			output = " " + outputFolder+"/"+targetName;
		}
		else {
			output = " " + targetName;
		}
		return "ffmpeg" /* + noOverwrite */ + input + volume + encoding + seek + duration + output;
	}

	public static String queryAudioCmdLine(String inputFolder, String filename) {
		String inFilename = filename;
		if ((inputFolder != null) && !inputFolder.isEmpty()) {
			inFilename = inputFolder + "/" + inFilename; 
		}
		String input = " -i "+inFilename;
		String query = " -f null -";
		return "ffmpeg" + input + query;
	}
	

	public static String milli2secString(int milli) {
		return NumberUtil.num2decStr(0.001*milli, 3);
	}
	
	public static String percent2decString(int percent) {
		return NumberUtil.num2decStr(0.01*percent, 2);
	}
	
	public static String tenth2decString(int tenth) {
		return NumberUtil.num2decStr(0.1*tenth, 1);
	}
	
	private final static String TIME_FIX_RX = "^(\\d{2})[:](\\d{2})[:](\\d{2})[.](\\d{2})$";
	private final static String TIME_DYN_RX = "^([0-9:]+)[.]([0-9]+)$";
	private final static String TIME_HMS_SEPERATOR_RX = "^([0-9]+)[:]([0-9:]+)$";
	
	public static int time2hundreds(String time) {
		int result;
		if (time.matches(TIME_FIX_RX)) {
			String hours = time.replaceFirst(TIME_FIX_RX, "$1");
			String mins = time.replaceFirst(TIME_FIX_RX, "$2");
			String secs = time.replaceFirst(TIME_FIX_RX, "$3");
			String fract = time.replaceFirst(TIME_FIX_RX, "$4");
			result = ((Integer.parseInt(hours)*60+Integer.parseInt(mins))*60+Integer.parseInt(secs))*100+Integer.parseInt(fract);
		}
		else if (!time.matches(TIME_DYN_RX)) {
				throw new RuntimeException("invalid time format '"+time+"'");
		}
		else {
			result = 0;
			String hms = time.replaceFirst(TIME_DYN_RX, "$1");
			String fract = time.replaceFirst(TIME_DYN_RX, "$2");
			while (hms.matches(TIME_HMS_SEPERATOR_RX)) {
				String num = hms.replaceFirst(TIME_HMS_SEPERATOR_RX, "$1");
				hms = hms.replaceFirst(TIME_HMS_SEPERATOR_RX, "$2");
				result = result * 60 + Integer.parseInt(num);
			}
			result = result * 60 + Integer.parseInt(hms);
			if (fract.length() == 1) {
				fract = "0"+fract;
			}
			else if (fract.length() > 2) {
				fract = fract.substring(0, 2);
			}
			result = result*100 + Integer.parseInt(fract);
		}
		return result;
	}
	
	public static String hundreds2time(int hundreds) {
		int sec = hundreds / 100; 
		int fract = hundreds - 100*sec; 
		int min = sec / 60;
		int secs = sec - 60*min; 
		int hours = min / 60;
		int mins = min - 60*hours;
		String result = NumberUtil.num2str(hours, 2)+":"+NumberUtil.num2str(mins, 2)+":"+NumberUtil.num2str(secs, 2)+"."+NumberUtil.num2str(fract, 2);
		return result;
	}
	

}
