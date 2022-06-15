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

/** Provides rates. */
trait Rates extends Count:
  /** Gets mean rate. */
  def meanRate: Double

  /** Gets one-minute rate. */
  def oneMinuteRate: Double

  /** Gets five-minute rate. */
  def fiveMinuteRate: Double

  /** Gets fifteen-minute rate. */
  def fifteenMinuteRate: Double

private case class RatesImpl(
  count:             Long,
  meanRate:          Double,
  oneMinuteRate:     Double,
  fiveMinuteRate:    Double,
  fifteenMinuteRate: Double
) extends Rates:
  override lazy val toString =
    StringBuilder()
      .append("Rates(count=")
      .append(count)
      .append(",meanRate=")
      .append(meanRate)
      .append(",oneMinuteRate=")
      .append(oneMinuteRate)
      .append(",fiveMinuteRate=")
      .append(fiveMinuteRate)
      .append(",fifteenMinuteRate=")
      .append(fifteenMinuteRate)
      .append(")")
      .toString
