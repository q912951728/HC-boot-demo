package com.ztj.hcboot;

import com.ztj.hcboot.mapper.UserMapper;
import com.ztj.hcboot.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitDatabase implements CommandLineRunner {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查用户是否存在，不存在则创建
        User user = userMapper.findByUserId(18070960411L);

        if (user == null) {
            User newUser = new User();
            newUser.setId(18070960411L);
            newUser.setNickname("ztj");
            newUser.setPassword(passwordEncoder.encode("123456"));

            System.out.println(newUser);

            userMapper.insertOne(newUser);
            System.out.println("用户不存在，创建用户：" + newUser.getNickname());
        }
    }
}