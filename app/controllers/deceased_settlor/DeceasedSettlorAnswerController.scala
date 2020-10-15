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

package controllers.deceased_settlor

import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import javax.inject.Inject
import models.NormalMode
import models.pages.Status.Completed
import navigation.Navigator
import pages.DeceasedSettlorStatus
import pages.deceased_settlor.DeceasedSettlorAnswerPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.deceased_settlor.DeceasedSettlorAnswerView

import scala.concurrent.{ExecutionContext, Future}

class DeceasedSettlorAnswerController @Inject()(
                                                 override val messagesApi: MessagesApi,
                                                 registrationsRepository: RegistrationsRepository,
                                                 @DeceasedSettlor navigator: Navigator,
                                                 actions: Actions,
                                                 requireName: NameRequiredActionProvider,
                                                 val controllerComponents: MessagesControllerComponents,
                                                 view: DeceasedSettlorAnswerView,
                                                 countryOptions : CountryOptions
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)) {
    implicit request =>

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          Seq(checkYourAnswersHelper.setUpAfterSettlorDied,
            checkYourAnswersHelper.deedOfVariation,
            checkYourAnswersHelper.setUpInAddition,
            checkYourAnswersHelper.deceasedSettlorsName,
            checkYourAnswersHelper.deceasedSettlorDateOfDeathYesNo,
            checkYourAnswersHelper.deceasedSettlorDateOfDeath,
            checkYourAnswersHelper.deceasedSettlorDateOfBirthYesNo,
            checkYourAnswersHelper.deceasedSettlorsDateOfBirth,
            checkYourAnswersHelper.deceasedSettlorsNINoYesNo,
            checkYourAnswersHelper.deceasedSettlorNationalInsuranceNumber,
            checkYourAnswersHelper.deceasedSettlorsLastKnownAddressYesNo,
            checkYourAnswersHelper.wasSettlorsAddressUKYesNo,
            checkYourAnswersHelper.deceasedSettlorsUKAddress,
            checkYourAnswersHelper.deceasedSettlorsInternationalAddress
          ).flatten
        )
      )

      Ok(view(draftId, sections))
  }

  def onSubmit(draftId: String) = (actions.authWithData(draftId) andThen requireName(draftId)).async {
    implicit request =>

      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(DeceasedSettlorStatus, Completed))
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(navigator.nextPage(DeceasedSettlorAnswerPage, NormalMode, draftId)(request.userAnswers))
  }
}
