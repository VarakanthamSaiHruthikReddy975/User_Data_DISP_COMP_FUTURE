package com.demo.multiThreadingExample.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.multiThreadingExample.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {

}
