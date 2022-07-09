package com.kk.bean;


import com.kk.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: Spike
 * @Created: 2022/5/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean {
    private Integer currentPage;
    private Integer pageSize;

    public void preparePage() {
        //judge if null or <1, then init value to be 1
        if (currentPage == null || currentPage < 1) {
            currentPage = 1;
        }
        //judge if null or <1,then init value to be 10, exceed then to be 20
        if (pageSize == null || pageSize < 1) {
            pageSize = Constants.Page.DEFAULT_SIZE;
        } else if (pageSize > Constants.Page.MAX_PAGE_SIZE) {
            pageSize = Constants.Page.MAX_PAGE_SIZE;
        }
    }
}
