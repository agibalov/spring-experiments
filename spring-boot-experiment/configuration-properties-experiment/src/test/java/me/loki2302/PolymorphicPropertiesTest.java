package me.loki2302;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Demonstrates how to load a "polymorphic" collection from application.yml.
 * Take a look at campaigns-test.yml
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.config.location=classpath:campaigns-test.yml")
public class PolymorphicPropertiesTest {
    @Autowired
    private AppProperties appProperties;

    @Test
    public void dummy() {
        List<Campaign> actualCampaigns = appProperties.getActualCampaigns();

        assertEquals(2, actualCampaigns.size());

        Campaign campaign0 = actualCampaigns.get(0);
        assertTrue(campaign0 instanceof CampaignA);
        assertEquals("The Campaign One", campaign0.getName());
        assertEquals("222", ((CampaignA) campaign0).getXxx());

        Campaign campaign1 = actualCampaigns.get(1);
        assertTrue(campaign1 instanceof CampaignB);
        assertEquals("Some other campaign", campaign1.getName());
        assertEquals("555", ((CampaignB) campaign1).getYyy());
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Config {
        @Bean
        public AppProperties appProperties() {
            return new AppProperties();
        }
    }

    @ConfigurationProperties
    @Data
    @Validated
    @NoArgsConstructor
    public static class AppProperties {
        @NotEmpty
        private String something;

        @Valid
        private List<CampaignHolder> campaigns;

        public List<Campaign> getActualCampaigns() {
            return campaigns.stream()
                    .map(c -> c.getCampaign())
                    .collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    @Validated
    public static class CampaignHolder {
        @NotNull
        @Valid
        private Campaign campaign;

        public void setCampaignA(CampaignA campaign) {
            this.campaign = campaign;
        }

        public void setCampaignB(CampaignB campaign) {
            this.campaign = campaign;
        }
    }

    @Data
    @Validated
    public static abstract class Campaign {
        @NotEmpty
        private String name;
    }

    @Data
    @Validated
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class CampaignA extends Campaign {
        @NotEmpty
        private String xxx;
    }

    @Data
    @Validated
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @NoArgsConstructor
    public static class CampaignB extends Campaign {
        @NotEmpty
        private String yyy;
    }
}
