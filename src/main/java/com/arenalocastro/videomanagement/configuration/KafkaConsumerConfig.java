package com.arenalocastro.videomanagement.configuration;

import com.arenalocastro.videomanagement.kafka.VideoProcessingListener;
import com.arenalocastro.videomanagement.services.CloudEdgeService;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/*@ConditionalOnProperty(
        value="deployment.needKafka",
        havingValue="true",
        matchIfMissing=true
)*/
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value(value = "${KAFKA_ADDRESS}")
    private String bootstrapAddress;

    @Value(value = "${KAFKA_GROUP_ID}")
    private String groupId;

    @Value("#{new Boolean('${IS_CLOUD:true}')}")
    private Boolean isCloud;

    @Value("#{new Integer('${VARIANT_TYPE:-1}')}")
    private Integer variantType;

    @Bean
    public ConsumerFactory<String,String> consumerFactory() {
        if (!isCloud && variantType == 0) {
            return null;
        }

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        if (!isCloud && variantType == 0) {
            return null;
        }

        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public VideoProcessingListener userListener2() {
        if (!isCloud && variantType == 0) {
            return null;
        }
        return new VideoProcessingListener();
    }
}
