package com.linkedin.oauth.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
 public class ProfileDetails {
    @JsonProperty(value = "id")
    private String id;
    @JsonProperty(value = "localizedFirstName")
    private String first_name;
    @JsonProperty(value = "localizedLastName")
    private String last_name;

   public ProfileDetails() {
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getFirst_name() {
      return first_name;
   }

   public void setFirst_name(String first_name) {
      this.first_name = first_name;
   }

   public String getLast_name() {
      return last_name;
   }

   public void setLast_name(String last_name) {
      this.last_name = last_name;
   }
}

