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
 * Provides interface for gathering metrics.
 *
 * @param registry underlying metric registry
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
   * @return this
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
   * Gets counters matching supplied filter.
   *
   * @param filter name filter
   */
  def getCounters(filter: String => Boolean): Seq[Counter] =
    asScala(registry.getCounters(toMetricFilter(filter)))
      .map { (name, metric) => convert(name, metric) }
      .toSeq

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
   * @return this
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
   * Gets meters matching supplied filter.
   *
   * @param filter name filter
   */
  def getMeters(filter: String => Boolean): Seq[Meter] =
    asScala(registry.getMeters(toMetricFilter(filter)))
      .map { (name, metric) => convert(name, metric) }
      .toSeq

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
   * @return this
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
   * Gets histograms matching supplied filter.
   *
   * @param filter name filter
   */
  def getHistograms(filter: String => Boolean): Seq[Histogram] =
    asScala(registry.getHistograms(toMetricFilter(filter)))
      .map { (name, metric) => convert(name, metric) }
      .toSeq

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
   * @return this
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
   * Gets timers matching supplied filter.
   *
   * @param filter name filter
   */
  def getTimers(filter: String => Boolean): Seq[Timer] =
    asScala(registry.getTimers(toMetricFilter(filter)))
      .map { (name, metric) => convert(name, metric) }
      .toSeq

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
   *
   * @return this
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
   * Gets gauges matching supplied filter.
   *
   * @param filter name filter
   */
  def getGauges(filter: String => Boolean): Seq[Gauge[?]] =
    asScala(registry.getGauges(toMetricFilter(filter)))
      .map { (name, metric) => convert(name, metric) }
      .toSeq

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
   * @return this
   *
   * @note A counter is added to registry if it does not already exist.
   */
  def inc(name: String, count: Long = 1): this.type =
    registry.counter(name).inc(count)
    this

  /**
   * Increments counters matching supplied filter.
   *
   * @param count increment amount
   * @param filter name filter
   *
   * @return this
   */
  def inc(count: Long)(filter: String => Boolean): this.type =
    registry.getCounters(toMetricFilter(filter))
      .forEach { (_, counter) => counter.inc(count) }
    this

  /**
   * Decrements counter.
   *
   * @param name metric name
   * @param count decrement amount
   *
   * @return this
   *
   * @note A counter is added to registry if it does not already exist.
   */
  def dec(name: String, count: Long = 1): this.type =
    registry.counter(name).dec(count)
    this

  /**
   * Decrements counters matching supplied filter.
   *
   * @param count decrement amount
   * @param filter name filter
   *
   * @return this
   */
  def dec(count: Long)(filter: String => Boolean): this.type =
    registry.getCounters(toMetricFilter(filter))
      .forEach { (_, counter) => counter.dec(count) }
    this

  /**
   * Marks occurrences in meter.
   *
   * @param name metric name
   * @param count event count
   *
   * @return this
   *
   * @note A meter is added to registry if it does not already exist.
   */
  def mark(name: String, count: Long = 1): this.type =
    registry.meter(name).mark(count)
    this

  /**
   * Marks occurences in meters matching supplied filter.
   *
   * @param count event count
   * @param filter name filter
   *
   * @return this
   */
  def mark(count: Long)(filter: String => Boolean): this.type =
    registry.getMeters(toMetricFilter(filter))
      .forEach { (_, meter) => meter.mark(count) }
    this

  /**
   * Adds value to histogram.
   *
   * @param name metric name
   * @param value recorded value
   *
   * @note A histogram is added to registry if it does not already exist.
   *
   * @return this
   */
  def update(name: String, value: Long = 1): this.type =
    registry.histogram(name).update(value)
    this

  /**
   * Adds value to histograms matching supplied filter.
   *
   * @param value recorded value
   * @param filter name filter
   *
   * @return this
   */
  def update(value: Long)(filter: String => Boolean): this.type =
    registry.getHistograms(toMetricFilter(filter))
      .forEach { (_, histogram) => histogram.update(value) }
    this

  /**
   * Adds event to timer.
   *
   * @param name metric name
   * @param event timed event
   *
   * @return event
   *
   * @note A timer is added to registry if it does not already exist.
   */
  def time[T](name: String)(event: => T): T =
    val timer = registry.timer(name).time()
    try event finally timer.stop()

  /**
   * Gets filtered metrics.
   *
   * @param filter name filter
   *
   * @return filtered metrics
   */
  def filter(filter: String => Boolean): Metrics =
    val newRegistry = MetricRegistry()

    registry.getMetrics().forEach { (name, metric) =>
      if filter(name) then
        newRegistry.register(name, metric)
    }

    Metrics(newRegistry)

  /**
   * Copies metrics.
   *
   * @param metrics source metrics
   * @param prefix name prefix
   *
   * @return this
   *
   * @throws IllegalArgumentException &nbsp; if copied name already exists
   *
   * @note If `prefix` supplied, `"."` is added as separator, so copied name is
   * `prefix.name`.
   */
  def copy(metrics: Metrics, prefix: Option[String] = None): this.type =
    registry.registerAll(prefix.getOrElse(null), metrics.registry)
    this

  /**
   * Removes metric from registry.
   *
   * @param name metric name
   *
   * @return this
   */
  def remove(name: String): this.type =
    registry.remove(name)
    this

  /**
   * Removes metrics from registry matching supplied filter.
   *
   * @param filter name filter
   *
   * @return this
   */
  def remove(filter: String => Boolean): this.type =
    registry.removeMatching(toMetricFilter(filter))
    this

  /**
   * Removes all metrics from registry.
   *
   * @return this
   */
  def clear(): this.type =
    registry.removeMatching(MetricFilter.ALL)
    this

  /** Returns string representation. */
  override def toString(): String =
    var counters   = 0
    var meters     = 0
    var histograms = 0
    var timers     = 0
    var gauges     = 0

    registry.getMetrics().forEach {
      case (_, _: jmetrics.Counter  ) => counters += 1
      case (_, _: jmetrics.Meter    ) => meters += 1
      case (_, _: jmetrics.Histogram) => histograms += 1
      case (_, _: jmetrics.Timer    ) => timers += 1
      case (_, _: jmetrics.Gauge[?] ) => gauges += 1
    }

    StringBuilder()
      .append("Metrics(counters=")
      .append(counters)
      .append(",meters=")
      .append(meters)
      .append(",histograms=")
      .append(histograms)
      .append(",timers=")
      .append(timers)
      .append(",gauges=")
      .append(gauges)
      .append(")")
      .toString

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

  private def toMetricFilter(filter: String => Boolean): MetricFilter =
    (name, _) => filter(name)

  private def reject[T <: jmetrics.Metric]: MetricRegistry.MetricSupplier[T] =
    () => throw new NoSuchElementException
