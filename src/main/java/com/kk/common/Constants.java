package com.kk.common;


public interface Constants {

    Integer SUCCESS = 0;
    Integer FAIL = 1;
    String COMMA = ",";
    String UNDER_LINE = "_";
    String YES = "Y";
    String NO = "N";

    int OKHTTP_CONNECT_TIMEOUT = 5;
    int OKHTTP_READ_TIMEOUT = 10;

    interface Page {
        String REQ_PARAM_PAGE_NO = "queryPageNo";
        String REQ_PARAM_PAGE_SIZE = "queryPageSize";
        int MAX_PAGE_SIZE = 20;
        // default page Size is 10
        int DEFAULT_SIZE = 10;
        //default current page is 1
        int DEFAULT_CURRENT_PAGE = 1;
    }
}
