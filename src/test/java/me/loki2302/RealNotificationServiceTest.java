package me.loki2302;

import static org.junit.Assert.assertEquals;
import me.loki2302.notifications.NotificationService;
import me.loki2302.notifications.RealNotificationService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = RealNotificationServiceTestConfiguration.class)
public class RealNotificationServiceTest {
    @Autowired
    private NotificationService notificationService;
    
    @Test
    public void test() {
        notificationService.notifyUser();        
        assertEquals(RealNotificationService.class, notificationService.getClass());
    }
}
