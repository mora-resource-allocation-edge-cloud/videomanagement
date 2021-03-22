package com.arenalocastro.videomanagement.configuration;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConditionalOnProperty(
        value="deployment.needKafka",
        havingValue="true",
        matchIfMissing=true
)
public class KafkaProducerConfig {
    @Value(value = "${KAFKA_ADDRESS}")
    private String bootstrapAddress;

    @Value("#{new Boolean('${IS_CLOUD:true}')}")
    private Boolean isCloud;

    @Value("#{new Integer('${VARIANT_TYPE:-1}')}")
    private Integer variantType;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        if (!isCloud && variantType == 0) {
            return null;
        }
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        if (!isCloud && variantType == 0) {
            return null;
        }
        return new KafkaTemplate<>(producerFactory());
    }
}
