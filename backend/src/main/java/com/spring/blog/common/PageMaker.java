package com.spring.blog.common;

public class PageMaker {

    private int startPage;
    private int endPage;
    private boolean prev;
    private boolean next;

    private PageMaker() {
    }

    /*
    JPA 쿼리의 경우 Page Index가 0부터 시작하지만 페이지 계산시에는 1부터 들어간다.
    클라이언트가 page=0으로 요청을 보내면 0으로 쿼리를 날리고,
    페이지 계산시에만 1로 치환한다.
     */
    public PageMaker(int page, int size, int pageBlockCounts, int totalItemCounts) {
        endPage = (int) (Math.ceil((page + 1) / (double) pageBlockCounts) * pageBlockCounts);
        startPage = (endPage - pageBlockCounts) + 1;

        int tempEndPage = (int) (Math.ceil(totalItemCounts / (double) size));

        if (endPage > tempEndPage) {
            endPage = tempEndPage;
        }

        prev = startPage != 1;
        next = endPage * size < totalItemCounts;
    }

    public int getStartPage() {
        return startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public boolean isPrev() {
        return prev;
    }

    public boolean isNext() {
        return next;
    }
}
