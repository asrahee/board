package net.spring.example.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import net.spring.example.board.service.BoardService;
import net.spring.example.board.vo.Article;
import net.spring.example.board.vo.AttachFile;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class FileUploadUtil {
	// 게시판 서비스
	private BoardService boardService;
	// 파일을 저장할 기본 경로
	String mSavePath = WebConstants.BASE_PATH;
	
	public FileUploadUtil() {
		super();
	}
	
	public FileUploadUtil(BoardService boardService) {
		this.boardService = boardService;
	}

	/**
	 * 파일 업로드 메인 함수, HttpServletRequest 파라미터로부터 MultipartRequest 객체를 생성하고
	 * 해당 객체의 정보들을 이용하여 DB 내용 update, 파일 업로드하고 결과를 리다이렉트할 URL(String 값)을
	 * 리턴한다.
	 * @param request
	 * @return 예외가 발생한 경우 null 값을 리턴하게 된다.
	 */
	public String fileUpload(HttpServletRequest request){
		HashMap<String, String> reqKeyValue = new HashMap<String, String>();
    	
    	try{
	    	request.setCharacterEncoding("UTF-8");
	    	// 파일이 저장되는 경로 설정(웹 컨텍스트 공통 사용 패스)
	    	String savePath = WebConstants.BASE_PATH;
	    	// 최대 업로드 파일 크기 5MB(메가)로 제한
	    	// TODO 2014.07.23 [[ 파일 업로드시 maxSize 가 넘는 크기를 업로드하는 경우의 예외처리 추가해야함
	    	int maxSize = WebConstants.MAX_FILE_UPLOAD_SIZE;
	    	// 해당 폴더가 없는 경우 새로 생성
			File dir = new File(savePath);
			if (!dir.exists()) {
				dir.mkdir();
			}
	
			MultipartRequest multi = new MultipartRequest(request, savePath, maxSize, new DefaultFileRenamePolicy());
			
			reqKeyValue = getArticleInfo(multi);
			
			int articleNo = Integer.parseInt(reqKeyValue.get("articleNo").toString());
			int curPage = Integer.parseInt(reqKeyValue.get("curPage").toString());
			String boardCd = reqKeyValue.get("boardCd").toString();
			String searchWord = reqKeyValue.get("searchWord").toString();
			String title = reqKeyValue.get("title").toString();
			String content = reqKeyValue.get("content").toString();
			// 게시글 수정
			update(articleNo, boardCd, title, content);			
			// 파일 업로드
			fielUpload(multi, articleNo);
			
			// 리다이렉트 할 view(String 값) 리턴
			return "redirect:/bbs/view?articleNo=" + articleNo
					+ "&boardCd=" + boardCd 
					+ "&curPage=" + curPage
					+ "&searchWord=" + searchWord;
		
    	} catch (Exception e){
    		return "redirect:/inc/error-page?error-message=" + e.getMessage();
    	}
	}
	
	/**
	 * 파일 업로드(수정) 화면 요청 정보로부터(enctype="multipart/form-data") DB 내용 수정에 사용할 
	 * 파라미터들을 추출하여 reqKeyValue 맵에 담아 리턴한다.
	 * @param multi
	 * @return
	 */
	private HashMap<String, String> getArticleInfo(MultipartRequest multi){
		// multipart request 에서 수정시 사용할 params를 저장
		HashMap<String, String> reqKeyValue = new HashMap<String, String>();
		// 파일이 아닌 폼필드에 입력되어 전달된 파라미터의 모든 이름과 값을 구해서 출력한다.
		@SuppressWarnings("rawtypes")
		Enumeration params = multi.getParameterNames();

		try {
			// 폼필드에 입력된 내용을 읽어온다(파일을 제외한 폼필드의 내용)
			while (params.hasMoreElements()) {
				String key = (String) params.nextElement(); 				// 폼의 텍스트박스 이름을 하나 얻는다.
				String value = multi.getParameter(key); 					//
				value = new String(value.getBytes("8859_1"), "UTF-8");
				reqKeyValue.put(key, value);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return reqKeyValue;
	}
	
	/**
	 * 폼으로부터 입력받은 정보로 DB 내용을 update 한다.
	 * @param articleNo
	 * @param boardCd
	 * @param title
	 * @param content
	 */
	private void update(int articleNo, String boardCd, String title, String content){
		
		Article article = new Article();
		
		// 게시글 수정
		article.setEmail("yjson@esumtech.com");								// 임시 설정
		article.setTitle(title);
		article.setContent(content);
		article.setBoardCd(boardCd);
		article.setArticleNo(articleNo);
		
		boardService.update(article);
	}
	
	/**
	 * HttpServletRequest 정보로부터 MultipartRequest 객체를 생성하여, 필요한 정보들을
	 * 가지고 실제 파일을 지정된 경로에 업로드한다.
	 * @param multi
	 * @param articleNo
	 */
	private void fielUpload(MultipartRequest multi, int articleNo) {
		try {
			Enumeration files = multi.getFileNames();
			while (files.hasMoreElements()) {
				String name = (String) files.nextElement();
				String fileName = multi.getFilesystemName(name); 			// 실제 업로드된 파일명
				AttachFile attachFile = new AttachFile();

				// 한글 파일명이 깨진 것을 수정
				File oldFile = new File(mSavePath + fileName);
				fileName = new String(fileName.getBytes("8859_1"), "UTF-8");
				File newFile = new File(mSavePath + fileName);

				if (!oldFile.renameTo(newFile)) {
					byte[] buf = new byte[1024];
					FileInputStream fin = new FileInputStream(oldFile);
					FileOutputStream fos = new FileOutputStream(newFile);
					int read = 0;
					while ((read = fin.read(buf, 0, buf.length)) != -1) {
						fos.write(buf, 0, read);
					}
					fin.close();
					fos.close();
					oldFile.delete();
				}
				// 파일 데이터 삽입
				attachFile.setFilename(fileName);
				attachFile.setFiletype(multi.getContentType(fileName));
				attachFile.setFilesize(oldFile.length());
				attachFile.setArticleNo(articleNo);
				boardService.insertAttachFile(attachFile);
			}
		} catch (Exception e) {

		}
	}
}
