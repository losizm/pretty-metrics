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

/** Provides statistics. */
trait Statistics extends Count:
  /** Gets minimum. */
  def min: Long

  /** Gets maximum. */
  def max: Long

  /** Gets mean. */
  def mean: Double

  /** Gets median. */
  def median: Double

  /** Gets standard deviation. */
  def stdDev: Double

private case class StatisticsImpl(
  count:  Long,
  min:    Long,
  max:    Long,
  mean:   Double,
  median: Double,
  stdDev: Double
) extends Statistics:
  override lazy val toString =
    StringBuilder()
      .append("Statistics(count=")
      .append(count)
      .append(",min=")
      .append(min)
      .append(",max=")
      .append(max)
      .append(",mean=")
      .append(mean)
      .append(",median=")
      .append(median)
      .append(",stdDev=")
      .append(stdDev)
      .append(")")
      .toString
