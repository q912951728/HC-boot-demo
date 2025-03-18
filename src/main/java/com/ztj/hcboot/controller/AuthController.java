package com.ztj.hcboot.controller;


import com.ztj.hcboot.util.JwtTokenUtil;
import com.ztj.hcboot.vo.AuthRequest;
import com.ztj.hcboot.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

}