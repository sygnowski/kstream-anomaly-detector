package io.s7i.temp.domain;

import io.s7i.temp.config.AnomalyConfig;
import io.s7i.temp.domain.calculator.FixedMeanCalculator;
import io.s7i.temp.model.TemperatureMeasurement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TemperatureReadingsTest implements FloatAssert {
    @Test
    void testAnomalyDetection() {
        //setup
        var anomalyConfig = new AnomalyConfig();
        anomalyConfig.setAvgThreshold(9);
        anomalyConfig.setDeviationThreshold(5);
        anomalyConfig.setMeanSize(10);

        var calc = new FixedMeanCalculator(anomalyConfig);
        //given
        var measurements = DoubleStream.of(
                        20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 27.1, 23.1
                )
                .mapToObj(temp -> TemperatureMeasurement.builder()
                        .temperature(temp)
                        .build())
                .toList();

        var anomalies = new ArrayList<TemperatureMeasurement>();
        var readings = new TemperatureReadings();

        // when
        for (var m : measurements) {
            calc.calcAnomaly(m, readings).whenAnomaly(anomalies::add);
        }
        //then

        assertTrue(() -> {
            var temp = anomalies.stream().findFirst().orElseThrow().temperature();
            return anomalies.size() == 1 && floatEquals(27.1, temp);
        });

    }


}