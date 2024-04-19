package com.curso.ecommerce.service;

import com.curso.ecommerce.model.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findById(Integer id);

}
