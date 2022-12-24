package com.linkedin.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Getter
@Setter
@ToString
public class PostStoryResponseDto {

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("lifecycleState")
    private String lifecycleState;

    @JsonProperty("lastModifiedAt")
    private Date lastModifiedAt;

    @JsonProperty("visibility")
    private String visibility;

    @JsonProperty("publishedAt")
    private Date publishedAt;

    @JsonProperty("author")
    private String author;

    @JsonProperty("id")
    private String uid;

    @JsonProperty("content")
    private PostContentDto content;

    @JsonProperty("commentary")
    private String commentary;

    @JsonProperty("lifecycleStateInfo")
    private LifecycleStateInfoDto lifecycleStateInfoDto;

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getLifecycleState() {
		return lifecycleState;
	}

	public void setLifecycleState(String lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Date getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(Date publishedAt) {
		this.publishedAt = publishedAt;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public PostContentDto getContent() {
		return content;
	}

	public void setContent(PostContentDto content) {
		this.content = content;
	}

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public LifecycleStateInfoDto getLifecycleStateInfoDto() {
		return lifecycleStateInfoDto;
	}

	public void setLifecycleStateInfoDto(LifecycleStateInfoDto lifecycleStateInfoDto) {
		this.lifecycleStateInfoDto = lifecycleStateInfoDto;
	}

}
