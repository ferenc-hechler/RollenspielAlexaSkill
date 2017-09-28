<%@page import="de.hechler.soloroleplay.data.Response.QuestionType"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="de.hechler.aigames.soloroleplay.SoloRoleplayImpl"%>
<%@page import="de.hechler.soloroleplay.SoloRoleplayGame"%>
<%@page import="de.hechler.soloroleplay.data.Response"%>
<%
	request.setCharacterEncoding("UTF-8");
	String answer = request.getParameter("answer");
	boolean finished = false;
	Response resp;
	if (answer == null) {
		String text = (String) request.getSession(true).getAttribute("solotext");
		SoloRoleplayGame game = SoloRoleplayImpl.createGame(text);
		request.getSession(true).setAttribute("game", game);
		resp = game.executeFromLast();
	}
	else {
		SoloRoleplayGame game = (SoloRoleplayGame) request.getSession(true).getAttribute("game");
		resp = game.processAnswer(answer);
		game.executeFromCurrent(resp);
		if (resp.isType(QuestionType.FINISHED)) {
			finished = true;
		}
	}
	String output = resp.getText().toString();
	output = output.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");

%>
<html>
<head>
<title>Soloabenteuer Testplayer</title>
<meta charset="UTF-8">
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="pragma" content="no-cache" />

</head>
<body>

<h1>Soloabenteuer Testplayer</h1>

<a href="upload.jsp">Upload zu deiner Alexa (nur privat)</a><br>

<textarea name="output" readonly="true" rows="20" cols="80">
<%= output %>
</textarea><br>
		
<% if (!finished) { %>
	<form method="post" action="play.jsp">
	    <label for="name">Antwort:</label>
	    <input type="text" name="answer" id="answer" autofocus="true">
	    <input type="submit" value="Submit">
	</form>
<% } %>	


</body>
</html>