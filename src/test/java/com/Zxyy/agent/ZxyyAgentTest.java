package com.Zxyy.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ZxyyAgentTest {

    @Resource
    private ZxyyAgent zxyyAgent;

    @Test
    public void testRun() {
        String result = zxyyAgent.run("帮我找一下附近的小众旅游点");
        System.out.println(result);
    }

}