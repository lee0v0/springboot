package com.example.blog.controller;

import com.example.blog.dto.AcessTokenDTO;
import com.example.blog.dto.GithubUser;
import com.example.blog.provider.GitHubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthorizeController {

    @Autowired
    private GitHubProvider gitHubProvider;

    @Value("${github.client_id}")
    private  String client_id;

    @Value("${github.client_secret}")
    private  String client_secret;
    @Value("${github.redirect_uri}")
    private  String redirect_uri;
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state){
        AcessTokenDTO acessTokenDTO = new AcessTokenDTO();
        acessTokenDTO.setCode(code);
        acessTokenDTO.setState(state);
        acessTokenDTO.setRedirect_uri(redirect_uri);
        acessTokenDTO.setClient_id(client_id);
        acessTokenDTO.setClient_secret(client_secret);
//      获取acessToken
        String acessToken=gitHubProvider.getAccessToken(acessTokenDTO);
//        通过acessToken 获取user信息
        GithubUser user = gitHubProvider.getUser(acessToken);
        System.out.println(acessToken);
        System.out.println(user.toString());
        return "index";
    }
}
