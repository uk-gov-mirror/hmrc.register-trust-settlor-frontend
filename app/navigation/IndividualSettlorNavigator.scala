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

package navigation

import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import models.{Mode, NormalMode, UserAnswers}
import pages._
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._
import play.api.mvc.Call

import javax.inject.Singleton

@Singleton
class IndividualSettlorNavigator extends Navigator {

  override def nextPage(page: Page,
                        mode: Mode,
                        draftId: String): UserAnswers => Call = route(draftId)(page)

  override protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    simpleNavigation(draftId) orElse
      yesNoNavigation(draftId) orElse
      mldDependentSimpleNavigation(draftId) orElse
      mldDependentYesNoNavigation(draftId)
  }

  private def simpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorIndividualNamePage(index) => _ =>
      SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId)
    case CountryOfNationalityPage(index) => ua =>
      navigateAwayFromCountryOfNationalityQuestions(ua.isTaxable, index, draftId)
    case CountryOfResidencyPage(index) => ua =>
      navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
    case SettlorAddressUKPage(index) => _ =>
      SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case SettlorAddressInternationalPage(index) => _ =>
      SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId)
    case MentalCapacityYesNoPage(index) => _ =>
      SettlorIndividualAnswerController.onPageLoad(index, draftId)
    case SettlorIndividualAnswerPage => _ =>
      controllers.routes.AddASettlorController.onPageLoad(draftId)
  }

  private def yesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case page @ CountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = UkCountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromCountryOfNationalityQuestions(ua.isTaxable, index, draftId)
      )(ua)
    case page @ UkCountryOfNationalityYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = navigateAwayFromCountryOfNationalityQuestions(ua.isTaxable, index, draftId),
        noCall = CountryOfNationalityController.onPageLoad(NormalMode, index, draftId)
      )(ua)
    case page @ CountryOfResidencyYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = UkCountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId)
      )(ua)
    case page @ UkCountryOfResidencyYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = navigateAwayFromCountryOfResidencyQuestions(ua, index, draftId),
        noCall = CountryOfResidencyController.onPageLoad(NormalMode, index, draftId)
      )(ua)
    case page @ SettlorAddressUKYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId)
      )
    case page @ SettlorIndividualPassportYesNoPage(index) =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId),
        noCall = SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId)
      )
  }

  private def mldDependentSimpleNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case SettlorIndividualDateOfBirthPage(index) => ua =>
      navigateAwayFromDateOfBirthQuestions(ua.is5mldEnabled, index, draftId)
    case SettlorIndividualNINOPage(index) => ua =>
      if (ua.is5mldEnabled) {
        CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
      } else {
        SettlorIndividualAnswerController.onPageLoad(index, draftId)
      }
    case SettlorIndividualPassportPage(index) => ua =>
      navigateToAnswersOrLegallyIncapable(ua.is5mldEnabled, index, draftId)
    case SettlorIndividualIDCardPage(index) => ua =>
      navigateToAnswersOrLegallyIncapable(ua.is5mldEnabled, index, draftId)
  }

  private def mldDependentYesNoNavigation(draftId: String): PartialFunction[Page, UserAnswers => Call] = {
    case page @ SettlorIndividualDateOfBirthYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateAwayFromDateOfBirthQuestions(ua.is5mldEnabled, index, draftId)
      )(ua)
    case page @ SettlorIndividualNINOYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId),
        noCall = if (ua.is5mldEnabled) {
          CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
        } else {
          SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
        }
      )(ua)
    case page @ SettlorAddressYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateToAnswersOrLegallyIncapable(ua.is5mldEnabled, index, draftId)
      )(ua)
    case page @ SettlorIndividualIDCardYesNoPage(index) => ua =>
      yesNoNav(
        fromPage = page,
        yesCall = SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId),
        noCall = navigateToAnswersOrLegallyIncapable(ua.is5mldEnabled, index, draftId)
      )(ua)
  }

  private def navigateAwayFromDateOfBirthQuestions(is5mldEnabled: Boolean, index: Int, draftId: String): Call = {
    if (is5mldEnabled) {
      CountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    }
  }

  private def navigateAwayFromCountryOfNationalityQuestions(isTaxable: Boolean, index: Int, draftId: String): Call = {
    if (isTaxable) {
      SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId)
    }
  }

  private def navigateAwayFromCountryOfResidencyQuestions(ua: UserAnswers, index: Int, draftId: String): Call = {
    if (isNinoDefined(ua, index) || !ua.isTaxable) {
      MentalCapacityYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId)
    }
  }

  private def navigateToAnswersOrLegallyIncapable(is5mldEnabled: Boolean, index: Int, draftId: String): Call = {
    if (is5mldEnabled) {
      MentalCapacityYesNoController.onPageLoad(NormalMode, index, draftId)
    } else {
      SettlorIndividualAnswerController.onPageLoad(index, draftId)
    }
  }

  private def isNinoDefined(ua: UserAnswers, index: Int): Boolean = {
    ua.get(SettlorIndividualNINOPage(index)).isDefined
  }
}
