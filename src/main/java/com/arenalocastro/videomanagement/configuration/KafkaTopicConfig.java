package com.arenalocastro.videomanagement.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(
        value="deployment.needKafka",
        havingValue="true",
        matchIfMissing=true
)
public class KafkaTopicConfig {
    @Value(value = "${KAFKA_ADDRESS}")
    private String bootstrapAddress;

    @Value(value = "${KAFKA_MAIN_TOPIC}")
    private String kafkaMainTopic;

    @Value("#{new Boolean('${IS_CLOUD:true}')}")
    private Boolean isCloud;

    @Value("#{new Integer('${VARIANT_TYPE:-1}')}")
    private Integer variantType;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        if (!isCloud && variantType == 0)
            return null;
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
        return new NewTopic(kafkaMainTopic, 100, (short) 1);
    }
}
