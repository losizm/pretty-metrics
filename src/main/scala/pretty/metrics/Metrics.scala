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

import com.codahale.metrics as jmetrics

import scala.jdk.javaapi.CollectionConverters.asScala
import scala.collection.{ Set, SortedSet }

import jmetrics.{ MetricFilter, MetricRegistry }

/**
 * Provides simplified interface to gather metrics.
 *
 * @param registry underlying metric registry; defaults to `MetricRegistry()`
 */
class Metrics (val registry: MetricRegistry = MetricRegistry()):
  /** Gets all metric names. */
  def names: Set[String] =
    asScala(registry.getNames()).to(SortedSet)

  /** Gets all metrics. */
  def metrics: Seq[Metric] =
    asScala(registry.getMetrics()).collect {
      case (name, metric: jmetrics.Counter  ) => convert(name, metric)
      case (name, metric: jmetrics.Meter    ) => convert(name, metric)
      case (name, metric: jmetrics.Histogram) => convert(name, metric)
      case (name, metric: jmetrics.Timer    ) => convert(name, metric)
      case (name, metric: jmetrics.Gauge[?] ) => convert(name, metric)
    }.toSeq

  /**
   * Adds counter to registry.
   *
   * @param name metric name
   *
   * @see [[inc]], [[dec]]
   */
  def addCounter(name: String): this.type =
    registry.counter(name)
    this

  /**
   * Optionally gets counter.
   *
   * @param name metric name
   */
  def getCounter(name: String): Option[Counter] =
    try
      Some(registry.counter(name, reject))
        .map(metric => convert(name, metric))
    catch
      case _: IllegalArgumentException => None
      case _: NoSuchElementException   => None

  /**
   * Gets counter.
   *
   * @param name metric name
   *
   * @throws NoSuchElementException &nbsp; if counter does not exist
   */
  def counter(name: String): Counter =
    getCounter(name).get

  /** Gets all counters. */
  def counters: Seq[Counter] =
    asScala(registry.getCounters()).map { (name, metric) =>
      convert(name, metric)
    }.toSeq

  /**
   * Adds meter to registry.
   *
   * @param name metric name
   *
   * @see [[mark]]
   */
  def addMeter(name: String): this.type =
    registry.meter(name)
    this

  /**
   * Optionally gets meter.
   *
   * @param name metric name
   */
  def getMeter(name: String): Option[Meter] =
    try
      Some(registry.meter(name, reject))
        .map(metric => convert(name, metric))
    catch
      case _: IllegalArgumentException => None
      case _: NoSuchElementException   => None

  /**
   * Gets meter.
   *
   * @param name metric name
   *
   * @throws NoSuchElementException &nbsp; if meter does not exist
   */
  def meter(name: String): Meter =
    getMeter(name).get

  /** Gets all meters. */
  def meters: Seq[Meter] =
    asScala(registry.getMeters()).map { (name, metric) =>
      convert(name, metric)
    }.toSeq

  /**
   * Adds histogram to registry.
   *
   * @param name metric name
   *
   * @see [[update]]
   */
  def addHistogram(name: String): this.type =
    registry.histogram(name)
    this

  /**
   * Optionally gets histogram.
   *
   * @param name metric name
   */
  def getHistogram(name: String): Option[Histogram] =
    try
      Some(registry.histogram(name, reject))
        .map(metric => convert(name, metric))
    catch
      case _: IllegalArgumentException => None
      case _: NoSuchElementException   => None

  /**
   * Gets histogram.
   *
   * @param name metric name
   *
   * @throws NoSuchElementException &nbsp; if histogram does not exist
   */
  def histogram(name: String): Histogram =
    getHistogram(name).get

  /** Gets all histograms. */
  def histograms: Seq[Histogram] =
    asScala(registry.getHistograms()).map { (name, metric) =>
      convert(name, metric)
    }.toSeq

  /**
   * Adds timer to registry.
   *
   * @param name metric name
   *
   * @see [[time]]
   */
  def addTimer(name: String): this.type =
    registry.timer(name)
    this

  /**
   * Optionally gets timer.
   *
   * @param name metric name
   */
  def getTimer(name: String): Option[Timer] =
    try
      Some(registry.timer(name, reject))
        .map(metric => convert(name, metric))
    catch
      case _: IllegalArgumentException => None
      case _: NoSuchElementException   => None

  /**
   * Gets timer.
   *
   * @param name metric name
   *
   * @throws NoSuchElementException &nbsp; if timer does not exist
   */
  def timer(name: String): Timer =
    getTimer(name).get

  /** Gets all timers. */
  def timers: Seq[Timer] =
    asScala(registry.getTimers()).map { (name, metric) =>
      convert(name, metric)
    }.toSeq

  /**
   * Adds gauge to registry.
   *
   * @param name metric name
   * @param gauge gauging function
   */
  def addGauge[T](name: String)(gauge: () => T): this.type =
    registry.register(name, new jmetrics.Gauge[T] { def getValue() = gauge() })
    this

  /**
   * Optionally gets gauge.
   *
   * @param name metric name
   */
  def getGauge[T](name: String): Option[Gauge[T]] =
    try
      Some[jmetrics.Gauge[T]](registry.gauge(name, reject))
        .map(metric => convert(name, metric))
    catch
      case _: IllegalArgumentException => None
      case _: NoSuchElementException   => None

  /**
   * Gets gauge.
   *
   * @param name metric name
   *
   * @throws NoSuchElementException &nbsp; if gauge does not exist
   */
  def gauge[T](name: String): Gauge[T] =
    getGauge(name).get

  /** Gets all gauges. */
  def gauges: Seq[Gauge[?]] =
    asScala(registry.getGauges()).map { (name, metric) =>
      convert(name, metric)
    }.toSeq

  /**
   * Increments counter.
   *
   * @param name metric name
   * @param count increment amount
   *
   * @note A counter is added to registry if it does not already exist.
   */
  def inc(name: String, count: Long = 1): this.type =
    registry.counter(name).inc(count)
    this

  /**
   * Decrements counter.
   *
   * @param name metric name
   * @param count decrement amount
   *
   * @note A counter is added to registry if it does not already exist.
   */
  def dec(name: String, count: Long = 1): this.type =
    registry.counter(name).dec(count)
    this

  /**
   * Marks occurrences in meter.
   *
   * @param name metric name
   * @param count event count
   *
   * @note A meter is added to registry if it does not already exist.
   */
  def mark(name: String, count: Long = 1): this.type =
    registry.meter(name).mark(count)
    this

  /**
   * Adds value to histogram.
   *
   * @param name metric name
   * @param value recorded value
   *
   * @note A histogram is added to registry if it does not already exist.
   */
  def update(name: String, value: Long = 1): this.type =
    registry.histogram(name).update(value)
    this

  /**
   * Adds event to timer.
   *
   * @param name metric name
   * @param event timed event
   *
   * @note A timer is added to registry if it does not already exist.
   */
  def time[T](name: String)(event: => T): T =
    val timer = registry.timer(name).time()
    try event finally timer.stop()


  /**
   * Removes metric from registry.
   *
   * @param name metric name
   */
  def remove(name: String): this.type =
    registry.remove(name)
    this

  /** Removes all metrics from registry. */
  def reset(): this.type =
    registry.removeMatching(MetricFilter.ALL)
    this

  private def convert(name: String, metric: jmetrics.Counter): Counter =
    CounterImpl(name, metric.getCount)

  private def convert(name: String, metric: jmetrics.Meter): Meter =
    MeterImpl(name, toRates(metric))

  private def convert(name: String, metric: jmetrics.Histogram): Histogram =
    HistogramImpl(name, toStatistics(metric.getSnapshot))

  private def convert(name: String, metric: jmetrics.Timer): Timer =
    TimerImpl(name, toRates(metric), toStatistics(metric.getSnapshot))

  private def convert[T](name: String, metric: jmetrics.Gauge[T]): Gauge[T] =
    GaugeImpl(name, metric.getValue)

  private def toStatistics(snapshot: jmetrics.Snapshot): Statistics =
    StatisticsImpl(
      snapshot.size,
      snapshot.getMin,
      snapshot.getMax,
      snapshot.getMean,
      snapshot.getMedian,
      snapshot.getStdDev
    )

  private def toRates(metered: jmetrics.Metered): Rates =
    RatesImpl(
      metered.getCount,
      metered.getMeanRate,
      metered.getOneMinuteRate,
      metered.getFiveMinuteRate,
      metered.getFifteenMinuteRate
    )

  private def reject[T <: jmetrics.Metric]: MetricRegistry.MetricSupplier[T] =
    () => throw new NoSuchElementException
