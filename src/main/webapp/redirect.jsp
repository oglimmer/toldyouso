<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %><%
	String root = request.getContextPath();
	if (root == null || root.trim().isEmpty()) {
		root = "/";
	}
	response.sendRedirect(root);
%>
