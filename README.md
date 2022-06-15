# Pretty Metrics

[![Maven Central](https://img.shields.io/maven-central/v/com.github.losizm/pretty-metrics_3.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.losizm%22%20AND%20a:%22pretty-metrics_3%22)

A Scala veneer for [Metrics](https://metrics.dropwizard.io/).

The library provides a simplified interface for gathering application metrics.

## Getting Started

To get started, add **Pretty Metrics** to your project.

```scala
libraryDependencies += "com.github.losizm" %% "pretty-metrics" % "0.1.0"
```

And since [Metrics](https://metrics.dropwizard.io/) is used under the hood,
you'll need to add an implementation.

```scala
libraryDependencies += "io.dropwizard.metrics" % "metrics-core" % "4.2.9"
```

## A Few Examples

This first snippet shows how to manage a counter, meter, and histogram.

```scala
import pretty.metrics.Metrics

// Create metric registry
val metrics = Metrics()

// Increment and decrement counter
metrics.inc("app.counter")
metrics.inc("app.counter", 5)
metrics.dec("app.counter")
metrics.dec("app.counter", 2)

val counter = metrics.counter("app.counter")
printf("counter: count=%d%n", counter.count)

// Mark occurrences in meter
metrics.mark("app.meter")
metrics.mark("app.meter", 7)
metrics.mark("app.meter", 3)

val meter = metrics.meter("app.meter")
printf("meter: count=%d, meanRate=%.3f%n", meter.count, meter.meanRate)

// Record values in histogram
metrics.update("app.histogram", 85)
metrics.update("app.histogram", 32)
metrics.update("app.histogram", 77)
metrics.update("app.histogram", 15)
metrics.update("app.histogram", 32)

val histo = metrics.histogram("app.histogram")
printf("histogram: count=%d, min=%d, max=%d, median=%.3f, mean=%.3f%n",
    histo.count, histo.min, histo.max, histo.median, histo.mean)
```

Next is an example of gathering metrics using a timer.

```scala
def fib(n: Int): Int =
  require(n > 0)
  if n < 3 then 1
  else fib(n - 1) + fib(n - 2)

// Add event to timer
val answer1 = metrics.time("app.timer") { fib(10) }
assert(answer1 == 55)

// Add another event to timer
val answer2 = metrics.time("app.timer") { fib(42) }
assert(answer2 == 267914296)

val timer = metrics.timer("app.timer")
printf("timer: min: %d, max=%d, oneMinuteRate=%.3f%n",
    timer.min, timer.max, timer.oneMinuteRate)
```

To close things out, the following takes readings from a custom gauge.

```scala
// Add gauge to check JVM free memory
metrics.addGauge("app.gauge") { () =>
  Runtime.getRuntime().freeMemory() / 1024
}

val gauge1 = metrics.gauge("app.gauge")
println(s"There are ${gauge1.value} KiB of free memory.")

val buffer = new Array[Int](256 * 1024)

val gauge2 = metrics.gauge("app.gauge")
println(s"There are ${gauge2.value} KiB of free memory.")
```

## API Documentation

See [scaladoc](https://losizm.github.io/pretty-metrics/latest/api/index.html)
for additional details.

## License

**Pretty Metrics** is licensed under the Apache License, Version 2. See
[LICENSE](LICENSE) for more information.
