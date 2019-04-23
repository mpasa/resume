package me.mpasa.resume

import ammonite.ops._
import me.mpasa.resume.model.{Resume, Template}

object Main {

  /**
    * Writes a resume to a file using a template and a name
    */
  private def writeResume(resume: Resume, name: String, template: Template) = {
    val html = template.renderPrintable(resume)
    val nameHtml = s"$name.html"
    rm(pwd / 'target / nameHtml)
    write.over(pwd / 'target / nameHtml, html.render)
  }

  def main(args: Array[String]): Unit = {
    writeResume(Data.resume, "resume", new CleanTemplate(false))
    writeResume(Data.resume, "one_page", new CleanTemplate(true))
  }
}
