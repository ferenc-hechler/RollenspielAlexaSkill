<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="de.hechler.aigames.ai.soloroleplay.SoloRoleplayImpl"%>
<%
	request.setCharacterEncoding("UTF-8");
	String text = request.getParameter("solotext");
	String validieren = request.getParameter("validieren");
	String spielen = request.getParameter("spielen");
	String validationText = "[no validation started]";
	if (text == null) {
		text =  "TITEL: Beispielabenteuer\r\n" + 
				"AUTOR: Unbekannt\r\n" + 
				"\r\n" + 
				"KURZBESCHREIBUNG:\r\n" + 
				"Dies ist ein Beispielabenteuer.\r\n" + 
				"\r\n" + 
				"\r\n" + 
				"[BEGINN]\r\n" + 
				"Das Abenteuer beginnt.\r\n" + 
				"WEITER: [1]\r\n" +
				"\r\n" +
				"[1]\r\n" + 
				"Hier fehlt noch ein spannender Text.\r\n" + 
				"\r\n" +
				"JANEINENTSCHEIDUNG: \r\n" + 
				"Möchtest du das Abenteuer beenden?\r\n" + 
				"JA:   [ENDE]\r\n" + 
				"NEIN: [1]\r\n" + 
				"\r\n" + 
				"[ENDE]\r\n" + 
				"Super, Du hast es geschafft!\r\n" + 
				"\r\n"; 
	}
	else {
		validationText = SoloRoleplayImpl.validate(text).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}
	text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

%>
<html>
<head>
<title>Soloabenteuer Generator für Alexa</title>
<meta charset="UTF-8">
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="pragma" content="no-cache" />
<% if (spielen != null) { %>
<script>window.open('play.jsp', 'Soloabenteuer');</script>
<% } %>

</head>
<body>

<h1>Soloabenteuer Generator für den Alexa Rollenspiel Skill</h1>

		
<p>Schreib dein eigenes Soloabenteuer für Alexa. Eine Anleitung dazu findest du hier: <a target="_blank" href="anleitung.html">Eigene Soloabenteuere für Alexa erstellen</a></p>
<p>Kontakt: ferenc.hechler(AT)gmail.com</p>

<form method="post" action="generator.jsp">

<input type="submit" name="testen" value="Prüfen">

<% if (validationText.endsWith("erfolgreich validiert.")) { 
	request.getSession(true).setAttribute("solotext", text); %>
  <a target="_blank" href="play.jsp">Testspiel starten</a><br>
<% } %>
<br>

<textarea name="validation" readonly="true" rows="3" cols="80">
<%= validationText %>
</textarea><br>

<p><b>ACHTUNG:</b> Der hier eingegebene Text wird nicht gespeichert.<br>
Behalte also an anderer Stelle eine Kopie deines mühevoll erstellten Abenteuers!</p> 

  
<textarea name="solotext" rows="30" cols="80">
<%= text %>
</textarea><br>

</form>

</body>
</html>