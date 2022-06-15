/*
 * Copyright 2022 Carlos Conyers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pretty.metrics

/** Provides metric. */
sealed trait Metric:
  /** Gets name. */
  def name: String

/**
 * Provides counter metric.
 *
 * @see [[Metrics.addCounter]], [[Metrics.inc]], [[Metrics.dec]]
 */
sealed trait Counter extends Metric with Count

/**
 * Provides meter metric.
 *
 * @see [[Metrics.addMeter]], [[Metrics.mark]]
 */
sealed trait Meter extends Metric with Rates

/**
 * Provides histogram metric.
 *
 * @see [[Metrics.addHistogram]], [[Metrics.update]]
 */
sealed trait Histogram extends Metric with Statistics

/**
 * Provides timer metric.
 *
 * @see [[Metrics.addTimer]], [[Metrics.time]]
 */
sealed trait Timer extends Metric with Rates with Statistics

/**
 * Provides gauge metric.
 *
 * @see [[Metrics.addGauge]]
 */
sealed trait Gauge[T] extends Metric:
  /** Gets value. */
  def value: T

private case class CounterImpl(name: String, count: Long) extends Counter:
  override lazy val toString = s"Counter(name=$name,count=$count)"

private case class MeterImpl(name: String, rates: Rates) extends Meter:
  export rates.*
  override lazy val toString = s"Meter(name=$name,$rates)"

private case class HistogramImpl(name: String, stats: Statistics) extends Histogram:
  export stats.*
  override lazy val toString = s"Histogram(name=$name,$stats)"

private case class TimerImpl(name: String, rates: Rates, stats: Statistics) extends Timer:
  export rates.*
  export stats.{ min, max, mean, median, stdDev }
  override lazy val toString = s"Timer(name=$name,$rates,$stats)"

private case class GaugeImpl[T](name: String, value: T) extends Gauge[T]:
  override lazy val toString = s"Gauge(name=$name,value=$value)"
