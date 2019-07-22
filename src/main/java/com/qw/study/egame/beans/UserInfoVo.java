package com.qw.study.egame.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : qw.r
 * @since : 19-1-13 19:57
 */
public class UserInfoVo {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoVo.class);



    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserInfoVo(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + id + '\"' +
                ", \"name\":\"" + name + '\"' +
                '}';
    }

    public static void main(String[] args) {
        UserInfoVo userInfoVo = new UserInfoVo("userid", "username");
        logger.info("{}", userInfoVo.toString());

    }
}
