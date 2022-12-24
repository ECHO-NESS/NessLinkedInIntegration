package com.linkedin.oauth.pojo;


import com.linkedin.oauth.util.DateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "master_data")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class LinkedInMasterModel {

	    @Id
		@GeneratedValue(strategy = GenerationType.AUTO)
	    private int id;

		@Column(name = "created_at")
		private String createdAt;

	    @Column(name = "lifecycle_status")
		private String lifecycleState;

		@Column(name = "last_modified_at")
		private String lastModifiedAt;

		@Column(name = "visibility")
		private String visibility;

		@Column(name = "published_at")
		private String publishedAt;

		@Column(name = "author")
		private String author;

		@Column(name = "u_id")
		private String uid;

		@Column(name = "commentary",length = 7000)
		private String commentary;

		@Column(name = "media_id")
		private String contentMediaId;

	  	@Column(name = "article_description", length = 3000)
		private String description;

		@Column(name = "article_thumbnail")
		private String thumbnail;

		@Column(name = "article_source", length = 3000)
		private String source;

		@Column(name = "article_title")
		private String title;

		@Column(name = "editable_by_author")
		private boolean isEditedByAuthor;

		public void setCreatedAt(Date date){
			this.createdAt = DateUtil.convertDateToWelkinFormat(date);
		}

		public void setPublishedAt(Date publishedAt){
			this.publishedAt = DateUtil.convertDateToWelkinFormat(publishedAt);
		}

		public void setLastModifiedAt(Date lastModifiedAt){
			this.lastModifiedAt = DateUtil.convertDateToWelkinFormat(lastModifiedAt);
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
