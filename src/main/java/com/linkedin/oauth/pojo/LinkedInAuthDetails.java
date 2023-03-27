package com.linkedin.oauth.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "linkedin_auth_details")
@Data
@ToString
@NoArgsConstructor
public class LinkedInAuthDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @Column(name = "user_id")
    private String userId;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "access_token", length = 400)
    private String accessToken;

    @Column(name = "refresh_token", length = 400)
    private String refreshToken;

    @Column(name = "refresh_token_exp_in")
    private int refreshTokenExpirein;

    @Column(name = "access_token_exp_in")
    private int accessTokenExpireIn;

    @Column(name = "scope")
    private String scope;

}
