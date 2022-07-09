package com.kk.bean;

import lombok.Data;

/**
 * @Description:
 * @author: Spike
 * @Created: 2022/5/15
 */
@Data
public class RequestLog {

    private String trackingId;

    private String ip;

    private String header;

    private String uri;

    private String requestParams;

    private String response;

    private Integer respCode;

    private Long costTime;
}
