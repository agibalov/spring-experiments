package io.agibalov.lobs;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LargeDocumentRepository extends JpaRepository<LargeDocument, Long> {

}
