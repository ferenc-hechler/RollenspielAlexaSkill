<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="de.hechler.aigames.soloroleplay.SoloRoleplayImpl"%>
<%@page import="de.hechler.soloroleplay.util.TextUtil"%>
<%@page import="de.hechler.utils.SpeechUtils"%>
<%
	request.setCharacterEncoding("UTF-8");
	String soloText = (String) request.getSession(true).getAttribute("solotext");
	String uploadID = SoloRoleplayImpl.upload(soloText);
	String spelledUploadID = SpeechUtils.spellId(uploadID);
%>


<html>
<head>
<title>Soloabenteuer Upload</title>
<meta charset="UTF-8">
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="pragma" content="no-cache" />

</head>
<body>

<h1>Soloabenteuer Upload</h1>

<p>
Du kannst Dein Abenteuer jetzt mit Deiner Alexa verbinden, wenn Du innerhalb der nächsten 5 Minuten folgendes zu Deiner Alexa sagst:
</p>
<pre>
"ALEXA sage Rollenspiel lade Upload ID <%=uploadID%>" 
</pre>
<p>oder</p> 
<pre>
"ALEXA sage Rollenspiel lade Upload ID <%=spelledUploadID%>" 
</pre>

<p>
Danach ist Dein Rollenspiel für Deine Alexa sichtbar.

Wenn du es getestet hast und Dein Abenteuer veröffentlichen möchtest (oder einfach nur Fragen hast), 
dann sende eine Mail an ferenc.hechler(AT)gmail.com.
</p> 

</body>
</html>