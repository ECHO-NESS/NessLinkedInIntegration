package com.linkedin.oauth.repository;

import com.linkedin.oauth.pojo.LinkedInMasterModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkedInRepo extends JpaRepository<LinkedInMasterModel, Integer> {

    @Override
    <S extends LinkedInMasterModel> S save(S entity);


    @Override
    List<LinkedInMasterModel> findAll();
}
