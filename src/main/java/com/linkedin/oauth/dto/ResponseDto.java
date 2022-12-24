package com.linkedin.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ResponseDto {

    @JsonProperty("elements")
    private List<PostStoryResponseDto> element;

	public List<PostStoryResponseDto> getElement() {
		return element;
	}

	public void setElement(List<PostStoryResponseDto> element) {
		this.element = element;
	}
    
    
}
