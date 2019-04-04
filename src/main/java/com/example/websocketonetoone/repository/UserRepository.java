package com.example.websocketonetoone.repository;

import com.example.websocketonetoone.model.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by rambo on 2019/4/4.
 */
public interface UserRepository extends JpaRepository<UserInfo,Integer>{

}
