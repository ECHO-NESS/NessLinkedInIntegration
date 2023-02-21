package com.linkedin.oauth.repository;

import com.linkedin.oauth.pojo.LinkedInAuthDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface LinkedInAuthDetailsDAO extends JpaRepository<LinkedInAuthDetails, Integer> {

    @Override
    List<LinkedInAuthDetails> findAll();

    @Override
    Optional<LinkedInAuthDetails> findById(Integer integer);

    @Override
    <S extends LinkedInAuthDetails> S save(S s);
}
