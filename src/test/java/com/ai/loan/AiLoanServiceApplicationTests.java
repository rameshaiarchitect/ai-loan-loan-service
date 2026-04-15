package com.ai.loan;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(properties = {
		"spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
		"spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
})
class AiLoanServiceApplicationTests {

	@Container
	static KafkaContainer kafka =
			new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Test
	void shouldPublishLoanEvent() {

		String topic = "loan.application.submitted";

		kafkaTemplate.send(topic, "test-loan-event");

		Map<String, Object> consumerProps = new HashMap<>();

		consumerProps.put("bootstrap.servers", kafka.getBootstrapServers());
		consumerProps.put("group.id", "test-group");
		consumerProps.put("key.deserializer", StringDeserializer.class);
		consumerProps.put("value.deserializer", StringDeserializer.class);
		consumerProps.put("auto.offset.reset", "earliest");
		consumerProps.put("enable.auto.commit", "true");

		var consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerProps,
				new StringDeserializer(),
				new StringDeserializer()
		);

		var consumer = consumerFactory.createConsumer();
		consumer.subscribe(Collections.singletonList(topic));

		ConsumerRecord<String, String> record =
				KafkaTestUtils.getSingleRecord(consumer, topic);

		assertThat(record.value()).isEqualTo("test-loan-event");
	}
}