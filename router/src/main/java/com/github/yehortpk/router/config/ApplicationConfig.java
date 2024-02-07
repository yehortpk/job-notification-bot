package com.github.yehortpk.router.config;

import com.github.yehortpk.router.models.SubscriptionDTO;
import com.github.yehortpk.router.models.VacancyDTO;
import com.github.yehortpk.router.models.VacancyNotificationDTO;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApplicationConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${notifier-topic-consumer-group-id}")
    private String notifierTopicConsumerGroupId;

    @Value("${KAFKA_NOTIFIER_TOPIC}")
    private String notifierTopic;

    @Bean
    public NewTopic notifierTopic() {
        return new NewTopic(notifierTopic, 1, (short) 1);
    }

    @Value("${KAFKA_SUBSCRIBE_TOPIC}")
    private String subscribeTopic;

    @Value("${subscribe-topic-consumer-group-id}")
    private String subscribeTopicConsumerGroupId;

    @Bean
    public NewTopic subscribeTopic() {
        return new NewTopic(subscribeTopic, 1, (short) 1);
    }

    private Map<String, Object> getConsumerFactoryProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "2000");

        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return props;
    }

    @Bean
    public ConsumerFactory<String, VacancyDTO> notifierConsumerFactory() {
        Map<String, Object> props = getConsumerFactoryProps();

        props.put(ConsumerConfig.GROUP_ID_CONFIG, notifierTopicConsumerGroupId);
        props.put(JsonDeserializer.TYPE_MAPPINGS, "vacancy:com.github.yehortpk.router.models.VacancyDTO");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, SubscriptionDTO> subscribeConsumerFactory() {
        Map<String, Object> props = getConsumerFactoryProps();

        props.put(ConsumerConfig.GROUP_ID_CONFIG, subscribeTopicConsumerGroupId);
        props.put(JsonDeserializer.TYPE_MAPPINGS, "subscription:com.github.yehortpk.router.models.SubscriptionDTO");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VacancyDTO> notifierContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, VacancyDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(notifierConsumerFactory());

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, SubscriptionDTO> subscribeContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, SubscriptionDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(subscribeConsumerFactory());

        return factory;
    }

    // Producer factory
    @Value("${KAFKA_BOT_NOTIFIER_TOPIC}")
    private String kafkaBotNotifierTopic;

    @Bean
    public ProducerFactory<String, VacancyNotificationDTO> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put(JsonSerializer.TYPE_MAPPINGS,
                "vacancy:com.github.yehortpk.router.models.VacancyNotificationDTO");

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate() {
        KafkaTemplate<String, VacancyNotificationDTO> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setDefaultTopic(kafkaBotNotifierTopic);
        return kafkaTemplate;
    }

}
