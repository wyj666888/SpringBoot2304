package com.tedu.springboot2304.entity;

import java.io.Serializable;

public class User implements Serializable {
        private String username;
        private String password;
        private String nickname;
        private int age;

        /*
        * JAVA BEAN定义规范:
        * 1:属性私有化
        * 2:提供属性对应的getter,setter
        * 3:要有无参构造器
        * */
    public User(){}
    public User(String username, String password, String nickname, int age) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", age=" + age +
                '}';
    }
}

