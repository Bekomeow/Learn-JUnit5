package com.beko.junit.Service;

import com.beko.junit.DTO.User;

import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class UserService {
    private final List<User> users = new ArrayList<>();
    public List<User> getAll() {
        return users;
    }

    public void add(User... user) {
        this.users.addAll(Arrays.asList(user));
    }

    public Optional<User> login(String name, String password) {
        if(name == null ||password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }

        return users.stream()
                .filter(user -> user.getName().equals(name))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConvertedById() {
        return users.stream().collect(toMap(User::getId, identity()));
    }
}
