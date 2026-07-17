resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")

ThisBuild / scalaVersion := "2.13.18"
ThisBuild / majorVersion := 0

lazy val microservice = Project("register-trust-settlor-frontend", file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) // Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(CodeCoverageSettings())
  .settings(
    routesImport += "models._",
    TwirlKeys.templateImports ++= Seq(
      "play.twirl.api.HtmlFormat",
      "play.twirl.api.HtmlFormat._",
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "views.ViewUtils._",
      "controllers.routes._"
    ),
    PlayKeys.playDefaultPort := 8843,
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    ),
    Test / javaOptions ++= Seq(
      "-Dconfig.resource=test.application.conf"
    ),
    libraryDependencies ++= AppDependencies()
  )

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
