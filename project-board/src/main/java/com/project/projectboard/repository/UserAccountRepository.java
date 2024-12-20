package com.project.projectboard.repository;

import com.project.projectboard.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
@RepositoryRestResource
public interface UserAccountRepository extends JpaRepository<UserAccount, String> {

}