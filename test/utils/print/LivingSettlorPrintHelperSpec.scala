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

package utils.print

import base.SpecBase
import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import controllers.living_settlor.routes._
import models.pages.IndividualOrBusiness._
import models.pages.Status.Completed
import models.pages._
import models.{Mode, NormalMode, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.LivingSettlorStatus
import pages.living_settlor._
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class LivingSettlorPrintHelperSpec extends SpecBase with ScalaCheckPropertyChecks with BeforeAndAfterEach {

  private val answerRowConverter = injector.instanceOf[AnswerRowConverter]
  private val mockTrustTypePrintHelper = mock[TrustTypePrintHelper]
  private val livingSettlorPrintHelper = new LivingSettlorPrintHelper(answerRowConverter, mockTrustTypePrintHelper)

  private val mode: Mode = NormalMode

  private val name: FullName = FullName("Joe", Some("Joseph"), "Bloggs")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val nino: String = "AA000000A"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val passportOrIdCardDetails: PassportOrIdCardDetails = PassportOrIdCardDetails("GB", "0987654321", date)
  private val index: Int = 0

  override def beforeEach(): Unit = {
    reset(mockTrustTypePrintHelper)
    when(mockTrustTypePrintHelper.answerRows(any(), any())(any())).thenReturn(Nil)
  }

  "LivingSettlorPrintHelper" must {

    "return a living settlor answer section" when {

      "4mld" when {

        "no DoB, NINO or address" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), false).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "DoB and NINO" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
            .set(SettlorIndividualDateOfBirthPage(index), date).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualDateOfBirth.checkYourAnswersLabel", answer = Html("3 February 1996"), changeUrl = Some(SettlorIndividualDateOfBirthController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "UK address and no passport/ID card" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), true).success.value
            .set(SettlorAddressUKYesNoPage(index), true).success.value
            .set(SettlorAddressUKPage(index), ukAddress).success.value
            .set(SettlorIndividualPassportYesNoPage(index), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), false).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressUK.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualPassportYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualIDCardYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "UK address and passport" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), true).success.value
            .set(SettlorAddressUKYesNoPage(index), true).success.value
            .set(SettlorAddressUKPage(index), ukAddress).success.value
            .set(SettlorIndividualPassportYesNoPage(index), true).success.value
            .set(SettlorIndividualPassportPage(index), passportOrIdCardDetails).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressUK.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualPassportYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualPassport.checkYourAnswersLabel", answer = Html("United Kingdom<br />0987654321<br />3 February 1996"), changeUrl = Some(SettlorIndividualPassportController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "international address and ID card" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), true).success.value
            .set(SettlorAddressUKYesNoPage(index), false).success.value
            .set(SettlorAddressInternationalPage(index), nonUkAddress).success.value
            .set(SettlorIndividualPassportYesNoPage(index), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), true).success.value
            .set(SettlorIndividualIDCardPage(index), passportOrIdCardDetails).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressUKYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualAddressInternational.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(SettlorIndividualAddressInternationalController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualPassportYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualIDCardYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualIDCard.checkYourAnswersLabel", answer = Html("United Kingdom<br />0987654321<br />3 February 1996"), changeUrl = Some(SettlorIndividualIDCardController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }
      }

      "5mld" when {

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
          .set(SettlorIndividualNamePage(index), name).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value

        "nationality and residency both unknown" in {

          val userAnswers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), false).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value
            .set(CountryOfResidencyYesNoPage(index), false).success.value
            .set(MentalCapacityYesNoPage(index), false).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualMentalCapacityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "UK nationality and residency" in {

          val userAnswers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), true).success.value
            .set(UkCountryOfNationalityYesNoPage(index), true).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value
            .set(CountryOfResidencyYesNoPage(index), true).success.value
            .set(UkCountryOfResidencyYesNoPage(index), true).success.value
            .set(MentalCapacityYesNoPage(index), false).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualUkCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(UkCountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualUkCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(UkCountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualMentalCapacityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "non-UK nationality and residency" in {

          val userAnswers = baseAnswers
            .set(CountryOfNationalityYesNoPage(index), true).success.value
            .set(UkCountryOfNationalityYesNoPage(index), false).success.value
            .set(CountryOfNationalityPage(index), "FR").success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value
            .set(CountryOfResidencyYesNoPage(index), true).success.value
            .set(UkCountryOfResidencyYesNoPage(index), false).success.value
            .set(CountryOfResidencyPage(index), "FR").success.value
            .set(MentalCapacityYesNoPage(index), false).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url)),
            AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualUkCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(UkCountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfNationality.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(CountryOfNationalityController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualUkCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(UkCountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualCountryOfResidency.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(CountryOfResidencyController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString),
            AnswerRow(label = "settlorIndividualMentalCapacityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = name.toString)
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }
      }
    }
  }

  private def assertThatUserAnswersProduceExpectedAnswerRows(userAnswers: UserAnswers,
                                                     expectedAnswerRows: Seq[AnswerRow]): Assertion = {

    val checkDetailsSection = livingSettlorPrintHelper.checkDetailsSection(userAnswers, name.toString, fakeDraftId, index)
    checkDetailsSection mustEqual AnswerSection(
      headingKey = None,
      rows = expectedAnswerRows,
      sectionKey = None
    )

    val firstPrintSection = livingSettlorPrintHelper.printSection(userAnswers, name.toString, fakeDraftId, index)
    firstPrintSection mustEqual AnswerSection(
      headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
      rows = expectedAnswerRows,
      sectionKey = Some(messages("answerPage.section.settlors.heading"))
    )

    forAll(arbitrary[Int].suchThat(_ > 0)) { subsequentIndex =>
      val subsequentPrintSection = livingSettlorPrintHelper.printSection(userAnswers, name.toString, fakeDraftId, subsequentIndex)
      subsequentPrintSection.headingKey mustBe Some(messages("answerPage.section.settlor.subheading", subsequentIndex + 1))
      subsequentPrintSection.sectionKey mustBe None
    }
  }
}
