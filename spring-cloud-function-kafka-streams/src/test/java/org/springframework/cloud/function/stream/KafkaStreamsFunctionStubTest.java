package org.springframework.cloud.function.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

/**
 * @author Soby Chacko
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KafkaStreamsFunctionStubTest.StreamingFunctionApplication.class,
				properties = {"server.port=0", "spring.jmx.enabled=false",
						"spring.cloud.stream.bindings.input.destination=foo",
						"spring.cloud.stream.bindings.output.destination=bar",
						"spring.cloud.stream.bindings.output.contentType=application/json",
						"spring.cloud.stream.kafka.streams.binder.configuration.commit.interval.ms=1000",
						"spring.cloud.stream.kafka.streams.binder.configuration.default.key.serde=org.apache.kafka.common.serialization.Serdes$StringSerde",
						"spring.cloud.stream.kafka.streams.binder.configuration.default.value.serde=org.apache.kafka.common.serialization.Serdes$StringSerde",
						"spring.cloud.stream.kafka.streams.timeWindow.length=5000",
						"spring.cloud.stream.kafka.streams.timeWindow.advanceBy=0",
						"spring.cloud.stream.bindings.input.consumer.useNativeDecoding=true"
				})
public class KafkaStreamsFunctionStubTest {

	@Test
	public void test() throws Exception {
		Thread.sleep(300_000);
	}

	@SpringBootApplication
	public static class StreamingFunctionApplication {

		@Bean
		public Function<KStream<Object, String>, KStream<Object, WordCount>> foobar()  {

			return w -> w
					.flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
					.map((key, value) -> new KeyValue<>(value, value))
					.groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
					.windowedBy(TimeWindows.of(30_000))
					.count(Materialized.as("foo-WordCounts"))
					.toStream()
					.map((key, value) -> new KeyValue<>(null, new WordCount(key.key(), value, new Date(key.window().start()), new Date(key.window().end()))));
		}
	}

	static class WordCount {

		private String word;

		private long count;

		private Date start;

		private Date end;

		WordCount(String word, long count, Date start, Date end) {
			this.word = word;
			this.count = count;
			this.start = start;
			this.end = end;
		}

		public String getWord() {
			return word;
		}

		public void setWord(String word) {
			this.word = word;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public Date getStart() {
			return start;
		}

		public void setStart(Date start) {
			this.start = start;
		}

		public Date getEnd() {
			return end;
		}

		public void setEnd(Date end) {
			this.end = end;
		}
	}
}
