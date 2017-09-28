<%@page import="de.hechler.soloroleplay.util.TextUtil"%>
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
		String soloName = request.getParameter("soloName");
		SoloRoleplayGame game = SoloRoleplayImpl.startGame(soloName);
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
	String output = TextUtil.escapeHtml(resp.getText().toString());

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

<textarea name="output" readonly="true" rows="20" cols="80">
<%= output %>
</textarea><br>
		
<% if (!finished) { %>
	<form method="post" action="playPublished.jsp">
	    <label for="name">Antwort:</label>
	    <input type="text" name="answer" id="answer" autofocus="true">
	    <input type="submit" value="Submit">
	</form>
<% } %>	


</body>
</html>