package com.ztj.hcboot.controller;


import com.ztj.hcboot.mapper.UserMapper;
import com.ztj.hcboot.pojo.User;
import com.ztj.hcboot.util.JwtTokenUtil;
import com.ztj.hcboot.vo.AuthRequest;
import com.ztj.hcboot.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/authenticate")
    public RespBean authenticate(@RequestBody AuthRequest authRequest) throws Exception {
        try {

            // 进行认证
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getId(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("用户名或密码错误", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getId().toString());
        final String jwt = jwtTokenUtil.generateToken(userDetails.getUsername());

        return RespBean.success(jwt);
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @GetMapping("/register")
    public RespBean register(@RequestParam Integer num) {

        while((num--)>0){
            User newUser = new User();
            newUser.setId(13303100000L+num);
            newUser.setNickname("zzzuser"+num+"c");
            newUser.setPassword(passwordEncoder.encode("123456"));

            System.out.println(newUser);

            userMapper.insertOne(newUser); // 解除注释以启用插入操作
            System.out.println("用户不存在，创建用户：" + newUser.getNickname());
        }
        return RespBean.success("注册成功");
    }


    @GetMapping("/getjwt")
    public RespBean getJwt() {
        // 获取所有用户
        List<User> users = userMapper.selectAllUsers();

        // 存储所有用户的JWT令牌
        try (FileWriter writer = new FileWriter("user_jwts.txt")) {
            for (User user : users) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getId().toString());
                final String jwt = jwtTokenUtil.generateToken(userDetails.getUsername());
                writer.write(jwt + "\n");
            }
        } catch (IOException e) {
            return RespBean.success();
        }

        return RespBean.success("JWT令牌已写入文件");
    }

}