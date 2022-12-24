package com.linkedin.oauth.dto;

import com.linkedin.oauth.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;


@Getter
@Setter
@ToString
public class GetResponseDto {

    private int id;

    private String createdAt;

    private String lifecycleState;

    private String lastModifiedAt;

    private String visibility;

    private String publishedAt;

    private String author;

    private String uid;

    private String commentary;

    private String contentMediaId;

    private String description;

    private String thumbnail;

    private String source;

    private String title;

    private boolean isEditedByAuthor;

    public void setCreatedAt(Date createdAt){
        this.createdAt = DateUtil.convertDateToWelkinFormat(createdAt);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getLifecycleState() {
		return lifecycleState;
	}

	public void setLifecycleState(String lifecycleState) {
		this.lifecycleState = lifecycleState;
	}

	public String getLastModifiedAt() {
		return lastModifiedAt;
	}

	public void setLastModifiedAt(String lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(String publishedAt) {
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

	public String getCommentary() {
		return commentary;
	}

	public void setCommentary(String commentary) {
		this.commentary = commentary;
	}

	public String getContentMediaId() {
		return contentMediaId;
	}

	public void setContentMediaId(String contentMediaId) {
		this.contentMediaId = contentMediaId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isEditedByAuthor() {
		return isEditedByAuthor;
	}

	public void setEditedByAuthor(boolean isEditedByAuthor) {
		this.isEditedByAuthor = isEditedByAuthor;
	}

}
