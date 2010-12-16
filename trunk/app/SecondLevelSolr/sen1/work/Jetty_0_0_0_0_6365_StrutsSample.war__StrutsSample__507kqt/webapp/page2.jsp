<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html:html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>Cloud Search Engine</title>
	</head>
	<body>
		<center>
		<font size="5">Cloud Search Engine</font>
			<html:form method="POST" action="action2.do">
				<html:text property="query" size="80"/>
				<html:submit value="検索"/>
			</html:form>
		</center>
		<br>
		<hr width="80%">
		<br>
		<span>
			${requestScope.msg}
		</span>
	</body>
</html:html>