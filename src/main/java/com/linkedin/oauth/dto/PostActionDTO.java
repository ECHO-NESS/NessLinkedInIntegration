package com.linkedin.oauth.dto;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostActionDTO {

    private List<String> likePostIds;

    private List<String> failedLikePostIds;

    private List<String> sharePostIds;

    private List<String> failedSharePostIds;

    private String message;


    public void addLikePostIds(String postId) {
        if (ObjectUtils.isEmpty(this.likePostIds))
            this.likePostIds = new ArrayList<>();
        this.likePostIds.add(postId);
    }

    public void addFailedLikePostIds(String postId) {
        if (ObjectUtils.isEmpty(this.failedLikePostIds))
            this.failedLikePostIds = new ArrayList<>();
        this.failedLikePostIds.add(postId);
    }

    public void addSharePostIds(String postId) {
        if (ObjectUtils.isEmpty(this.sharePostIds))
            this.sharePostIds = new ArrayList<>();
        this.sharePostIds.add(postId);
    }

    public void addFailedSharePostIds(String postId) {
        if (ObjectUtils.isEmpty(this.failedSharePostIds))
            this.failedSharePostIds = new ArrayList<>();
        this.failedSharePostIds.add(postId);
    }


}
