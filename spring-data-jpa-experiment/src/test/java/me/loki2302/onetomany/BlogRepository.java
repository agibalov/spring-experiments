package me.loki2302.onetomany;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
