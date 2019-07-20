package com.qw.study.marvenwebsocket.controller;

import com.qw.study.marvenwebsocket.beans.UserInfoVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : qw.r
 * @since : 19-1-12 13:35
 */
@Controller
public class HelloWorldController {

    private static final Logger logger = LoggerFactory.getLogger(HelloWorldController.class);


    @RequestMapping("hello")
    @ResponseBody
    public Object hello() {
        UserInfoVo userInfoVo = new UserInfoVo("userid", "username");
        logger.info("{}", userInfoVo.toString());
        return userInfoVo;
    }
}
