package com.example.demo.service;

import com.example.demo.entity.UserInfo;
import com.example.demo.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserInfoService {
    private final UserInfoRepository userInfoRepository;

    @Autowired
    public UserInfoService(UserInfoRepository userInfoRepository) {
        this.userInfoRepository = userInfoRepository;
    }

    public void saveUserInfo(String snsProvider, String tokenInfo, String email, String name, String picture) {
        UserInfo userInfo = new UserInfo();
        userInfo.setSnsProvider(snsProvider);
        userInfo.setTokenInfo(tokenInfo);
        userInfo.setEmail(email);
        userInfo.setName(name);
        userInfo.setPicture(picture);
        userInfo.setJoinDate(LocalDateTime.now());
        userInfoRepository.save(userInfo);
    }
}