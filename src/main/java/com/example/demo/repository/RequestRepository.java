package com.example.demo.repository;

import com.example.demo.entity.Request;
import com.example.demo.entity.RequestStatus;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByUser(User user);
    List<Request> findByStatus(RequestStatus status);
}
