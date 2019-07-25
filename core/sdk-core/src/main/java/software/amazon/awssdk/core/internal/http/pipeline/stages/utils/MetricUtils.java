/*
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package software.amazon.awssdk.core.internal.http.pipeline.stages.utils;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.metrics.meter.ConstantGauge;
import software.amazon.awssdk.metrics.meter.Counter;
import software.amazon.awssdk.metrics.meter.Gauge;
import software.amazon.awssdk.metrics.meter.NoOpCounter;
import software.amazon.awssdk.metrics.meter.NoOpGauge;
import software.amazon.awssdk.metrics.meter.NoOpTimer;
import software.amazon.awssdk.metrics.meter.Timer;
import software.amazon.awssdk.metrics.metrics.SdkDefaultMetric;
import software.amazon.awssdk.metrics.registry.MetricBuilderParams;
import software.amazon.awssdk.metrics.registry.MetricRegistry;
import software.amazon.awssdk.metrics.registry.NoOpMetricRegistry;

/**
 * Helper class to register metrics in the {@link MetricRegistry} instances.
 */
@SdkInternalApi
public final class MetricUtils {

    private MetricUtils() {
    }

    /**
     * Register a {@link Timer} in the #metricRegistry using the information in given {@link SdkDefaultMetric}.
     * If there is already a metric registered with {@link SdkDefaultMetric#name()}, the existing {@link Timer} instance is
     * returned.
     */
    public static Timer timer(MetricRegistry metricRegistry, SdkDefaultMetric metric) {
        if (metricRegistry instanceof NoOpMetricRegistry) {
            return NoOpTimer.instance();
        }

        return metricRegistry.timer(metric.name(), metricBuilderParams(metric));
    }

    /**
     * Register a {@link Counter} in the #metricRegistry using the information in given {@link SdkDefaultMetric}.
     * If there is already a metric registered with {@link SdkDefaultMetric#name()}, the existing {@link Counter} instance is
     * returned.
     */
    public static Counter counter(MetricRegistry metricRegistry, SdkDefaultMetric metric) {
        if (metricRegistry instanceof NoOpMetricRegistry) {
            return NoOpCounter.instance();
        }

        return metricRegistry.counter(metric.name(), metricBuilderParams(metric));
    }

    /**
     * Register a {@link ConstantGauge} in the #metricRegistry. Throws an error if there is already a metric registered with
     * same {@link SdkDefaultMetric#name()}.
     */
    public static <T> Gauge<T> registerConstantGauge(T value, MetricRegistry metricRegistry, SdkDefaultMetric metric) {
        if (metricRegistry instanceof NoOpMetricRegistry) {
            return NoOpGauge.instance();
        }

        return (ConstantGauge<T>) metricRegistry.register(metric.name(), ConstantGauge.builder()
                                                                                          .value(value)
                                                                                          .categories(metric.categories())
                                                                                          .build());
    }

    private static MetricBuilderParams metricBuilderParams(SdkDefaultMetric metric) {
        return MetricBuilderParams.builder()
                                  .categories(metric.categories())
                                  .build();
    }
}
