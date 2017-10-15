package cz.fb.manaus.core.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class MetricsService {

    @Autowired
    private MetricRegistry registry;

    public List<MetricRecord<?>> getCollectedMetrics(String prefix) {
        Stream<MetricRecord<?>> counters = registry.getCounters().entrySet().stream()
                .flatMap(e -> getCounterMetricRecords(e.getKey(), e.getValue()));
        Stream<MetricRecord<?>> meters = registry.getMeters().entrySet().stream()
                .flatMap(e -> getMeterMetricRecords(e.getKey(), e.getValue()));
        return Stream.concat(counters, meters)
                .filter(record -> record.getName().startsWith(prefix))
                .sorted(Comparator.comparing(MetricRecord::getName))
                .collect(Collectors.toList());
    }

    private Stream<MetricRecord<?>> getMeterMetricRecords(String name, Meter meter) {
        return Stream.of(
                new MetricRecord<>(name + ".count", meter.getCount()),
                new MetricRecord<>(name + ".rate15", meter.getFifteenMinuteRate()),
                new MetricRecord<>(name + ".rate5", meter.getFiveMinuteRate()),
                new MetricRecord<>(name + ".rate1", meter.getOneMinuteRate()),
                new MetricRecord<>(name + ".meanRate", meter.getMeanRate()));
    }

    private Stream<MetricRecord<?>> getCounterMetricRecords(String name, Counter counter) {
        return Stream.of(new MetricRecord<>(name, counter.getCount()));
    }
}