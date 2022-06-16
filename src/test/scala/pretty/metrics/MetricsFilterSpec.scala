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

class MetricsFilterSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val metrics = Metrics()

  private def prefix(n: Int) = (name: String) => {
    name.startsWith(s"$n.")
  }

  private def suffix(n: Int) = (name: String) => {
    name.endsWith(s".$n")
  }

  it should "filter counters" in {
    metrics.addCounter("1.counter.1")
    metrics.addCounter("1.counter.2")
    metrics.addCounter("1.counter.3")

    metrics.addCounter("2.counter.1")
    metrics.addCounter("2.counter.2")
    metrics.addCounter("2.counter.3")

    metrics.addCounter("3.counter.1")
    metrics.addCounter("3.counter.2")
    metrics.addCounter("3.counter.3")

    metrics.inc(10)(prefix(1))
    metrics.inc(20)(prefix(2))
    metrics.inc(30)(prefix(3))

    val prefix1 = metrics.getCounters(prefix(1))
    assert(prefix1.map(_.name)  == Seq("1.counter.1", "1.counter.2", "1.counter.3"))
    assert(prefix1.map(_.count) == Seq(10, 10, 10))

    val prefix2 = metrics.getCounters(prefix(2))
    assert(prefix2.map(_.name) == Seq("2.counter.1", "2.counter.2", "2.counter.3"))
    assert(prefix2.map(_.count) == Seq(20, 20, 20))

    val prefix3 = metrics.getCounters(prefix(3))
    assert(prefix3.map(_.name) == Seq("3.counter.1", "3.counter.2", "3.counter.3"))
    assert(prefix3.map(_.count) == Seq(30, 30, 30))

    metrics.dec(1)(suffix(1))
    metrics.dec(2)(suffix(2))
    metrics.dec(3)(suffix(3))

    val suffix1 = metrics.getCounters(suffix(1))
    assert(suffix1.map(_.name)  == Seq("1.counter.1", "2.counter.1", "3.counter.1"))
    assert(suffix1.map(_.count) == Seq(9, 19, 29))

    val suffix2 = metrics.getCounters(suffix(2))
    assert(suffix2.map(_.name) == Seq("1.counter.2", "2.counter.2", "3.counter.2"))
    assert(suffix2.map(_.count) == Seq(8, 18, 28))

    val suffix3 = metrics.getCounters(suffix(3))
    assert(suffix3.map(_.name) == Seq("1.counter.3", "2.counter.3", "3.counter.3"))
    assert(suffix3.map(_.count) == Seq(7, 17, 27))
  }

  it should "filter meters" in {
    metrics.addMeter("1.meter.1")
    metrics.addMeter("1.meter.2")
    metrics.addMeter("1.meter.3")

    metrics.addMeter("2.meter.1")
    metrics.addMeter("2.meter.2")
    metrics.addMeter("2.meter.3")

    metrics.addMeter("3.meter.1")
    metrics.addMeter("3.meter.2")
    metrics.addMeter("3.meter.3")

    metrics.mark(10)(prefix(1))
    metrics.mark(20)(prefix(2))
    metrics.mark(30)(prefix(3))

    val prefix1 = metrics.getMeters(prefix(1))
    assert(prefix1.map(_.name)  == Seq("1.meter.1", "1.meter.2", "1.meter.3"))
    assert(prefix1.map(_.count) == Seq(10, 10, 10))

    val prefix2 = metrics.getMeters(prefix(2))
    assert(prefix2.map(_.name) == Seq("2.meter.1", "2.meter.2", "2.meter.3"))
    assert(prefix2.map(_.count) == Seq(20, 20, 20))

    val prefix3 = metrics.getMeters(prefix(3))
    assert(prefix3.map(_.name) == Seq("3.meter.1", "3.meter.2", "3.meter.3"))
    assert(prefix3.map(_.count) == Seq(30, 30, 30))

    metrics.mark(-1)(suffix(1))
    metrics.mark(-2)(suffix(2))
    metrics.mark(-3)(suffix(3))

    val suffix1 = metrics.getMeters(suffix(1))
    assert(suffix1.map(_.name)  == Seq("1.meter.1", "2.meter.1", "3.meter.1"))
    assert(suffix1.map(_.count) == Seq(9, 19, 29))

    val suffix2 = metrics.getMeters(suffix(2))
    assert(suffix2.map(_.name) == Seq("1.meter.2", "2.meter.2", "3.meter.2"))
    assert(suffix2.map(_.count) == Seq(8, 18, 28))

    val suffix3 = metrics.getMeters(suffix(3))
    assert(suffix3.map(_.name) == Seq("1.meter.3", "2.meter.3", "3.meter.3"))
    assert(suffix3.map(_.count) == Seq(7, 17, 27))
  }

  it should "filter histograms" in {
    metrics.addHistogram("1.histogram.1")
    metrics.addHistogram("1.histogram.2")
    metrics.addHistogram("1.histogram.3")

    metrics.addHistogram("2.histogram.1")
    metrics.addHistogram("2.histogram.2")
    metrics.addHistogram("2.histogram.3")

    metrics.addHistogram("3.histogram.1")
    metrics.addHistogram("3.histogram.2")
    metrics.addHistogram("3.histogram.3")

    metrics.update(10)(prefix(1))
    metrics.update(20)(prefix(2))
    metrics.update(30)(prefix(3))

    val prefix1 = metrics.getHistograms(prefix(1))
    assert(prefix1.map(_.name)  == Seq("1.histogram.1", "1.histogram.2", "1.histogram.3"))
    assert(prefix1.map(_.min) == Seq(10, 10, 10))

    val prefix2 = metrics.getHistograms(prefix(2))
    assert(prefix2.map(_.name) == Seq("2.histogram.1", "2.histogram.2", "2.histogram.3"))
    assert(prefix2.map(_.min) == Seq(20, 20, 20))

    val prefix3 = metrics.getHistograms(prefix(3))
    assert(prefix3.map(_.name) == Seq("3.histogram.1", "3.histogram.2", "3.histogram.3"))
    assert(prefix3.map(_.min) == Seq(30, 30, 30))

    metrics.update(1)(suffix(1))
    metrics.update(2)(suffix(2))
    metrics.update(3)(suffix(3))

    val suffix1 = metrics.getHistograms(suffix(1))
    assert(suffix1.map(_.name)  == Seq("1.histogram.1", "2.histogram.1", "3.histogram.1"))
    assert(suffix1.map(_.min) == Seq(1, 1, 1))

    val suffix2 = metrics.getHistograms(suffix(2))
    assert(suffix2.map(_.name) == Seq("1.histogram.2", "2.histogram.2", "3.histogram.2"))
    assert(suffix2.map(_.min) == Seq(2, 2, 2))

    val suffix3 = metrics.getHistograms(suffix(3))
    assert(suffix3.map(_.name) == Seq("1.histogram.3", "2.histogram.3", "3.histogram.3"))
    assert(suffix3.map(_.min) == Seq(3, 3, 3))
  }

  it should "filter timers" in {
    metrics.addTimer("1.timer.1")
    metrics.addTimer("1.timer.2")
    metrics.addTimer("1.timer.3")

    metrics.addTimer("2.timer.1")
    metrics.addTimer("2.timer.2")
    metrics.addTimer("2.timer.3")

    metrics.addTimer("3.timer.1")
    metrics.addTimer("3.timer.2")
    metrics.addTimer("3.timer.3")

    val prefix1 = metrics.getTimers(prefix(1))
    assert(prefix1.map(_.name)  == Seq("1.timer.1", "1.timer.2", "1.timer.3"))

    val prefix2 = metrics.getTimers(prefix(2))
    assert(prefix2.map(_.name) == Seq("2.timer.1", "2.timer.2", "2.timer.3"))

    val prefix3 = metrics.getTimers(prefix(3))
    assert(prefix3.map(_.name) == Seq("3.timer.1", "3.timer.2", "3.timer.3"))

    val suffix1 = metrics.getTimers(suffix(1))
    assert(suffix1.map(_.name)  == Seq("1.timer.1", "2.timer.1", "3.timer.1"))

    val suffix2 = metrics.getTimers(suffix(2))
    assert(suffix2.map(_.name) == Seq("1.timer.2", "2.timer.2", "3.timer.2"))

    val suffix3 = metrics.getTimers(suffix(3))
    assert(suffix3.map(_.name) == Seq("1.timer.3", "2.timer.3", "3.timer.3"))
  }

  it should "filter gauges" in {
    metrics.addGauge("1.gauge.1") { () => 0 }
    metrics.addGauge("1.gauge.2") { () => 0 }
    metrics.addGauge("1.gauge.3") { () => 0 }

    metrics.addGauge("2.gauge.1") { () => 0 }
    metrics.addGauge("2.gauge.2") { () => 0 }
    metrics.addGauge("2.gauge.3") { () => 0 }

    metrics.addGauge("3.gauge.1") { () => 0 }
    metrics.addGauge("3.gauge.2") { () => 0 }
    metrics.addGauge("3.gauge.3") { () => 0 }

    val prefix1 = metrics.getGauges(prefix(1))
    assert(prefix1.map(_.name)  == Seq("1.gauge.1", "1.gauge.2", "1.gauge.3"))

    val prefix2 = metrics.getGauges(prefix(2))
    assert(prefix2.map(_.name) == Seq("2.gauge.1", "2.gauge.2", "2.gauge.3"))

    val prefix3 = metrics.getGauges(prefix(3))
    assert(prefix3.map(_.name) == Seq("3.gauge.1", "3.gauge.2", "3.gauge.3"))

    val suffix1 = metrics.getGauges(suffix(1))
    assert(suffix1.map(_.name)  == Seq("1.gauge.1", "2.gauge.1", "3.gauge.1"))

    val suffix2 = metrics.getGauges(suffix(2))
    assert(suffix2.map(_.name) == Seq("1.gauge.2", "2.gauge.2", "3.gauge.2"))

    val suffix3 = metrics.getGauges(suffix(3))
    assert(suffix3.map(_.name) == Seq("1.gauge.3", "2.gauge.3", "3.gauge.3"))
  }

  it should "remove metrics by filter" in {
    metrics.remove(prefix(1)).remove(suffix(2))

    assert(metrics.metrics.size == 20)

    assert(metrics.counters.map(_.name)   == Seq("2.counter.1",   "2.counter.3",   "3.counter.1",   "3.counter.3"))
    assert(metrics.meters.map(_.name)     == Seq("2.meter.1",     "2.meter.3",     "3.meter.1",     "3.meter.3"))
    assert(metrics.histograms.map(_.name) == Seq("2.histogram.1", "2.histogram.3", "3.histogram.1", "3.histogram.3"))
    assert(metrics.timers.map(_.name)     == Seq("2.timer.1",     "2.timer.3",     "3.timer.1",     "3.timer.3"))
    assert(metrics.gauges.map(_.name)     == Seq("2.gauge.1",     "2.gauge.3",     "3.gauge.1",     "3.gauge.3"))
  }

  it should "filter metrics" in {
    val newMetrics = metrics.filter(suffix(3))
    assert(metrics.metrics.size == 20)
    assert(newMetrics.metrics.size == 10)
    assert(newMetrics.names == Set("2.counter.3", "2.meter.3", "2.histogram.3", "2.timer.3", "2.gauge.3",
                                   "3.counter.3", "3.meter.3", "3.histogram.3", "3.timer.3", "3.gauge.3"))
  }
