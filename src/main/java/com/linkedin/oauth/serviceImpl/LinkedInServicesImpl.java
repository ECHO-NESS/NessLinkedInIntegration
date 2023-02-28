package com.linkedin.oauth.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linkedin.oauth.dto.PostStoryResponseDto;
import com.linkedin.oauth.dto.ResponseDto;
import com.linkedin.oauth.dto.TokenIntrospectionResponseDTO;
import com.linkedin.oauth.pojo.AccessToken;
import com.linkedin.oauth.pojo.LinkedInAuthDetails;
import com.linkedin.oauth.pojo.LinkedInMasterModel;
import com.linkedin.oauth.repository.LinkedInAuthDetailsDAO;
import com.linkedin.oauth.repository.LinkedInRepo;
import com.linkedin.oauth.service.LinkedInOAuthServiceCall;
import com.linkedin.oauth.service.LinkedInServices;
import com.linkedin.oauth.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.linkedin.oauth.util.LinkedInAPIConstants.*;

@Service
public class LinkedInServicesImpl implements LinkedInServices {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInServicesImpl.class);

    @Value("${LINKEDIN_ADMIN_USERS}")
    String adminUsers;

    @Autowired
    LinkedInRepo linkedInRepo;

    RestTemplate restTemplate = new RestTemplate();

    @Autowired
    LinkedInAuthDetailsDAO linkedInAuthDetailsDAO;

    @Autowired
    LinkedInOAuthServiceCall linkedInOAuthServiceCall;

    @Scheduled(cron = "0 0 10 ? * *")
    public void runScheduler() {
        logger.info("Scheduler started to fetching the data into Master table");
        ResponseEntity<ResponseDto> response = makeGetCall(ResponseDto.class);
        logger.info("Save all data into Master table");
        List<LinkedInMasterModel> linkedInMasterModels = importDto(response.getBody());
        Collections.reverse(linkedInMasterModels);

        List<LinkedInMasterModel> latestResponse = getAllStories();
        if (latestResponse.size() == 0) {
            logger.info("Count : " + linkedInMasterModels.size());
            linkedInRepo.saveAll(linkedInMasterModels);
        } else {

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
            for (LinkedInMasterModel linkedInMasterModel : newStories) {
                if (linkedInMasterModel.getCreatedAt().compareTo(latestRecord.getCreatedAt()) > 0) {
                    newStoriesTobeSaved.add(linkedInMasterModel);
                }
            }
            logger.info("Count : " + newStoriesTobeSaved.size());
            linkedInRepo.saveAll(newStoriesTobeSaved);


        }

    }

    public ResponseEntity<ResponseDto> makeGetCall(Class<ResponseDto> responseDto) {
        try {

            URI uri = new URI(BASE_URL + "author=" + AUTHOR + "&q=" + Q + "&count=10" + "&isDsc=false");
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
                logger.error("Error while fetching Linked Stories from Master Table: {} : ", e.getMessage());
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            // 404 = Patient not found, proceed without any errors
            logger.error("Error while fetching Linked Stories from Master Table: {} : ", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    protected HttpEntity<String> generateHttpEntity() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("LinkedIn-Version", "202209");
        if (ObjectUtils.isEmpty(adminUsers)) {
            throw new Exception("Admin users not configured");
        }

        String[] users = adminUsers.split(",");
        String[] adminuser = users[0].split(" ");
        LinkedInAuthDetails linkedInAuthDetails = linkedInAuthDetailsDAO.findByFirstNameAndLastName(adminuser[0], adminuser[1]);
        headers.setBearerAuth(getRefreshToken(linkedInAuthDetails));
        HttpEntity<String> header = new HttpEntity<String>(headers);
        return header;
    }


    private String getRefreshToken(LinkedInAuthDetails linkedInAuthDetails) throws Exception {
        String refreshToken = null;
        Date tokenCreatedDate = null;
        TokenIntrospectionResponseDTO introspectResponse = linkedInOAuthServiceCall.tokenIntrospection(linkedInAuthDetails.getRefreshToken());
        if (introspectResponse != null && !introspectResponse.getActive().equals(Boolean.TRUE)) {
            AccessToken accessTokenDetails = linkedInOAuthServiceCall.refreshToken(linkedInAuthDetails.getRefreshToken());
            if (!ObjectUtils.isEmpty(accessTokenDetails)) {
                LinkedInAuthDetails authDetails = updateLinkedInAuthDetails(accessTokenDetails, linkedInAuthDetails);
                refreshToken = authDetails.getRefreshToken();
            }
        } else {
            refreshToken = linkedInAuthDetails.getRefreshToken();
        }
        return refreshToken;
    }

    private LinkedInAuthDetails updateLinkedInAuthDetails(AccessToken accessToken, LinkedInAuthDetails linkedInAuthDetails) {
        linkedInAuthDetails.setAccessToken(accessToken.getAccessToken());
        linkedInAuthDetails.setAccessTokenExpireIn(accessToken.getExpiresIn());
        linkedInAuthDetails.setRefreshToken(accessToken.getRefreshToken());
        linkedInAuthDetails.setRefreshTokenExpirein(accessToken.getRefreshTokenExpiresIn());
        linkedInAuthDetails.setScope(accessToken.getScope());
        LinkedInAuthDetails details = linkedInAuthDetailsDAO.save(linkedInAuthDetails);
        return details;
    }


    public void saveLinkedInStories() {
        logger.info("Scheduler started to fetching the data into Master table");
        List<LinkedInMasterModel> linkedInMasterModels = importDto(makeGetCall(ResponseDto.class).getBody());
        linkedInRepo.saveAll(linkedInMasterModels);
    }


    public List<LinkedInMasterModel> getAllStories() {
        logger.info("Get API to fetch posted stories : ");
        List<LinkedInMasterModel> response = linkedInRepo.findAll();
        return response;
    }

    private List<LinkedInMasterModel> importDto(ResponseDto responseDto) {
        List<LinkedInMasterModel> linkedInMasterModels = new ArrayList<>();
        for (PostStoryResponseDto dto : responseDto.getElement()) {
            LinkedInMasterModel linkedInMasterModel = new LinkedInMasterModel();
            if (Objects.nonNull(dto.getCreatedAt())) {
                linkedInMasterModel.setCreatedAt(dto.getCreatedAt());
            }
            linkedInMasterModel.setLifecycleState(dto.getLifecycleState());
            if (Objects.nonNull(dto.getLastModifiedAt())) {
                linkedInMasterModel.setLastModifiedAt(dto.getLastModifiedAt());
            }
            linkedInMasterModel.setVisibility(dto.getVisibility());
            if (Objects.nonNull(dto.getPublishedAt())) {
                linkedInMasterModel.setPublishedAt(dto.getPublishedAt());
            }

            linkedInMasterModel.setAuthor(dto.getAuthor());
            linkedInMasterModel.setUid(dto.getUid());

            if (Objects.nonNull(dto.getContent())) {

                if (Objects.nonNull(dto.getContent().getMedia())) {
                    linkedInMasterModel.setContentMediaId(dto.getContent().getMedia().getId());
                }

                if (Objects.nonNull(dto.getContent().getMedia())) {
                    linkedInMasterModel.setTitle(dto.getContent().getMedia().getTitle());
                }
            }

            if (Objects.nonNull(dto.getContent())) {
                if (Objects.nonNull(dto.getContent().getArticle())) {
                    linkedInMasterModel.setDescription(dto.getContent().getArticle().getDescription());
                    linkedInMasterModel.setThumbnail(dto.getContent().getArticle().getThumbnail());
                    linkedInMasterModel.setSource(dto.getContent().getArticle().getSource());
                    linkedInMasterModel.setTitle(dto.getContent().getArticle().getTitle());
                }
            }

            linkedInMasterModel.setCommentary(dto.getCommentary());
            if (Objects.nonNull(dto.getLifecycleStateInfoDto().getEditable())) {
                linkedInMasterModel.setEditedByAuthor(DateUtil.convertStringToBoolean(dto.getLifecycleStateInfoDto().getEditable()));
            }

            linkedInMasterModels.add(linkedInMasterModel);
        }
        return linkedInMasterModels;
    }

    public void likePosts(List<String> postIds, String personId) throws Exception {
        if (ObjectUtils.isEmpty(postIds)) {
            for (String postId : postIds) {
                callLinkedInLikePost(postId, personId);
            }
        }
    }

    private void callLinkedInLikePost(String postId, String personId) throws Exception {
        {
            try {
                ObjectMapper mapper = new ObjectMapper();
                HttpHeaders headers = new HttpHeaders();
                headers.set("LinkedIn-Version", "202209");
                LinkedInAuthDetails linkedInAuthDetails = linkedInAuthDetailsDAO.findByUserId(personId);
                headers.setBearerAuth(getRefreshToken(linkedInAuthDetails));
                Map<String, String> reqParam = new HashMap<>();
                reqParam.put("", "");
                reqParam.put("", "");
                HttpEntity<String> entity = new HttpEntity<String>(mapper.writeValueAsString(reqParam), headers);

                //HttpEntity<String> entity = generateHttpEntity();

                UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(LIKE_POST_URI)
                        .queryParam("actor", "personId").encode();

                String response = String.valueOf(restTemplate.exchange(builder.buildAndExpand().toUri(), HttpMethod.POST, entity, ResponseDto.class));
                logger.info("Get Linked In Stories API response: {}", response);
            } catch (URISyntaxException e) {
                logger.error("Error while fetching Linked Stories from Master Table: {} : ", e.getMessage());
                throw new RuntimeException();
            } catch (HttpClientErrorException e) {
                if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                    // 404 = Patient not found, proceed without any errors
                    logger.error("Error while fetching Linked Stories from Master Table: {} : ", e.getMessage());
                    throw e;
                }

            } catch (Exception e) {
                // 404 = Patient not found, proceed without any errors
                logger.error("Error while fetching Linked Stories from Master Table: {} : ", e.getMessage());
                throw e;
            }
        }

    }


}
