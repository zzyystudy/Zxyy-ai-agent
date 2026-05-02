package com.Zxyy.controller;

import com.Zxyy.app.LoveApp;
import com.Zxyy.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HiController {

    @Autowired
    private LoveApp loveApp;

    @GetMapping("/hi")
    public Result hi(){
        return Result.success("hello world");
    }

    @GetMapping("/ceshi")
    public Result ceshi(){
        String who = loveApp.who();
        return Result.success(who);
    }
}
