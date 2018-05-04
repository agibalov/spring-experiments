package io.agibalov.lobs;

import org.apache.commons.io.IOUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Config.class)
@DirtiesContext
public class LobsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private LargeDocumentRepository largeDocumentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    public void canSaveAndReadLOBs() {
        TheBLOB theBLOB = new TheBLOB();
        theBLOB.message = "hello blob";

        Data data = new Data();
        data.clob = "hello clob";
        data.blob = theBLOB;
        dataRepository.save(data);

        data = dataRepository.findOne(1L);
        assertEquals("hello clob", data.clob);
        assertNotNull(data.blob);
        assertEquals("hello blob", data.blob.message);
    }

    @Test
    @Transactional // required for em.unwrap(Session.class)
    public void canSaveBlobFromStreamAndReadBlobAsStream() throws SQLException, IOException {
        File tempFile = temporaryFolder.newFile();
        try(FileOutputStream fos = new FileOutputStream(tempFile)) {
            for(int i = 0; i < 1000; ++i) {
                IOUtils.write("hello there", fos);
            }
        }
        long tempFileLength = tempFile.length();

        Session session = entityManager.unwrap(Session.class);

        long largeDocumentId;
        try(FileInputStream fis = new FileInputStream(tempFile)) {
            Blob blob = Hibernate.getLobCreator(session).createBlob(fis, tempFileLength);

            LargeDocument largeDocument = new LargeDocument();
            largeDocument.data = blob;
            largeDocument = largeDocumentRepository.save(largeDocument);
            largeDocumentId = largeDocument.id;
        }

        LargeDocument retrievedLargeDocument = largeDocumentRepository.findOne(largeDocumentId);

        // retrieveLargeDocument comes from the current session, not from the database
        // for some reason its .data is not usable,
        // A call to em.refresh() reloads the entire entity from db
        entityManager.refresh(retrievedLargeDocument);

        assertEquals(tempFileLength, retrievedLargeDocument.data.length());

        try(InputStream fis = new FileInputStream(tempFile)) {
            try (InputStream bis = retrievedLargeDocument.data.getBinaryStream()) {
                assertTrue(IOUtils.contentEquals(fis, bis));
            }
        }
    }
}
