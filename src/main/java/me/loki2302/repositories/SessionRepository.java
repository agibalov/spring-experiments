package me.loki2302.repositories;

import me.loki2302.entities.Session;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface SessionRepository extends JpaRepository<Session, Long>, QueryDslPredicateExecutor<Session> {
	Session findBySessionToken(String sessionToken);	
}