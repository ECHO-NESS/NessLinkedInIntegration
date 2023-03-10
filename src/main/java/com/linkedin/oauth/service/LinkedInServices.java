package com.linkedin.oauth.service;

import com.linkedin.oauth.dto.ResponseDto;
import com.linkedin.oauth.pojo.LinkedInMasterModel;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface LinkedInServices {

    List<LinkedInMasterModel> getAllStories();
    
    ResponseEntity<ResponseDto> makeGetCall(Class<ResponseDto> responseDto);
    
    void saveLinkedInStories();
}
