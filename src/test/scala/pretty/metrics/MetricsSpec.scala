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

class MetricsSpec extends org.scalatest.flatspec.AnyFlatSpec:
  private val metrics = Metrics()

  it should "increment and decrement counter" in {
    val name   = "counter1"
    var metric = metrics.inc(name).counter(name)

    assert(metric.name == name)
    assert(metric.count == 1)

    metric = metrics.inc(name, 3).counter(name)
    assert(metric.name == name)
    assert(metric.count == 4)

    metric = metrics.dec(name).counter(name)
    assert(metric.name == name)
    assert(metric.count == 3)

    metric = metrics.dec(name, 2).counter(name)
    assert(metric.name == name)
    assert(metric.count == 1)

    assert(metrics.toString == "Metrics(counters=1,meters=0,histograms=0,timers=0,gauges=0)")
  }

  it should "mark occurences in meter" in {
    val name   = "meter1"
    var metric = metrics.mark(name).meter(name)

    assert(metric.name == name)
    assert(metric.count == 1)

    metric = metrics.mark(name, 3).meter(name)
    assert(metric.name == name)
    assert(metric.count == 4)

    assert(metrics.toString == "Metrics(counters=1,meters=1,histograms=0,timers=0,gauges=0)")
  }

  it should "add values to histogram" in {
    val name   = "histogram1"
    var metric = metrics.update(name, 100).histogram(name)

    assert(metric.name == name)
    assert(metric.count == 1)
    assert(metric.min == 100)
    assert(metric.max == 100)
  
    metric = metrics.update(name, 50).histogram(name)
    assert(metric.name == name)
    assert(metric.count == 2)
    assert(metric.min == 50)
    assert(metric.max == 100)

    assert(metrics.toString == "Metrics(counters=1,meters=1,histograms=1,timers=0,gauges=0)")
  }

  it should "add events to timer" in {
    val name   = "timer1"
    var result = metrics.time(name)("Hello, world!")
    var metric = metrics.timer(name)

    assert(result == "Hello, world!")
    assert(metric.name == name)
    assert(metric.count == 1)

    result = metrics.time(name)("Goodbye, cruel world!")
    metric = metrics.timer(name)
    assert(result == "Goodbye, cruel world!")
    assert(metric.name == name)
    assert(metric.count == 2)

    assert(metrics.toString == "Metrics(counters=1,meters=1,histograms=1,timers=1,gauges=0)")
  }

  it should "take readings from gauge" in {
    val name   = "gauge1"
    val values = Seq(1000, 2000, 3000)
    var index  = 0

    metrics.addGauge(name) { () =>
      val value = values(index % values.size)
      index += 1
      value
    }

    var metric = metrics.getGauge[Int](name).get
    assert(metric.name == name)
    assert(metric.value == 1000)

    metric = metrics.getGauge[Int](name).get
    assert(metric.name == name)
    assert(metric.value == 2000)

    metric = metrics.getGauge[Int](name).get
    assert(metric.name == name)
    assert(metric.value == 3000)

    assert(metrics.toString == "Metrics(counters=1,meters=1,histograms=1,timers=1,gauges=1)")
  }

  it should "get all metric names" in {
    val names = metrics.names
    assert(names.size == 5)
    assert(names.contains("counter1"))
    assert(names.contains("meter1"))
    assert(names.contains("histogram1"))
    assert(names.contains("timer1"))
    assert(names.contains("gauge1"))
  }

  it should "get all counters" in {
    assert(metrics.counters.size == 1)
    metrics.inc("counter2")
    assert(metrics.counters.size == 2)
    assert(metrics.toString == "Metrics(counters=2,meters=1,histograms=1,timers=1,gauges=1)")
  }

  it should "get all meters" in {
    assert(metrics.meters.size == 1)
    metrics.mark("meter2")
    assert(metrics.meters.size == 2)
    assert(metrics.toString == "Metrics(counters=2,meters=2,histograms=1,timers=1,gauges=1)")
  }

  it should "get all histograms" in {
    assert(metrics.histograms.size == 1)
    metrics.update("histogram2", 0)
    assert(metrics.histograms.size == 2)
    assert(metrics.toString == "Metrics(counters=2,meters=2,histograms=2,timers=1,gauges=1)")
  }

  it should "get all timers" in {
    assert(metrics.timers.size == 1)
    metrics.time("timer2")("Hello, world!")
    assert(metrics.timers.size == 2)
    assert(metrics.toString == "Metrics(counters=2,meters=2,histograms=2,timers=2,gauges=1)")
  }

  it should "get all gauges" in {
    assert(metrics.gauges.size == 1)
    metrics.addGauge("gauge2") { () => true }
    assert(metrics.gauges.size == 2)
    assert(metrics.toString == "Metrics(counters=2,meters=2,histograms=2,timers=2,gauges=2)")
  }

  it should "get all metrics" in {
    val all = metrics.metrics
    assert(all.size == 10)

    val kind = """(\w+?)\d""".r
    all.foreach { metric =>
      info(s"$metric")
      metric.name match
        case kind("counter")   => assert(metric.isInstanceOf[Counter])
        case kind("meter")     => assert(metric.isInstanceOf[Meter])
        case kind("histogram") => assert(metric.isInstanceOf[Histogram])
        case kind("timer")     => assert(metric.isInstanceOf[Timer])
        case kind("gauge")     => assert(metric.isInstanceOf[Gauge[?]])
        case _                 => assert(false)
    }
  }

  it should "not apply wrong metric" in {
    var name = "counter1"
    assertThrows[IllegalArgumentException](metrics.mark(name))
    assertThrows[IllegalArgumentException](metrics.update(name))
    assertThrows[IllegalArgumentException](metrics.time(name)(None))
    assertThrows[IllegalArgumentException](metrics.addGauge(name) {() => false })

    name = "meter1"
    assertThrows[IllegalArgumentException](metrics.inc(name))
    assertThrows[IllegalArgumentException](metrics.dec(name))
    assertThrows[IllegalArgumentException](metrics.update(name))
    assertThrows[IllegalArgumentException](metrics.time(name)(None))
    assertThrows[IllegalArgumentException](metrics.addGauge(name) {() => false })

    name = "histogram1"
    assertThrows[IllegalArgumentException](metrics.inc(name))
    assertThrows[IllegalArgumentException](metrics.dec(name))
    assertThrows[IllegalArgumentException](metrics.mark(name))
    assertThrows[IllegalArgumentException](metrics.time(name)(None))
    assertThrows[IllegalArgumentException](metrics.addGauge(name) {() => false })

    name = "timer1"
    assertThrows[IllegalArgumentException](metrics.inc(name))
    assertThrows[IllegalArgumentException](metrics.dec(name))
    assertThrows[IllegalArgumentException](metrics.mark(name))
    assertThrows[IllegalArgumentException](metrics.update(name))
    assertThrows[IllegalArgumentException](metrics.addGauge(name) {() => false })

    name = "gauge1"
    assertThrows[IllegalArgumentException](metrics.inc(name))
    assertThrows[IllegalArgumentException](metrics.dec(name))
    assertThrows[IllegalArgumentException](metrics.mark(name))
    assertThrows[IllegalArgumentException](metrics.update(name))
    assertThrows[IllegalArgumentException](metrics.time(name)(None))

    assert(metrics.toString == "Metrics(counters=2,meters=2,histograms=2,timers=2,gauges=2)")
  }

  it should "remove metrics" in {
    assert(metrics.remove("counter2").metrics.size == 9)
    assert(metrics.remove("meter2").metrics.size == 8)
    assert(metrics.remove("histogram2").metrics.size == 7)
    assert(metrics.remove("timer2").metrics.size == 6)
    assert(metrics.remove("gauge2").metrics.size == 5)

    assert(metrics.counters.size == 1)
    assert(metrics.meters.size == 1)
    assert(metrics.histograms.size == 1)
    assert(metrics.timers.size == 1)
    assert(metrics.gauges.size == 1)

    assert(metrics.toString == "Metrics(counters=1,meters=1,histograms=1,timers=1,gauges=1)")
  }

  it should "reset metrics" in {
    assert(metrics.reset().metrics.isEmpty)
    assert(metrics.toString == "Metrics(counters=0,meters=0,histograms=0,timers=0,gauges=0)")
  }

  it should "add metrics" in {
    metrics.addCounter("counter1")
    assert(metrics.metrics.size == 1)
    assert(metrics.counters.size == 1)
    val counter = metrics.getCounter("counter1").get
    assert(counter.count == 0)

    metrics.addMeter("meter1")
    assert(metrics.metrics.size == 2)
    assert(metrics.meters.size == 1)
    val meter = metrics.getMeter("meter1").get
    assert(meter.count == 0)

    metrics.addHistogram("histogram1")
    assert(metrics.metrics.size == 3)
    assert(metrics.histograms.size == 1)
    val histogram = metrics.getHistogram("histogram1").get
    assert(histogram.count == 0)

    metrics.addTimer("timer1")
    assert(metrics.metrics.size == 4)
    assert(metrics.timers.size == 1)
    val timer = metrics.getTimer("timer1").get
    assert(timer.count == 0)

    metrics.addGauge("gauge1") { () => 0 }
    assert(metrics.metrics.size == 5)
    assert(metrics.gauges.size == 1)
    val gauge = metrics.getGauge[Int]("gauge1").get
    assert(gauge.value == 0)

    assert(metrics.toString == "Metrics(counters=1,meters=1,histograms=1,timers=1,gauges=1)")
  }
