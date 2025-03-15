package com.ztj.hcboot.service.impl;

import com.ztj.hcboot.mapper.UserMapper;
import com.ztj.hcboot.pojo.User;
import com.ztj.hcboot.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;




}
