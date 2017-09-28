<%@page import="de.hechler.soloroleplay.util.TextUtil"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="de.hechler.aigames.soloroleplay.SoloRoleplayImpl"%>
<%
	request.setCharacterEncoding("UTF-8");
	List<String> soloNames = SoloRoleplayImpl.getPublishedSoloNames();
%>
<html>
<head>
<title>Soloabenteuer für Alexa Übersicht</title>
<meta charset="UTF-8">
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="pragma" content="no-cache" />

</head>
<body>

<h1>Soloabenteuer Übersicht für den Alexa Rollenspiel Skill</h1>

<p>
Willkommen zum Alexa Rollenspiel Skill. Hier kannst du gegen Alexa ein Soloabenteuer spielen.
</p>

<p>
<img src="img/Logo-Fanprodukt.png">
</p>

<p>
Bei diesem Skill handelt es sich um ein Fan-Produkt.
DAS SCHWARZE AUGE, AVENTURIEN und DERE sind eingetragene Marken der Ulisses Medien und Spiel Distribution GmbH oder deren Partner.
</p>

<p>
Die folgenden Soloabenteuer wurden bereits publiziert und können auf Alexa gespielt werden.<br>
Klicke auf den Namen eines Abenteuers um mehr Infos darüber zu erfahren.
</p>

<ul>
<%
	for (int n=0; n<soloNames.size(); n++) {
		String link = "<a href='detail.jsp?n="+n+"'>"+TextUtil.escapeHtml(TextUtil.makeReadable(soloNames.get(n)))+"</a>";
		%>
		<li><%=link%></li>
		<%
	}
%>
</ul>

<p>
Hier eine Kurze Zusammenfassung der Kommandos zur Steuerung beim Spielen eines Soloabentuers, wenn Alexa aktiv zuhört:
</p>
<table>
<tr><td><code>Hilfe</code></td><td>Gibt die Liste der möglichen Kommandos aus.</td></tr>
<tr><td><code>Wiederhole&nbsp;letztes</code></td><td>Wiederholt die Ansage zur aktuellen Aktion (Probe / Kampf / ...).</td></tr>
<tr><td><code>Wiederhole&nbsp;alles</code></td><td>Wiederholt alles, was seit der letzten Aktion passiert ist.</td></tr>
<tr><td><code>Stop</code></td><td>Beendet den Eingabemodus des Soloabenteuers. Mit <code>Alexa starte Rollenspiel</code> wird das Abenteuer an der gleichen Stelle fortgesetzt.</td></tr>
<tr><td><code>Neues&nbsp;Spiel</code></td><td>Beendet das aktuelle Soloabenteuer und Beginnt wieder bei der Auswahlliste.</td></tr>
</table>

<p>
Nach ca. 15 Sekunden beendet Alexa den Skill automatisch. Das Spiel merkt sich aber den aktuellen Zustand (zumindest für eine Stunde). Mit den folgenden Kommandos kann das Spiel dann wieder fortgesetzt werden:  
</p>
<table>
<tr><td><code>Alexa starte Rollenspiel</code></td><td>Startet das Rollenspiel wieder an der Stelle, an der es abgebrochen wurde.</td></tr>
</table>

<h2>Soloabenteuer Generator</h2>

<p>
Du kannst auch eigene Soloabenteuer entwerfen und gegen Alexa spielen.
Nutze dafür den <a href="generator.jsp">Alexa Soloabenteuer Generator</a>!  
</p>		

</body>
</html>