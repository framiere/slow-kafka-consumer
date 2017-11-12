package com.github.framiere;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SlowKafkaConsumer {

    public static class CommandLine {
        @Parameter(names = "--bootstrap-servers", required = true)
        private String bootstrapServers = "localhost:9092";
        @Parameter(names = "--group", required = true)
        private String group = "slow-consumer";
        @Parameter(names = "--topics", required = true)
        private List<String> topics;
        @Parameter(names = "--timebetween-batches")
        private Integer maxWaitTimeBetweenBatches = 30;
        @Parameter(names = "--timebetween-batches-unit")
        private TimeUnit maxWaitTimeBetweenBatchesUnit = TimeUnit.SECONDS;

        @Override
        public String toString() {
            return "CommandLine{" +
                    "bootstrapServers='" + bootstrapServers + '\'' +
                    ", groupId='" + group + '\'' +
                    ", topics=" + topics +
                    ", timeBetweenBatches=" + maxWaitTimeBetweenBatches +
                    ", timeBetweenBatchesUnit=" + maxWaitTimeBetweenBatchesUnit +
                    '}';
        }
    }

    public static void main(String args[]) throws InterruptedException {
        Random random = new Random();
        CommandLine commandLine = new CommandLine();
        JCommander.newBuilder()
                .addObject(commandLine)
                .build()
                .parse(args);
        System.out.println(commandLine.toString());
        Properties props = new Properties();
        props.put("bootstrap.servers", commandLine.bootstrapServers);
        props.put("group.id", commandLine.group);
        props.put("enable.auto.commit", "false");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(commandLine.topics);
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset=%d, key=%s, value=%s", record.offset(), record.key(), record.value());
            }
            int timeout = random.nextInt(commandLine.maxWaitTimeBetweenBatches);
            System.out.println("Waiting for " + timeout + " " + commandLine.maxWaitTimeBetweenBatchesUnit);
            commandLine.maxWaitTimeBetweenBatchesUnit.sleep(timeout);
            consumer.commitSync();
        }
    }
}
