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

package controllers

import base.SpecBase
import controllers.living_settlor.individual.routes._
import controllers.routes._
import forms.{AddASettlorFormProvider, YesNoFormProvider}
import models.pages.IndividualOrBusiness.Individual
import models.pages.KindOfTrust.Intervivos
import models.pages.{AddASettlor, FullName}
import models.{NormalMode, UserAnswers}
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.individual.SettlorIndividualNamePage
import pages.trust_type.KindOfTrustPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.{AddASettlorView, AddASettlorYesNoView}

class AddASettlorControllerSpec extends SpecBase {

  lazy val getRoute : String = routes.AddASettlorController.onPageLoad(fakeDraftId).url
  lazy val submitAnotherRoute : String = routes.AddASettlorController.submitAnother(fakeDraftId).url
  lazy val submitYesNoRoute : String = routes.AddASettlorController.submitOne(fakeDraftId).url

  val yesNoForm: Form[Boolean] = new YesNoFormProvider().withPrefix("addASettlorYesNo")
  val addSettlorForm = new AddASettlorFormProvider()()

  val hint = "addASettlor.Lifetime"

  val userAnswersWithSettlorsComplete: UserAnswers = emptyUserAnswers
    .set(KindOfTrustPage, Intervivos).success.value

  "AddASettlor Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, getRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(POST, submitAnotherRoute)
          .withFormUrlEncodedBody(("value", AddASettlor.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "no settlors" must {

      "return OK and the correct view for a GET" when {

        "taxable" in {

          val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

          val request = FakeRequest(GET, getRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AddASettlorYesNoView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(yesNoForm, NormalMode, fakeDraftId, Some(hint))(request, messages).toString

          application.stop()
        }

        "non-taxable" in {

          val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

          val request = FakeRequest(GET, getRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AddASettlorYesNoView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(yesNoForm, NormalMode, fakeDraftId, None)(request, messages).toString

          application.stop()
        }
      }

      "redirect to the next page when valid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request = FakeRequest(POST, submitYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request = FakeRequest(POST, submitYesNoRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = yesNoForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddASettlorYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, Some(hint))(request, messages).toString

        application.stop()
      }
    }

    "there are settlors" must {

      "return OK and the correct view for a GET" when {

        val name = FullName("Joe", None, "Bloggs")
        val index = 0

        val settlors = List(
          AddRow(
            name.toString,
            "Individual Settlor",
            SettlorIndividualNameController.onPageLoad(NormalMode, index, fakeDraftId).url,
            RemoveSettlorYesNoController.onPageLoad(index, fakeDraftId).url
          )
        )

        "taxable" in {

          val answers = userAnswersWithSettlorsComplete
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, getRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AddASettlorView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(addSettlorForm, NormalMode, fakeDraftId, settlors, Nil, heading = "Add a settlor", Some(hint))(request, messages).toString

          application.stop()
        }

        "non-taxable" in {

          val answers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value

          val application = applicationBuilder(userAnswers = Some(answers)).build()

          val request = FakeRequest(GET, getRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[AddASettlorView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(addSettlorForm, NormalMode, fakeDraftId, settlors, Nil, heading = "Add a settlor", None)(request, messages).toString

          application.stop()
        }
      }

      "redirect to the next page when valid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request = FakeRequest(POST, submitAnotherRoute)
          .withFormUrlEncodedBody(("value", AddASettlor.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithSettlorsComplete)).build()

        val request = FakeRequest(POST, submitAnotherRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = addSettlorForm.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddASettlorView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(
            boundForm,
            NormalMode,
            fakeDraftId,
            Nil,
            Nil,
            heading = "Add a settlor",
            Some(hint)
          )(request, messages).toString

        application.stop()
      }

    }

  }
}
