package com.t212.tickers.producer.broker;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;

public class InstrumentPartitioner implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        String instrumentType = key.toString().split("-")[0];
        switch (instrumentType) {
            case "stock":
                return 0;
            case "index":
                return 1;
            case "crypto":
                return 2;
            case "currency":
                return 3;
            case "commodities":
                return 4;
            default:
                return 2;
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}



