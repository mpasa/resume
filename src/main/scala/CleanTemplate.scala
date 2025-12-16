package me.mpasa.resume

import java.time.format.DateTimeFormatter

import model._
import scalatags.Text.{TypedTag, tags2}
import scalatags.Text.all._
import mouse.boolean._

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
class CleanTemplate(onePage: Boolean) extends Template {

  private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy")

  /** A lazy way to call a span for icons */
  private def icon(c: String) = span(cls := c)

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
        div(StringFrag(dates.end.map(_.format(MONTH_FORMATTER)).getOrElse("Present")))
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

  private val experienceDurationScript = script(raw(
    """
      document.addEventListener("DOMContentLoaded", function() {
        const entries = document.querySelectorAll(".experienceDates");
        entries.forEach(function(el) {
          const start = el.dataset.start;
          if (!start) {
            return;
          }
          const startParts = start.split("-");
          const startDate = new Date(parseInt(startParts[0], 10), parseInt(startParts[1], 10) - 1, 1);
          const end = el.dataset.end;
          const endDate = end
            ? new Date(parseInt(end.split("-")[0], 10), parseInt(end.split("-")[1], 10) - 1, 1)
            : new Date();
          const months =
            (endDate.getFullYear() - startDate.getFullYear()) * 12 +
            (endDate.getMonth() - startDate.getMonth()) +
            1;
          const years = Math.floor(months / 12);
          const remainingMonths = months % 12;
          const durationParts = [];
          if (years > 0) {
            durationParts.push(`${years} yr${years === 1 ? "" : "s"}`);
          }
          if (remainingMonths > 0) {
            durationParts.push(`${remainingMonths} mo${remainingMonths === 1 ? "" : "s"}`);
          }
          const durationSpan = el.querySelector(".experienceDuration");
          if (durationSpan) {
            if (durationParts.length > 0) {
              durationSpan.textContent = ` (${durationParts.join(" ")})`;
            } else {
              durationSpan.textContent = "";
            }
          }
        });
      });
    """
  ))

  /** Shows a section title with a subtitle */
  private def titleWithSubHeading(ttle: String, organization: String) = div(cls := "heading")(
    title(ttle),
    subtitle(organization)
  )

  /** Shows a section title. Titles are shown bigger that the surrounding text */
  private def title(title: String) = div(cls := "title")(title)

  /** Subtitle for a section title. They're shown a bit smaller  */
  private def subtitle(text: String) = div(cls := "subtitle")(text)

  private sealed trait ExperienceSection
  private case class ExperienceCompany(company: String, entries: Seq[Job], dates: Dates) extends ExperienceSection
  private case class ExperienceSabbatical(entry: Sabbatical) extends ExperienceSection

  private def experienceDates(dates: Dates) = {
    val startLabel = dates.start.format(MONTH_FORMATTER)
    val endLabel = dates.end.map(_.format(MONTH_FORMATTER)).getOrElse("Present")
    val endValue = dates.end.map(_.toString).getOrElse("")

    div(
      cls := "experienceDates",
      attr("data-start") := dates.start.toString,
      attr("data-end") := endValue
    )(
      span(cls := "experienceDatesRange")(s"$startLabel to $endLabel"),
      span(cls := "experienceDuration")
    )
  }

  private def aggregateCompanyDates(entries: Seq[Job]): Dates = {
    val start = entries.map(_.dates.start).min
    val hasOpenEnded = entries.exists(_.dates.end.isEmpty)
    val lastEnd =
      if (hasOpenEnded) None
      else entries.flatMap(_.dates.end) match {
        case Nil => None
        case seq => Some(seq.max)
      }
    Dates(start, lastEnd)
  }

  private def groupedExperiences(items: Seq[ExperienceItem]): Seq[ExperienceSection] = {
    val sections = scala.collection.mutable.ArrayBuffer.empty[ExperienceSection]
    var currentCompany: Option[(String, Vector[Job])] = None

    def flushCompany(): Unit = {
      currentCompany.foreach {
        case (company, entries) =>
          sections += ExperienceCompany(company, entries, aggregateCompanyDates(entries))
      }
      currentCompany = None
    }

    items.foreach {
      case job: Job =>
        currentCompany match {
          case Some((company, entries)) if company == job.company =>
            currentCompany = Some((company, entries :+ job))
          case Some(_) =>
            flushCompany()
            currentCompany = Some((job.company, Vector(job)))
          case None =>
            currentCompany = Some((job.company, Vector(job)))
        }
      case sabbatical: Sabbatical =>
        flushCompany()
        sections += ExperienceSabbatical(sabbatical)
    }

    flushCompany()
    sections.toSeq
  }

  private def jobEntry(job: Job) = {
    div(cls := "companyJobEntry")(
      div(cls := "companyJobHeading")(
        div(cls := "positionTitle")(
          span(cls := "positionBullet"),
          title(job.title)
        ),
        experienceDates(job.dates)
      ),
      div(cls := "experienceEntryDetails")(
        if (onePage) job.description
        else job.descriptionAll
      )
    )
  }

  private def companySection(section: ExperienceCompany) = {
    div(cls := "block companyBlock")(
      div(cls := "blockHeader companyBlockHeader")(
        div(cls := "companyName")(
          section.company,
          experienceDates(section.dates)
        ),
      ),
      div(cls := "companyJobs")(section.entries.map(jobEntry)*)
    )
  }

  private def sabbaticalEntry(entry: Sabbatical) = {
    div(cls := "block sabbaticalBlock")(
      div(cls := "blockHeader sabbaticalHeader")(
        title("Sabbatical"),
        experienceDates(entry.dates)
      )
    )
  }

  private def renderExperienceSection(section: ExperienceSection): TypedTag[String] = section match {
    case company: ExperienceCompany => companySection(company)
    case ExperienceSabbatical(entry) => sabbaticalEntry(entry)
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
        div(
          "Co-authors: ",
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
      case NativeLanguage(language) =>
        tr(
          td(cls := "language", language),
          td(cls := "level", colspan := 4, "Native")
        )
      case ForeignLanguage(language, reading, listening, writing, speaking) =>
        tr(
          td(cls := "language", language),
          levelTD(reading),
          levelTD(listening),
          levelTD(writing),
          levelTD(speaking)
        )
    }
    val cefRow =
      tr(td(""), td(colspan := 4, cls := "note")("Common European Framework of Reference for Languages (CFE)"))

    table(
      thead(tr(Seq("", "Reading", "Listening", "Writing", "Speaking").map(th(_)))),
      tbody(languageRows :+ cefRow)
    )
  }

  /** Shows a row with contact information
    *
    * - E-mail
    * - Github profile
    * - X profile
    */
  private def contactInfo(personal: PersonalData) = {
    div(
      a(href := s"mailto:${personal.email}", span(icon("icon fas fa-envelope"), personal.email)),
      a(href := personal.github.url)(icon("icon fab fa-github"), personal.github.anchor),
      a(href := personal.x.url)(icon("icon fab fa-x-twitter"), personal.x.anchor),
      a(href := personal.webpage.url)(icon("icon fab fa-firefox"), personal.webpage.anchor)
    )
  }

  // A sequence of styles used by the template
  override val styles: Seq[TypedTag[String]] = {
    val normalStyles = tags2.style(raw(Source.fromResource("clean/styles.css").mkString))
    val onePageStyles = tags2.style(raw(Source.fromResource("clean/styles-onepage.css").mkString))

    if (onePage) Seq(normalStyles, onePageStyles)
    else Seq(normalStyles)
  }

  // A sequence of styles used by the template only in the printable version
  override val stylesPrintable: Seq[TypedTag[String]] = Seq(
    tags2.style(raw(Source.fromResource("clean/styles-printable.css").mkString))
  )

  // Renders a full version of the resume without anything more in the page (ready to be printed out)
  override def renderPrintable(resume: Resume): TypedTag[String] = {
    html(
      head(
        meta(charset := "UTF-8"),
        link(rel := "stylesheet", href := "https://use.fontawesome.com/releases/v6.7.2/css/all.css"),
        link(rel := "stylesheet", href := "https://fonts.googleapis.com/css?family=Open+Sans"),
        tags2.title(resume.personalData.name + " " + resume.personalData.lastName),
        styles,
        stylesPrintable
      ),
      body(
        render(resume)
      )
    )
  }

  // Renders a resume within another page
  override def render(resume: Resume): TypedTag[String] = {
    div(cls := "resume")(
      div(cls := "header")(
        h1(cls := "nameInfo")(strong(Data.personal.name), " ", Data.personal.lastName),
        contactInfo(Data.personal)
      ),
      div(cls := "personalDescription")(Data.personal.description),
      section("Experience", groupedExperiences(resume.experience).map(renderExperienceSection)*),
      section("Education", resume.education.map(education)*),
      // Optional sections
      (!onePage).option {
        Seq(
          section("Certifications", resume.certifications.map(certification)*),
          section("Languages", languagesTable(resume.languages)),
          section("Publications", resume.publications.map(publication)*)
        )
      },
      // One-page disclaimer
      onePage.option {
        p(cls := "note")(
          strong("Note"),
          ": this is a one-page version of my resume. If you want to read more about me, you can check the full version on https://mpasa.me/resume"
        )
      }
      ,
      experienceDurationScript
    )
  }
}
