/*
 * Copyright 2021 HM Revenue & Customs
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

package views.deceased_settlor.mld5

import forms.CountryFormProvider
import models.NormalMode
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.SelectCountryViewBehaviours
import views.html.deceased_settlor.mld5.CountryOfNationalityView

class CountryOfNationalityViewSpec extends SelectCountryViewBehaviours {

  val prefix = "5mld.countryOfNationality"

  val form = new CountryFormProvider().withPrefix(prefix)

  "CountryOfNationality view" must {

    val view = viewFor[CountryOfNationalityView](Some(emptyUserAnswers))

    val name = FullName("First", None, "Last")

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, NormalMode, fakeDraftId, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like selectCountryPage(form, applyView, prefix, name.toString)

    behave like pageWithASubmitButton(applyView(form))
  }
}