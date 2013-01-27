package com.loki2302.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.loki2302.entities.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
	Session findBySessionToken(String sessionToken);	
}