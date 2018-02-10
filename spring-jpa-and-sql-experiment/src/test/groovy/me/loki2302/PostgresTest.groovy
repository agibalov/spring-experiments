package me.loki2302

import groovy.sql.Sql
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.junit.Assert.*

@IntegrationTest
@SpringApplicationConfiguration(classes = Config.class)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("app")
class PostgresTest {
    @Autowired
    private Sql sql

    @Test
    void canTalkToPostgres() {
        def row = sql.firstRow('select 1 + 1 as result')
        assertEquals(2, row.result)
    }
}
