package net.spring.example.mybatis;

import java.util.ArrayList;
import java.util.HashMap;

import net.spring.example.board.vo.Article;
import net.spring.example.board.vo.AttachFile;
import net.spring.example.board.vo.Board;
import net.spring.example.board.vo.Comment;
import net.spring.example.board.vo.Reply;

public interface BoardMapper {
	/*
	 * 게시판 목록
	 */
	public ArrayList<Article> getArticleList(HashMap<String, String> hashmap);	
	
	/*
	 * 게시판의 총 게시물 갯수 구하기
	 */
	public int getTotalRecord(HashMap<String, String> hashmap);
	
	/* 
	 * 새로운 게시글 추가
	 */
	public int insert(Article article);	
	
	/*
	 * 첨부파일
	 */
	public AttachFile getAttachFile(int attachFileNo);

	/*
	 * 첨부파일 추가 
	 */
	public void insertAttachFile(AttachFile attachFile);
	
	/*
	 * 게시글 수정
	 */
	public void update(Article article);	
	
	/* 
	 * 게시글 삭제
	 */
	public void delete(int articleNo);
	
	/*
	 * 조회수 증가
	 */
	public void increaseHit(int articleNo);	
	
	/*
	 * 게시글 상세보기
	 */
	public Article getArticle(int articleNo);
	
	/*
	 * 다음글 보기 articleNo,boardCd,searchWord->HahMap 에 담는다
	 */
	public Article getNextArticle(HashMap<String, String> hashmap); 
	
	/*
	 * 이전글 보기 articleNo,boardCd,searchWord->HahMap 에 담는다
	 */
	public Article getPrevArticle(HashMap<String, String> hashmap);
	
	/*
	 * 게시글의 첨부파일 리스트
	 */
	public ArrayList<AttachFile> getAttachFileList(int articleNo);	
	
	/*
	 * 첨부파일 삭제
	 */
	public void deleteFile(int attachFileNo);	
	
	/*
	 * 게시판이름 구하기
	 */
	public String getBoardNm(String boardCd);
	
	/*
	 * 게시판종류 리스트 구하기
	 */
	public ArrayList<Board> getBoardList();
	
	/*
	 * 덧글쓰기
	 */
	public void insertComment(Comment comment);	
	
	/*
	 * 덧글수정
	 */
	public void updateComment(Comment comment);
	
	/*
	 * 덧글삭제
	 */
	public void deleteComment(int commentNo);
	
	/*
	 * 덧글 가져오기
	 */
	public Comment getComment(int commentNo);
	
	/*
	 * 게시글의 덧글리스트 구하기
	 */
	public ArrayList<Comment> getCommentList(int articleNo);
	
	/*
	 * 답글쓰기
	 */
	public void insertReply(Reply reply);
	
	/*
	 * 답글수정
	 */
	public void updateReply(Reply reply);
	
	/*
	 * 답글삭제
	 */
	public void deleteReply(int replyNo);
	
	/*
	 * 답글가져오기
	 */
	public Comment getReply(int replyNo);
	
	/*
	 * 덧글의 답글리스트 구하기
	 */
	public ArrayList<Reply> getReplyList(int articleNo);

}
