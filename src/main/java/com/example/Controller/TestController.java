package com.example.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author CaiWencheng
 * @Date 2021-10-12 23:31
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "hello mimi-springboot";
    }
}
