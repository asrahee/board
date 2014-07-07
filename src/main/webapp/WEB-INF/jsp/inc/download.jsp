<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.io.File" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="java.io.IOException" %>
<%@ page import="org.springframework.util.FileCopyUtils" %>
<%@ page import="net.spring.example.commons.WebConstants" %>
<%
	//request.setCharacterEncoding("UTF-8");//필터가 이 작업을 대신 해준다.
	String filename = request.getParameter("filename");

	File file = new File(WebConstants.BASE_PATH + filename);
	
	String filetype = filename.substring(filename.indexOf(".") + 1, filename.length());

	// 텍스트 파일인지 Data 파일인지 구분 
	if (filetype.trim().equalsIgnoreCase("txt")) {
		response.setContentType("text/plain");
	} else {
		response.setContentType("application/octet-stream");
	}
	// 파일 크기 설정
	response.setContentLength((int) file.length());

	// IE인 경우 파일명 인코딩 설정
	boolean ie = request.getHeader("User-Agent").indexOf("MSIE") != -1;
	if (ie) {
		filename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", " ");
	// IE 이외의 브라우저인 경우
	} else {
		filename = new String(filename.getBytes("UTF-8"), "8859_1");
	}
	// 파일 헤더에 Content-Disposition 파라미터에 attachment; filename='파일명' 값 설정 
	
	response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
	
	OutputStream outputStream = response.getOutputStream();
	FileInputStream fis = null;

	try {
		fis = new FileInputStream(file);
		FileCopyUtils.copy(fis, outputStream);
	} finally {
		if (fis != null) {
			try {
				fis.close();
			} catch (IOException e) {}
		}
	}
	
	out.flush();
%>

<!-- 참고 -->
<!-- Content-Disposition
Content-Disposition 은 컨텐트 타입의 옵션이기도 하며 실제로 지정된 파일명을 지정함으로써 더 자세한 파일의 속성을 알려줄 수 있다.

"Content-disposition: inline"은
브라우저 인식 파일확장자를 가진 파일들에 대해서는 웹브라우저 상에서 바로 파일을 자동으로 보여줄 수 있어서 의미상인 멀티파트 
메시지를 표현하는데 있다. 그외의 파일들에 대해서는 "파일 다운로드" 대화상자가 뜨도록 하는 헤더속성이다.

"Content-disposition: attachment"은
브라우저 인식 파일확장자를 포함하여 모든 확장자의 파일들에 대해,  
다운로드시 무조건 "파일 다운로드" 대화상자가 뜨도록 하는 헤더속성이라 할 수 있다.

Content-Disposition 스펙 정보
disposition := "Content-Disposition" ":"
                    disposition-type
                    *(";" disposition-parm)

     disposition-type := "inline"
                       / "attachment"
                       / extension-token
                       ; values are not case-sensitive

     disposition-parm := filename-parm
                       / creation-date-parm
                       / modification-date-parm
                       / read-date-parm
                       / size-parm
                       / parameter

     filename-parm := "filename" "=" value

     creation-date-parm := "creation-date" "=" quoted-date-time

     modification-date-parm := "modification-date" "=" quoted-date-time

     read-date-parm := "read-date" "=" quoted-date-time

     size-parm := "size" "=" 1*DIGIT

     quoted-date-time := quoted-string
                      ; contents MUST be an RFC 822 `date-time'
                      ; numeric timezones (+HHMM or -HHMM) MUST be used
 -->