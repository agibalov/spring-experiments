package me.loki2302.repositories;

import me.loki2302.entities.Session;

import org.springframework.data.jpa.repository.JpaRepository;


public interface SessionRepository extends JpaRepository<Session, Long> {
	Session findBySessionToken(String sessionToken);	
}