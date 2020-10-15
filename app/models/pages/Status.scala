/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.pages

import models.{Enumerable, WithName}

sealed trait Status

object Status extends Enumerable.Implicits {

  case object Completed extends WithName("completed") with Status

  case object InProgress extends WithName("progress") with Status

  val values: Set[Status] = Set(
    Completed, InProgress
  )

  implicit val enumerable: Enumerable[Status] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
