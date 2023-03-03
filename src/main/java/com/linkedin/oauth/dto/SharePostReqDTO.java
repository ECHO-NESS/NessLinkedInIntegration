package com.linkedin.oauth.dto;

import lombok.Data;

@Data
public class SharePostReqDTO {
    private String postId;

    private String title;
    private String comment;
}
