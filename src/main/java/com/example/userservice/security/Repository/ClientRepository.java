package com.example.userservice.security.Repository;


import com.example.userservice.security.Models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,String>{
    Optional<Client> findByClientId(String clientId);
}
