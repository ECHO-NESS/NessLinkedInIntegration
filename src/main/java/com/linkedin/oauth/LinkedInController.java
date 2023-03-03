package com.linkedin.oauth;

import java.util.List;

import com.linkedin.oauth.dto.ActionRequest;
import com.linkedin.oauth.dto.PostActionDTO;
import com.linkedin.oauth.dto.Response;
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

    @PostMapping(value = "/postAction")
    public ResponseEntity<Object> postAction(@RequestBody(required = true) ActionRequest shareRequest)  {
        Response responseDto = new Response();
        try {
            PostActionDTO response = linkedInServices.postAction(shareRequest);
            responseDto.setMessage(response.getMessage());
            responseDto.setStatusCode(HttpStatus.OK.value());
            responseDto.setStatus("SUCCESS");
            return new ResponseEntity<>(responseDto,HttpStatus.OK);
        }catch (Exception e){
            responseDto.setMessage("Internal Server Error, Please try again");
            responseDto.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            responseDto.setStatus("ERROR");
            return new ResponseEntity<>(responseDto,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
