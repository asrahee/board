package net.spring.example.board.controller;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.spring.example.board.service.BoardService;
import net.spring.example.board.vo.Article;
import net.spring.example.board.vo.AttachFile;
import net.spring.example.board.vo.Comment;
import net.spring.example.commons.PagingHelper;
import net.spring.example.commons.WebConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
 
@Controller
// /bbs 를 포함하는 모든 요청을 담당
@RequestMapping("/bbs")
public class BbsController {
    // @Autowired 를 종속변수에 적용하면 setter가 없어도 종속객체가 주입
    @Autowired
    private BoardService boardService;
     
    // 이 메소드는 GET 방식과 POST 방식의 /bbs/list 요청때 호출
    @RequestMapping(value="/list", method={RequestMethod.GET, RequestMethod.POST})
    public String list(
            String boardCd, 
            Integer curPage, 
            String searchWord, 
            Model model) throws Exception {
             
    	// 초기값 설정
        if (boardCd == null) boardCd = "free";
        if (curPage == null) curPage = 1;
        if (searchWord == null) searchWord = "";
 
        // 한 페이지당 보여줄 게시물 건수
        int numPerPage = 10;
        // 한 블럭당 보여줄 페이지 수
        int pagePerBlock = 10;
         
        // 전체 게시물 건수 조회
        int totalRecord = boardService.getTotalRecord(boardCd, searchWord);
         
        PagingHelper pagingHelper = new PagingHelper(totalRecord, curPage, numPerPage, pagePerBlock);
         
        boardService.setPagingHelper(pagingHelper);
        int start = pagingHelper.getStartRecord();
        int end = pagingHelper.getEndRecord();
 
        ArrayList<Article> list = boardService.getArticleList(boardCd, searchWord, start, end);
        String boardNm = boardService.getBoardNm(boardCd);
        Integer no = boardService.getListNo();
        Integer prevLink = boardService.getPrevLink();
        Integer nextLink = boardService.getNextLink();
        Integer firstPage = boardService.getFirstPage();
        Integer lastPage = boardService.getLastPage();
        int[] pageLinks = boardService.getPageLinks();
         
        model.addAttribute("list", list);
        model.addAttribute("boardNm", boardNm);
        model.addAttribute("no", no);
        model.addAttribute("prevLink", prevLink);
        model.addAttribute("nextLink", nextLink);
        model.addAttribute("firstPage", firstPage);
        model.addAttribute("lastPage", lastPage);
        model.addAttribute("pageLinks", pageLinks);
        model.addAttribute("curPage", curPage);//curPage는 null 값이면 1로 만들어야 하므로
        model.addAttribute("boardCd", boardCd);//boardCd는 null 값이면 free로 만들어야 하므로
         
        return "bbs/list";
    }
    
    // 글쓰기(입력폼으로 전환됨)
    @RequestMapping(value="/write", method=RequestMethod.GET)
    public String write(String boardCd, Model model) throws Exception {
    	// 게시판 이름
    	String boardNm = boardService.getBoardNm(boardCd);
    	model.addAttribute("boardNm", boardNm);
    	
    	return "bbs/writeform";
    }
    
    // 글쓰기(새 글을 추가하고 리스트 화면으로 이동)
    @RequestMapping(value="/write", method=RequestMethod.POST)
    public String write(Article article){
    	// TODO 회원모듈 구현 전으로 임시 이메일을 설정
    	article.setEmail("yjson@esumtech.com");
    	// 새글 추가
    	boardService.insert(article);
    	return "redirect:/bbs/list?boardCd=" + article.getBoardCd();
    }
    
    // 글 수정
    @RequestMapping(value="/modify", method=RequestMethod.GET)
    public String modify(
    		Integer articleNo,
    		String boardCd,
    		Model model) throws Exception {
    	Article thisArticle = boardService.getArticle(articleNo);
    	String boardNm = boardService.getBoardNm(boardCd);
    	
    	// 수정페이지에서 보일 게시글 정보
    	model.addAttribute("thisArticle", thisArticle);
    	model.addAttribute("boardNm",  boardNm);
    	
    	return "bbs/modifyform";
    }
    
    // 글 수정(내용을 수정하고 리스트 화면으로 이동)
    @RequestMapping(value="/modify", method=RequestMethod.POST)
    public String modify(HttpServletRequest request) throws Exception {
    	
    	// MultipartHttpServletRequest 사용을 위해서는 HttpServletRequest 로 인자를 받고
    	// HttpServletRequest 객체를 casting 하여 사용하여야 한다. 
    	MultipartHttpServletRequest mpRequest = (MultipartHttpServletRequest) request;
    	
    	int articleNo = Integer.parseInt(mpRequest.getParameter("articleNo"));
    	String boardCd = mpRequest.getParameter("boardCd");
    	int curPage = Integer.parseInt(mpRequest.getParameter("curPage"));
    	String searchWord = mpRequest.getParameter("searchWord");
    	
    	String title = mpRequest.getParameter("title");
    	String content = mpRequest.getParameter("content");
    	
    	// 게시글 수정
    	Article article = new Article();
    	article.setEmail("yjson@esumtech.com");		// 임시 설정
    	article.setTitle(title);
    	article.setContent(content);
    	article.setBoardCd(boardCd);
    	article.setArticleNo(articleNo);
    	boardService.update(article);
    	
    	// 파일 업로드
    	Iterator<String> it = mpRequest.getFileNames();
    	List<MultipartFile> fileList = new ArrayList<MultipartFile>();
    	while(it.hasNext()) {
    		MultipartFile multiFile = mpRequest.getFile((String)it.next());
    		
    		if(multiFile.getSize() > 0){
    			String filename = multiFile.getOriginalFilename();
    			multiFile.transferTo(new File(WebConstants.BASE_PATH + filename));
    			fileList.add(multiFile);
    		}
    	}
    	
    	// 파일 데이터 삽입
    	int size = fileList.size();
    	for(int i = 0 ; i < size ; i++){
    		MultipartFile mpFile = fileList.get(i);
    		AttachFile attachFile = new AttachFile();
    		String filename = mpFile.getOriginalFilename();
    		attachFile.setFilename(filename);
    		attachFile.setFiletype(mpFile.getContentType());
    		attachFile.setFilesize(mpFile.getSize());
    		attachFile.setArticleNo(articleNo);
    		boardService.insertAttachFile(attachFile);
    	}
    	
    	searchWord = URLEncoder.encode(searchWord, "UTF-8");
    	return "redirect:/bbs/view?articleNo=" + articleNo
    			+ "&boardCd=" + boardCd 
    			+ "&curPage=" + curPage
    			+ "&searchWord=" + searchWord;
    	
    }
    
    // 글 삭제
    @RequestMapping(value="/delete", method=RequestMethod.POST)
    public String delete(
    		Integer articleNo,
    		String boardCd,
    		Integer curPage,
    		String searchWord) throws Exception {
    	
    	boardService.delete(articleNo);
    	searchWord = URLEncoder.encode(searchWord, "UTF-8");
    	
    	return "redirect:/bbs/list?boardCd=" + boardCd +
    			"&curPage=" + curPage +
    			"&searchWord=" + searchWord;
    }
    
    // 상세보기(리스트 특정 항목을 선택하여 상세 내용 보기
    @RequestMapping(value="/view", method=RequestMethod.GET)
    public String view(
    		Integer articleNo,
    		String boardCd,
    		Integer curPage,
    		String searchWord,
    		Model model) throws Exception{
    	if(searchWord == null){
    		searchWord="";
    	}
    	
    	int numPerPage = 10;
    	int pagePerBlock = 10;
    	
    	// 조회수 증가
    	boardService.increaseHit(articleNo);
    	
    	// 상세 보기
    	Article thisArticle = boardService.getArticle(articleNo);
    	ArrayList<AttachFile> attachFileList = boardService.getAttachFileList(articleNo);
    	ArrayList<Comment> commentList = boardService.getCommentList(articleNo);
    	Article nextArticle = boardService.getNextArticle(articleNo, boardCd, searchWord);
    	Article prevArticle = boardService.getPrevArticle(articleNo, boardCd, searchWord);
    	
    	// 목록 보기
    	int totalRecord = boardService.getTotalRecord(boardCd, searchWord);
    	PagingHelper pagingHelper = new PagingHelper(totalRecord, curPage, numPerPage, pagePerBlock);
    	
    	boardService.setPagingHelper(pagingHelper);
    	int start = pagingHelper.getStartRecord();
    	int end = pagingHelper.getEndRecord();
    	
    	ArrayList<Article> list = boardService.getArticleList(boardCd, searchWord, start, end);
    	String boardNm = boardService.getBoardNm(boardCd);
    	Integer no = boardService.getListNo();
    	Integer prevLink = boardService.getPrevLink();
        Integer nextLink = boardService.getNextLink();
        Integer firstPage = boardService.getFirstPage();
        Integer lastPage = boardService.getLastPage();
        int[] pageLinks = boardService.getPageLinks();
        
        model.addAttribute("thisArticle", thisArticle);
        model.addAttribute("attachFileList", attachFileList);
        model.addAttribute("commentList", commentList);
        model.addAttribute("nextArticle", nextArticle);
        model.addAttribute("prevArticle", prevArticle);
        
     // 목록을 위한 데이터
        model.addAttribute("list", list);
        model.addAttribute("boardNm", boardNm);
        model.addAttribute("no", no);
        model.addAttribute("prevLink", prevLink);
        model.addAttribute("nextLink", nextLink);
        model.addAttribute("firstPage", firstPage);
        model.addAttribute("lastPage", lastPage);
        model.addAttribute("pageLinks", pageLinks);
         
        return "bbs/view";
    }
    
    // 덧글쓰기
    @RequestMapping(value="/commentAdd", method=RequestMethod.POST)
    public String commentAdd(
    		Integer articleNo,
    		String boardCd,
    		Integer curPage,
    		String searchWord,
    		String memo) throws Exception {
    	
    	Comment comment = new Comment();
    	comment.setMemo(memo);
    	comment.setEmail("yjson@esumtech.com");
    	comment.setArticleNo(articleNo);
    	boardService.insertComment(comment);
    	
    	searchWord = URLEncoder.encode(searchWord, "UTF-8");
    	
    	return "redirect:/bbs/view?articleNo=" + articleNo + 
    			"&boardCd=" + boardCd +
    			"&curPage=" + curPage +
    			"&searchWord=" + searchWord;
    }
    
   // 덧글수정
    @RequestMapping(value="/commentUpdate", method=RequestMethod.POST)
    public String commentUpdate(
    		Integer commentNo,
    		Integer articleNo,
    		String boardCd,
    		Integer curPage,
    		String searchWord,
    		String memo) throws Exception{
    	
    	Comment comment = boardService.getComment(commentNo);
    	comment.setMemo(memo);
    	boardService.updateComment(comment);
    	searchWord = URLEncoder.encode(searchWord, "UTF-8");
    	
    	return "redirect:/bbs/view?articleNo=" + articleNo +
    			"&boardCd=" + boardCd +
    			"&curPage=" + curPage + 
    			"&searchWord" + searchWord;
    }
    
    // 덧글 삭제
    @RequestMapping(value="/commentDel", method=RequestMethod.POST)
    public String commentDel(Integer commentNo,
    		Integer articleNo,
    		String boardCd,
    		Integer curPage,
    		String searchWord) throws Exception {
    	boardService.deleteComment(commentNo);
    	searchWord = URLEncoder.encode(searchWord, "UTF-8");
    	
    	return "redirect:/bbs/view?articleNo=" + articleNo +
    			"&boardCd=" + boardCd + 
    			"&curPage=" + curPage +
    			"&searchWord=" + searchWord;
    }
    
    // 파일 다운로드
    @RequestMapping(value="/download", method=RequestMethod.POST)
    public String download(String filename, Model model){
    	model.addAttribute("filename", filename);
    	return "inc/download";
    }
    
    // 첨부파일 삭제
    @RequestMapping(value="/attachFileDel", method=RequestMethod.POST)
    public String attachFileDel(
    		Integer attachFileNo,
    		Integer articleNo,
    		String boardCd,
    		Integer curPage,
    		String searchWord,
    		Model model) throws Exception {
    	
    	boardService.deleteFile(attachFileNo);
    	searchWord = URLEncoder.encode(searchWord, "UTF-8");
    	
    	return "redirect:/bbs/view?articleNo=" + articleNo +
    			"&boardCd=" + boardCd +
    			"&curPage=" + curPage +
    			"&searchWord=" + searchWord;
    }
    		
}