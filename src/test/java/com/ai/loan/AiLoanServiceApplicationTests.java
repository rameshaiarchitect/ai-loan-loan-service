package com.ai.loan;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		properties = {
				"spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
				"spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer"
		}
)
class AiLoanServiceApplicationTests {

	@Container
	static KafkaContainer kafka =
			new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));

	@DynamicPropertySource
	static void overrideProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
	}

	@Autowired
	private TestRestTemplate restTemplate;

	private Map<String, Object> consumerConfig() {
		Map<String, Object> props = new HashMap<>();
		props.put("bootstrap.servers", kafka.getBootstrapServers());
		props.put("group.id", "test-group");
		props.put("key.deserializer", StringDeserializer.class);
		props.put("value.deserializer", StringDeserializer.class);
		props.put("auto.offset.reset", "earliest");
		props.put("enable.auto.commit", "true");
		return props;
	}

	@Test
	void shouldProcessLoanApplicationAndPublishEvent() {

		String topic = "loan.application.submitted";

		var consumerFactory = new DefaultKafkaConsumerFactory<>(
				consumerConfig(),
				new StringDeserializer(),
				new StringDeserializer()
		);

		var consumer = consumerFactory.createConsumer();
		consumer.subscribe(Collections.singletonList(topic));

		// 🔥 IMPORTANT: trigger partition assignment
		consumer.poll(Duration.ofSeconds(2));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		String requestJson = """
                {
                  "applicationId": "APP123",
                  "amount": 5000,
                  "salary": 3000
                }
                """;

		HttpEntity<String> request = new HttpEntity<>(requestJson, headers);

		ResponseEntity<String> response = restTemplate.postForEntity(
				"/loan/apply",
				request,
				String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo("Application Submitted");

		ConsumerRecord<String, String> record =
				KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofSeconds(10));

		assertThat(record.value()).contains("APP123");
	}
}