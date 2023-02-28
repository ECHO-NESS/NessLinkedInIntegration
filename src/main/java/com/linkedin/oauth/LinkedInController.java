package com.linkedin.oauth;

import java.util.List;

import com.linkedin.oauth.dto.ActionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.linkedin.oauth.dto.ResponseDto;
import com.linkedin.oauth.pojo.LinkedInMasterModel;
import com.linkedin.oauth.service.LinkedInServices;

@RestController
@RequestMapping(value = "/linkedIn")
public class LinkedInController {

    @Autowired
    LinkedInServices linkedInServices;

    @GetMapping(value = "/posts")
    @ResponseBody // take from db
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

    @PostMapping(value = "/rePost")
    public ResponseEntity<String> rePost(@RequestBody(required = true) ActionRequest shareRequest) {

        linkedInServices.repost(shareRequest);

        return ResponseEntity.ok(HttpStatus.OK.toString());
    }
    
}
