package me.loki2302.enums;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@IntegrationTest
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EnumEntityTest {
    @Autowired
    private TicketRepository ticketRepository;

    @Test
    public void canSaveAnEntityWithEnumProperty() {
        Ticket ticket = new Ticket();
        ticket.status = TicketStatus.Reported;
        ticket = ticketRepository.save(ticket);
        assertNotNull(ticket.id);
        assertEquals(TicketStatus.Reported, ticket.status);

        ticket.status = TicketStatus.Done;
        ticket = ticketRepository.save(ticket);
        assertEquals(TicketStatus.Done, ticket.status);
    }

    @Test
    public void canSearchTicketsByStatus() {
        ticketRepository.save(Arrays.asList(
                ticket(TicketStatus.Reported),
                ticket(TicketStatus.InProgress),
                ticket(TicketStatus.Done),
                ticket(TicketStatus.InProgress),
                ticket(TicketStatus.Done),
                ticket(TicketStatus.Done)));

        assertEquals(1, ticketRepository.findByStatus(TicketStatus.Reported).size());
        assertEquals(2, ticketRepository.findByStatus(TicketStatus.InProgress).size());
        assertEquals(3, ticketRepository.findByStatus(TicketStatus.Done).size());
    }

    private static Ticket ticket(TicketStatus status) {
        Ticket ticket = new Ticket();
        ticket.status = status;
        return ticket;
    }
}
