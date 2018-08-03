package me.mpasa.resume

import java.time.format.DateTimeFormatter

import model._
import scalatags.Text.{TypedTag, tags2}
import scalatags.Text.all._

import scala.io.Source

/** A clean white template
  *
  * It shows the name in big, followed by the personal links with FontAwesome icons
  * Then, it shows the different sections in the following order:
  * - Work experience
  * - Education
  * - Certifications
  * - Languages
  * - Publications
  */
object CleanTemplate extends Template {

  /** A lazy way to call a span for icons */
  private def icon(c: String) = span(cls := c)

  private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy")

  /** Shows a div representing a [[Dates]] object
    * If the end date is not set, only the start date is shown
    * It both dates are set, an arrow is shown in between
    */
  private def formatDates(dates: Dates): TypedTag[String] = {
    if (dates.end.contains(dates.start)) {
      div(cls := "dates")(dates.start.format(MONTH_FORMATTER))
    } else {
      div(cls := "dates")(
        div(dates.start.format(MONTH_FORMATTER)),
        icon("toDate fas fa-angle-down"),
        div(dates.end.map(_.format(MONTH_FORMATTER)).getOrElse("Present").asInstanceOf[String])
      )
    }
  }

  /** A section is just a big title and content
    *
    * Examples of sections are: Work experience, education...
    */
  private def section(title: String, content: TypedTag[String]*) = div(cls := "section")(
    h2(cls := "name")(title),
    div(content)
  )

  /** Generates the data of a section of something that have dates
    *
    * @param element the element being shown
    * @param dates a function to extract a [[Dates]] from the element
    * @param extractor a function to extract HTML from the element
    */
  private def sectionDataWithDates[A](element: A)(dates: A => Dates)(extractor: A => TypedTag[String]) = {
    div(cls := "content")(
      div(cls := "dates")(formatDates(dates(element))),
      div(cls := "data")(extractor(element))
    )
  }

  /** Shows a section title with a subtitle */
  private def titleWithSubHeading(ttle: String, organization: String) = div(cls := "heading")(
    title(ttle),
    subtitle(organization)
  )

  /** Shows a section title. Titles are shown bigger that the surrounding text */
  private def title(title: String) = div(cls := "title")(title)

  /** Subtitle for a section title. They're shown a bit smaller  */
  private def subtitle(text: String) = div(cls := "subtitle")(text)

  /** Shows a job with its dates */
  private def job(job: Job) = {
    sectionDataWithDates(job)(_.dates) { job =>
      div(
        titleWithSubHeading(job.title, job.company),
        job.description
      )
    }
  }

  /** Shows an education with its dates */
  private def education(education: Education) = {
    sectionDataWithDates(education)(_.dates) { education =>
      div(
        titleWithSubHeading(education.title, education.organization),
        div(education.skills),
        education.notes
      )
    }
  }

  /** Shows a certification with its dates */
  private def certification(certification: Certification) = {
    sectionDataWithDates(certification)(_.dates) { certification =>
      div(
        titleWithSubHeading(certification.name, certification.organization),
        div(certification.skills)
      )
    }
  }

  /** Shows a publication with its publication date */
  private def publication(publication: Publication) = {
    sectionDataWithDates(publication)(_.date) { publication =>
      div(cls := "heading")(
        title(publication.title),
        div("Co-authors: ",
          if (publication.coAuthors.length > 1) {
            publication.coAuthors.init.mkString(", ") + " and " + publication.coAuthors.last
          } else {
            publication.coAuthors.mkString(", ")
          },
          "."
        )
      )
    }
  }

  /** Shows a table showing the native and foreign language levels
    *
    * Each language shows the reading, listening, writing and speaking levels in different columns
    */
  private def languagesTable(languages: Seq[Language]) = {
    def levelTD(level: LanguageLevel) = div(
      td(cls := "level")(
        div(level.name),
        div(cls := "languageLevelDescription")(level.description)
      )
    )

    val languageRows = languages.map {
      case NativeLanguage(language) => tr(
        td(cls := "language", language),
        td(cls := "level", colspan := 4, "Native")
      )
      case ForeignLanguage(language, reading, listening, writing, speaking) => tr(
        td(cls := "language", language),
        levelTD(reading),
        levelTD(listening),
        levelTD(writing),
        levelTD(speaking)
      )
    }
    val cefRow = tr(td(""), td(colspan := 4, cls := "note")("Common European Framework of Reference for Languages (CFE)"))

    table(
      thead(tr(Seq("", "Reading", "Listening", "Writing", "Speaking").map(th(_)))),
      tbody(languageRows :+ cefRow)
    )
  }


  /** Shows a row with contact information
    *
    * - E-mail
    * - Github profile
    * - Twitter profile
    */
  private def contactInfo(personal: PersonalData) = {
    div(
      a(href := s"mailto:${personal.email}", span(icon("icon fas fa-envelope"), personal.email)),
      a(href := personal.github.url)(icon("icon fab fa-github"), personal.github.anchor),
      a(href := personal.twitter.url)(icon("icon fab fa-twitter"), personal.twitter.anchor)
    )
  }

  override def render(resume: Resume): TypedTag[String] = {
    html(
      head(
        meta(charset := "UTF-8"),
        tags2.style(raw(Source.fromResource("clean/styles.css").mkString)),
        link(rel := "stylesheet", href := "https://use.fontawesome.com/releases/v5.0.13/css/all.css"),
        link(rel := "stylesheet", href := "https://fonts.googleapis.com/css?family=Open+Sans"),
        tags2.title(resume.personalData.name + " " + resume.personalData.lastName)
      ),
      body(
        div(cls := "header")(
          h1(cls := "nameInfo")(strong(Data.personal.name), " ", Data.personal.lastName),
          contactInfo(Data.personal)
        ),
        div(cls := "personalDescription")(Data.personal.description),
        section("Experience", resume.experience.map(job): _*),
        section("Education", resume.education.map(education): _*),
        section("Certifications", resume.certifications.map(certification): _*),
        section("Languages", languagesTable(resume.languages)),
        section("Publications", resume.publications.map(publication): _*)
      )
    )
  }
}
