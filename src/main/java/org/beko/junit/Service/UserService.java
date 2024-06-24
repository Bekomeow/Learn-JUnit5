package org.beko.junit.Service;

import org.beko.junit.DTO.User;

import java.util.*;

public class UserService {
    private final List<User> users = new ArrayList<>();
    public List<User> getAll() {
        return users;
    }

    public boolean add(User user) {
        return users.add(user);
    }

    public Optional<User> login(String name, String password) {
        return users.stream()
                .filter(user -> user.getName().equals(name))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }
}
