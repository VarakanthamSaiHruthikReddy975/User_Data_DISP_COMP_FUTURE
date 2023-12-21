package com.demo.multiThreadingExample.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.demo.multiThreadingExample.entity.User;
import com.demo.multiThreadingExample.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    UserRepository userRepository;
    
    Logger logger = LoggerFactory.getLogger(UserService.class);
    
    @Async
    public CompletableFuture<List<User>> saveUsers(MultipartFile file) throws Exception {
        long start = System.currentTimeMillis();
        List<User> users = new ArrayList<>();
        try {
            users = parseCSVFile(file);
        } catch (IOException e) {
            logger.error("Error parsing CSV file: {}", e.getMessage());
            // Handle the exception based on your application's requirement
            // For instance, return an empty list or a CompletableFuture with an error
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        logger.info("saving list of users of size {}", users.size(), Thread.currentThread().getName());
        users = userRepository.saveAll(users);
        long end = System.currentTimeMillis();
        logger.info("Total Time {}", (end - start));
        return CompletableFuture.completedFuture(users);
    }

    @Async
    public CompletableFuture<List<User>> findAllUsers() {
        logger.info("Get list of users by {}", Thread.currentThread().getName());
        List<User> users = userRepository.findAll();
        return CompletableFuture.completedFuture(users);
    }

    private List<User> parseCSVFile(final MultipartFile file) throws Exception {
        final List<User> users = new ArrayList<>();
        try (final BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                final String[] data = line.split(",");
                // Validate the data length
                if (data.length < 3) {
                    logger.warn("Skipping invalid record: {}", line);
                    continue;
                }
                final User user = new User();
                user.setName(data[0]);
                user.setEmail(data[1]);
                user.setGender(data[2]);
                users.add(user);
            }
        } catch (final IOException e) {
            logger.error("Failed to parse CSV file", e);
            throw new Exception("Failed to parse CSV file {}", e);
 // Rethrow the exception to be handled in saveUsers
        }
        return users;
    }
}

