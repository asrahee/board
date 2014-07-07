package net.spring.example.commons;

public class PagingHelper {
	private int firstPage;		// 첫번째 페이지 번호
	private int lastPage; 		// 마지막 페이지 번호
	private int prevLink; 		// [이전] 링크 
	private int nextLink; 		// [다음] 링크
	private int startRecord; 	// 목록을 구할때 쓰이는 ROWNUM 시작
	private int endRecord; 	// 목록을 구할때 쓰이는 ROWNUM 끝
	private int listNo; 		// 목록에서 위에서 순서대로 붙여지는 번호
	private int[] pageLinks; 	// 첫번쩨 페이지 번호부터 시작하여 1씩 증가하여 마지막 페이지번호까지 int[] 배열
	
	/**
	 * 파라미터
	 * totalRecord		: 조회된 전체 레코드 수
	 * numPerPage		: 한 페이지당 보여줄 레코드 수
	 * curPage			: 현재 페이지 번호
	 * pagePerBlock		: 한 블럭당 보여질 페이지 수
	 * 
	 * 메소드 변수
	 * totalPage 		: 전체 페이지 수
	 * totalBlock		: 전체 블럭 수
	 * block			: 현재 블럭 
	 * firstPage		: 현재 블럭의 첫번째 페이지 번호
	 * lastPage			: 현재 블럭의 마지막 페이지 번호
	 */
	public PagingHelper(int totalRecord, int curPage, int numPerPage, int pagePerBlock) {
		int totalPage = ((totalRecord % numPerPage) == 0) ? 
			totalRecord / numPerPage : totalRecord / numPerPage + 1;	// 0으로 나누어 떨어지는 경우 나눈 몫, 아닌 경우 페이지수 +1 증가
		int totalBlock = ((totalPage % pagePerBlock) == 0) ? 
			totalPage / pagePerBlock : totalPage / pagePerBlock + 1;	// 상동
		int block = ((curPage % pagePerBlock) == 0) ? 
			curPage / pagePerBlock : curPage / pagePerBlock + 1;		// 상동
		this.firstPage = (block - 1) * pagePerBlock + 1;
		this.lastPage = block * pagePerBlock;
		if (block >= totalBlock) {										// 마지막 페이지는 따로 설정
			this.lastPage = totalPage;
		}
		pageLinks = makeArray(firstPage, lastPage);
		
		if (block > 1) {
			this.prevLink = firstPage - 1;
		}
		if (block < totalBlock) {
			this.nextLink = lastPage + 1;
		}
		this.listNo = totalRecord - (curPage - 1) * numPerPage;
		this.startRecord = (curPage - 1) * numPerPage + 1;
		this.endRecord = startRecord + numPerPage - 1;
	}
	
	private int[] makeArray(int first, int last) {
		int size = last - first + 1;
		int[] ret = new int[size]; 
		for (int i = 0; i < size; i++) {
			ret[i] = first++;
		}
		
		return ret;
	}

	public int getFirstPage() {
		return firstPage;
	}

	public int getLastPage() {
		return lastPage;
	}

	public int getPrevLink() {
		return prevLink;
	}

	public int getNextLink() {
		return nextLink;
	}

	public int getStartRecord() {
		return startRecord;
	}

	public int getEndRecord() {
		return endRecord;
	}

	public int getListNo() {
		return listNo;
	}

	public int[] getPageLinks() {
		return pageLinks;
	}
}
