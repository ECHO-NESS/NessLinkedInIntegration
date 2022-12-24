package com.linkedin.oauth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.linkedin.oauth.dto.ResponseDto;
import com.linkedin.oauth.pojo.LinkedInMasterModel;
import com.linkedin.oauth.service.LinkedInServices;

@RestController
@RequestMapping(value = "/linkedIn")
public class LinkedInController {

    @Autowired
    LinkedInServices linkedInServices;

    @GetMapping(value = "/posts")
    @ResponseBody
    public List<LinkedInMasterModel> getAllStories(){
        return linkedInServices.getAllStories();
    }

    
    @GetMapping(value = "/getPosts")
    @ResponseBody
    public ResponseEntity<ResponseDto> getPosts(){
        return linkedInServices.makeGetCall(ResponseDto.class);
    }
    
    
    
    @PostMapping(value = "/post")
    public void getPost(){
        linkedInServices.saveLinkedInStories();
    }  
    

    
}
