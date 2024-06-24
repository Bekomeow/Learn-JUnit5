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
}
