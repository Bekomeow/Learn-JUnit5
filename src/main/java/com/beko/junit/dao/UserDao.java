package com.beko.junit.dao;

import lombok.SneakyThrows;

import java.sql.DriverManager;

public class UserDao {
    @SneakyThrows
    public boolean delete(Integer userId) {
        DriverManager.getConnection("url", "username", "password");
        return true;
    }
}
