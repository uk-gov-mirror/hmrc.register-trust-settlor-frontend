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

package controllers.living_settlor.business

import config.annotations.BusinessSettlor
import controllers.actions.Actions
import controllers.actions.living_settlor.business.NameRequiredActionProvider
import forms.InternationalAddressFormProvider

import javax.inject.Inject
import models.Mode
import models.pages.InternationalAddress
import navigation.Navigator
import pages.living_settlor.business.SettlorBusinessAddressInternationalPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.living_settlor.business.SettlorBusinessAddressInternationalView

import scala.concurrent.{ExecutionContext, Future}

class SettlorBusinessAddressInternationalController @Inject()(
                                                               override val messagesApi: MessagesApi,
                                                               registrationsRepository: RegistrationsRepository,
                                                               @BusinessSettlor navigator: Navigator,
                                                               actions: Actions,
                                                               requireName: NameRequiredActionProvider,
                                                               formProvider: InternationalAddressFormProvider,
                                                               val controllerComponents: MessagesControllerComponents,
                                                               view: SettlorBusinessAddressInternationalView,
                                                               val countryOptions: CountryOptionsNonUK
                                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[InternationalAddress] = formProvider()

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorBusinessAddressInternationalPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, countryOptions.options, mode, index, draftId, request.businessName))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, countryOptions.options, mode, index, draftId, request.businessName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorBusinessAddressInternationalPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorBusinessAddressInternationalPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
