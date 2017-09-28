<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="de.hechler.aigames.ai.soloroleplay.SoloRoleplayImpl"%>
<%
	request.setCharacterEncoding("UTF-8");
	String audioText = request.getParameter("audio-solotext");
	String audioValidieren = request.getParameter("audio-validieren");
	String spielen = request.getParameter("spielen");
	String audioValidationText = "[no audio-validation started]";
	String soloText = "";
	if (audioText == null) {
		audioText =  
				"TITEL: Audio Test Standards \r\n" + 
				"AUTOR: feri\r\n" + 
				"AUDIOBASEURL: https://calcbox.de/audio/punin\r\n" + 
				"\r\n" + 
				"KURZBESCHREIBUNG:\r\n" + 
				"Testet das Ersetzen der Standardtexte durch Audio-Dateien.\r\n" + 
				"\r\n" + 
				"[BEGINN]\r\n" + 
				"Es geht los.\r\n" + 
				"WEITER: [1]\r\n" + 
				"\r\n" + 
				"[1]\r\n" + 
				"AUDIO(ZOOM0001_MONO.mp3|Der Hauptmann von Punin. Ein Soloabenteuer für das Scharze Auge, geschrieben von Peter Michalski und Bernard Klitzke eingesprochen von Daniel Bartholomae.)\r\n" + 
				"AUDIO(ZOOM0001_MONO.mp3[0.000-2.000]|Der Hauptmann von Punin)\r\n" + 
				"WEITER: [ENDE]\r\n" + 
				"\r\n" + 
				"[ENDE]\r\n" + 
				"Und Schluss.\r\n";

	}
	else {
		String[] val_txt = SoloRoleplayImpl.audioValidate(audioText);
		audioValidationText = val_txt[0].replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"); 
		soloText = val_txt[1].replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	audioText = audioText.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

%>
<html>
<head>
<title>Soloabenteuer Audio-Generator für Alexa</title>
<meta charset="UTF-8">
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="pragma" content="no-cache" />
<% if (spielen != null) { %>
<script>window.open('play.jsp', 'Soloabenteuer');</script>
<% } %>

</head>
<body>

<h1>Soloabenteuer Audio-Generator für den Alexa Rollenspiel Skill</h1>

		
<p>Schreib dein eigenes Soloabenteuer für Alexa. Eine Anleitung dazu findest du hier: <a target="_blank" href="anleitung.html">Eigene Soloabenteuere für Alexa erstellen</a></p>
<p>Kontakt: ferenc.hechler(AT)gmail.com</p>

<form method="post" action="audio-generator.jsp">

<input type="submit" name="testen" value="Prüfen">

<% if (audioValidationText.endsWith("erfolgreich validiert.")) { 
	request.getSession(true).setAttribute("solotext", soloText); %>
  <a target="_blank" href="play.jsp">Testspiel starten</a><br>
<% } %>
<br>

<textarea name="audio-validation" readonly="true" rows="3" cols="80">
<%= audioValidationText %>
</textarea><br>

<p><b>ACHTUNG:</b> Der hier eingegebene Text wird nicht gespeichert.<br>
Behalte also an anderer Stelle eine Kopie deines mühevoll erstellten Abenteuers!</p> 

  
<textarea name="audio-solotext" rows="30" cols="80">
<%= audioText %>
</textarea><br>

<% if (!soloText.isEmpty()) { %> 
<textarea name="solotext" readonly="true" rows="10" cols="80">
<%= soloText %>
</textarea><br>
<% } %>


</form>

</body>
</html>