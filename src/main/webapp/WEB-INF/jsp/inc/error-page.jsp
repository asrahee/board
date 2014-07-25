<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<%
	String errMessage = request.getParameter("error-message");
%>
	<body>
		<div id="url-navi"><a href="../bbs/list">BBS</a></div></br></br>
		에러표시 페이지 </br/>
		<%=errMessage %>
	</body>
</html>
