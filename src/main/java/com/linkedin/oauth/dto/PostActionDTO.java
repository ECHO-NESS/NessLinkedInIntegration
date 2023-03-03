package com.linkedin.oauth.dto;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostActionDTO {

    private List<String> likedPosts;

    private List<String> failedLikePosts;

    private List<String> sharedPosts;

    private List<String> failedSharePosts;

    private String message;


    public void addLikedPosts(String post) {
        if (ObjectUtils.isEmpty(this.likedPosts))
            this.likedPosts = new ArrayList<>();
        this.likedPosts.add(post);
    }

    public void addFailedLikePosts(String post) {
        if (ObjectUtils.isEmpty(this.failedLikePosts))
            this.failedLikePosts = new ArrayList<>();
        this.failedLikePosts.add(post);
    }

    public void addSharePosts(String post) {
        if (ObjectUtils.isEmpty(this.sharedPosts))
            this.sharedPosts = new ArrayList<>();
        this.sharedPosts.add(post);
    }

    public void addFailedSharePosts(String post) {
        if (ObjectUtils.isEmpty(this.failedSharePosts))
            this.failedSharePosts = new ArrayList<>();
        this.failedSharePosts.add(post);
    }


}
