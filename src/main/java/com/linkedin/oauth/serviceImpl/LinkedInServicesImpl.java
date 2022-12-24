package com.linkedin.oauth.serviceImpl;

import static com.linkedin.oauth.util.LinkedInAPIConstants.AUTHOR;
import static com.linkedin.oauth.util.LinkedInAPIConstants.BASE_URL;
import static com.linkedin.oauth.util.LinkedInAPIConstants.Q;
import static com.linkedin.oauth.util.LinkedInAPIConstants.TOKEN;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.linkedin.oauth.dto.PostStoryResponseDto;
import com.linkedin.oauth.dto.ResponseDto;
import com.linkedin.oauth.pojo.LinkedInMasterModel;
import com.linkedin.oauth.repository.LinkedInRepo;
import com.linkedin.oauth.service.LinkedInServices;
import com.linkedin.oauth.util.DateUtil;

@Service
public class LinkedInServicesImpl implements LinkedInServices {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInServicesImpl.class);

    @Autowired
    LinkedInRepo linkedInRepo;

    RestTemplate restTemplate = new RestTemplate();

    @Scheduled(cron = "0 0 10 ? * *")
    public void runScheduler(){
        logger.info("Scheduler started to fetching the data into Master table");
        ResponseEntity<ResponseDto> response  =  makeGetCall(ResponseDto.class);
        logger.info("Save all data into Master table");
        List<LinkedInMasterModel> linkedInMasterModels = importDto(response.getBody());
        Collections.reverse(linkedInMasterModels);

        List<LinkedInMasterModel> latestResponse = getAllStories();
        if(latestResponse.size()==0){
            logger.info("Count : "+linkedInMasterModels.size());
            linkedInRepo.saveAll(linkedInMasterModels);
        }else{

            Collections.sort(latestResponse, new Comparator<LinkedInMasterModel>() {
                @Override
                public int compare(LinkedInMasterModel o1, LinkedInMasterModel o2) {
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                }
            });


            System.out.println(latestResponse);
            LinkedInMasterModel latestRecord = latestResponse.get(0);
            List<LinkedInMasterModel> newStories = importDto(response.getBody());
            List<LinkedInMasterModel> newStoriesTobeSaved = new ArrayList<>();
            for(LinkedInMasterModel linkedInMasterModel: newStories){
                if(linkedInMasterModel.getCreatedAt().compareTo(latestRecord.getCreatedAt())>0){
                    newStoriesTobeSaved.add(linkedInMasterModel);
                }
            }
            logger.info("Count : "+newStoriesTobeSaved.size());
            linkedInRepo.saveAll(newStoriesTobeSaved);


        }

    }

    public ResponseEntity<ResponseDto> makeGetCall(Class<ResponseDto> responseDto) {
        try {
             URI uri = new URI(BASE_URL+"author="+AUTHOR+"&q="+Q+"&count=10&start=0");
             HttpEntity<String> entity = generateHttpEntity();
            ResponseEntity<ResponseDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ResponseDto.class);
            logger.info("Get Linked In Stories API response: {}", response);
            return response;
        } catch (URISyntaxException e) {
            logger.error("Error while fetching Linked Stories from Master Table: {} : ", e.getMessage());
            throw new RuntimeException();
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                // 404 = Patient not found, proceed without any errors
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }
        return null;
    }

    protected HttpEntity<String> generateHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("LinkedIn-Version","202209");
        headers.setBearerAuth(TOKEN);
        HttpEntity<String> header = new HttpEntity<String>(headers);
        return  header;
    }


    public void saveLinkedInStories(){
        logger.info("Scheduler started to fetching the data into Master table");
        List<LinkedInMasterModel> linkedInMasterModels = importDto(makeGetCall(ResponseDto.class).getBody());
        linkedInRepo.saveAll(linkedInMasterModels);
    }
    

 

    public  List<LinkedInMasterModel> getAllStories(){
        logger.info("Get API to fetch posted stories : ");
        List<LinkedInMasterModel> response =  linkedInRepo.findAll();
        return response;
    }

    private List<LinkedInMasterModel> importDto(ResponseDto responseDto){
        List<LinkedInMasterModel> linkedInMasterModels = new ArrayList<>();
        for(PostStoryResponseDto dto: responseDto.getElement()){
            LinkedInMasterModel linkedInMasterModel = new LinkedInMasterModel();
            if(Objects.nonNull(dto.getCreatedAt())){
                linkedInMasterModel.setCreatedAt(dto.getCreatedAt());
            }
            linkedInMasterModel.setLifecycleState(dto.getLifecycleState());
            if(Objects.nonNull(dto.getLastModifiedAt())){
                linkedInMasterModel.setLastModifiedAt(dto.getLastModifiedAt());
            }
            linkedInMasterModel.setVisibility(dto.getVisibility());
            if(Objects.nonNull(dto.getPublishedAt())){
                linkedInMasterModel.setPublishedAt(dto.getPublishedAt());
            }
            
            linkedInMasterModel.setAuthor(dto.getAuthor());
            linkedInMasterModel.setUid(dto.getUid());
            
            if(Objects.nonNull(dto.getContent())){
            	
            	 if(Objects.nonNull(dto.getContent().getMedia()) ){
            		 linkedInMasterModel.setContentMediaId(dto.getContent().getMedia().getId());
            	 }
                
                if(Objects.nonNull(dto.getContent().getMedia())){
                    linkedInMasterModel.setTitle(dto.getContent().getMedia().getTitle());
                }
            }

            if(Objects.nonNull(dto.getContent())){
            	if(Objects.nonNull(dto.getContent().getArticle())) {
            		 linkedInMasterModel.setDescription(dto.getContent().getArticle().getDescription());
                     linkedInMasterModel.setThumbnail(dto.getContent().getArticle().getThumbnail());
                     linkedInMasterModel.setSource(dto.getContent().getArticle().getSource());
                     linkedInMasterModel.setTitle(dto.getContent().getArticle().getTitle());
            	}
            }
            
            linkedInMasterModel.setCommentary(dto.getCommentary());
            if(Objects.nonNull(dto.getLifecycleStateInfoDto().getEditable())){
                linkedInMasterModel.setEditedByAuthor(DateUtil.convertStringToBoolean(dto.getLifecycleStateInfoDto().getEditable()));
            }

            linkedInMasterModels.add(linkedInMasterModel);
        }
        return linkedInMasterModels;
    }
}
