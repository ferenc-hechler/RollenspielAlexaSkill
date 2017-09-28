<%@page import="java.io.IOException"%>
<%@page import="java.io.Writer"%>
<%@page import="de.hechler.soloroleplay.data.SoloRoleplayData"%>
<%@page import="de.hechler.aigames.soloroleplay.SoloRepository"%>
<%@page import="de.hechler.soloroleplay.util.TextUtil"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html; charset=UTF-8" %>
<%@page import="de.hechler.aigames.soloroleplay.SoloRoleplayImpl"%>
<%!
private boolean textAdded = false;

private void writeHtmlRow(JspWriter wrt, String name, String value) throws IOException {
	if ((value == null) || value.trim().isEmpty()) {
		return;
	}
	String html = "<tr><td>"+name+"</td><td>"+TextUtil.escapeHtml(value)+"</td></tr>";
	wrt.println(html);
	textAdded = true;
}
private void writeHtmlRowLink(JspWriter wrt, String name, String linkValue) throws IOException {
	if ((linkValue == null) || linkValue.trim().isEmpty()) {
		return;
	}
	String url = TextUtil.escapeHtml(linkValue);
	String html = "<tr><td>"+name+"</td><td><a href=\""+url+"\">"+url+"</a></td></tr>";
	wrt.println(html);
	textAdded = true;
}
private void writeHtmlRow(JspWriter wrt, String value) throws IOException {
	if ((value == null) || value.trim().isEmpty()) {
		return;
	}
	String html = "<tr><td colspan=\"2\">"+TextUtil.escapeHtml(value)+"</td></tr>";
	wrt.println(html);
	textAdded = true;
}
private void writeHorizontalRow(JspWriter wrt) throws IOException {
	if (textAdded) {
		writeHtmlRow(wrt, "------------------------------------------------------------------------------------------");
	}
	textAdded = false;
}
%>
<%
	request.setCharacterEncoding("UTF-8");
	int n = Integer.parseInt(request.getParameter("n"));
	List<String> soloNames = SoloRoleplayImpl.getPublishedSoloNames();
	String soloName = TextUtil.makeReadable(soloNames.get(n));
	SoloRoleplayData soloData = SoloRepository.getInstance().getSolo(soloName);
%>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="pragma" content="no-cache" />
<title>Details zum Soloabenteuer <%= TextUtil.escapeHtml(soloName) %></title>

</head>
<body>

<h1>Details zum Soloabenteuer <%= TextUtil.escapeHtml(soloName) %></h1>

<p>
<a href="playPublished.jsp?soloName=<%= TextUtil.escapeHtml(soloName)  %>">Im Textsimulator spielen</a>
</p>

<table>
<%
	textAdded = false;

	writeHtmlRow(out, "Kurzname", TextUtil.makeReadable(soloData.getShortName()));
	writeHtmlRow(out, "Titel", TextUtil.makeReadable(soloData.getTitle()));
	writeHtmlRow(out, "Autor", soloData.getAuthor());
	writeHtmlRow(out, "Beschreibung", soloData.getDescription());

	writeHorizontalRow(out);
	
	writeHtmlRow(out, "#Zeichen", Integer.toString(soloData.getCharSize()));
	writeHtmlRow(out, "#Kapitel", Integer.toString(soloData.getCountChapters()));

	writeHorizontalRow(out);
	
	writeHtmlRow(out, "Regelset", soloData.getRuleset());
	writeHtmlRow(out, "Stufe", soloData.getLevel());
	writeHtmlRow(out, "Ort", soloData.getLocation());
	writeHtmlRow(out, "Spieldauer", soloData.getDuration());

	writeHorizontalRow(out);

	writeHtmlRowLink(out, "Webseite", soloData.getWebSite());
	writeHtmlRow(out, "EMail", soloData.getContactMail());

	writeHorizontalRow(out);

	writeHtmlRow(out, "Lizenz", soloData.getLicense());
	writeHtmlRowLink(out, "LizenzURL", soloData.getLicenseUrl());

	writeHorizontalRow(out);

	writeHtmlRow(out, "Erschienen", soloData.getPublishingDate());
	writeHtmlRow(out, "Letztes Update", soloData.getLastUpdate());

	writeHorizontalRow(out);

	writeHtmlRow(out, "Anmerkungen", soloData.getNotes());
%>
</table>		


</body>
</html>