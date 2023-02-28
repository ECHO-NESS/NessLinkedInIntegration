package com.linkedin.oauth.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.List;


@Getter
@Setter
@Data
public class ActionRequest {
    private String personId;

    private List<String> sharePostsIds;



}
