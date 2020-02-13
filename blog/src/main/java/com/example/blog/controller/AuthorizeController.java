package com.example.blog.controller;

import com.example.blog.dto.AcessTokenDTO;
import com.example.blog.dto.GithubUser;
import com.example.blog.mapper.UserMapper;
import com.example.blog.model.User;
import com.example.blog.provider.GitHubProvider;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Autowired
    private UserMapper userMapper;
    @Value("${github.client_id}")
    private  String client_id;

    @Value("${github.client_secret}")
    private  String client_secret;
    @Value("${github.redirect_uri}")
    private  String redirect_uri;
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AcessTokenDTO acessTokenDTO = new AcessTokenDTO();
        acessTokenDTO.setCode(code);
        acessTokenDTO.setState(state);
        acessTokenDTO.setRedirect_uri(redirect_uri);
        acessTokenDTO.setClient_id(client_id);
        acessTokenDTO.setClient_secret(client_secret);
//      获取acessToken
        String acessToken=gitHubProvider.getAccessToken(acessTokenDTO);
//        通过acessToken 获取user信息
        GithubUser githubUser = gitHubProvider.getUser(acessToken);
//        System.out.println(acessToken);
//        System.out.println(user.toString());
        if(githubUser!=null){
            //授权成功，将授权用户信息插入数据库
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setAccount_id(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getName());
            user.setGmt_create(System.currentTimeMillis());
            user.setGmt_modified(user.getGmt_create());
            //            登陆成功写cookies和session
            userMapper.insert(user);
            response.addCookie(new Cookie("Token",user.getToken()));

            request.getSession().setAttribute("user",githubUser);
            return "redirect:index";
        }else{
//            登录失败
            return "redirect:index";
        }
    }
}
