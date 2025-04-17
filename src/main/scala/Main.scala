package me.mpasa.resume

import me.mpasa.resume.model.{Resume, Template}

object Main {

  /**
    * Writes a resume to a file using a template and a name
    */
  private def writeResume(resume: Resume, name: String, template: Template) = {
    val html = template.renderPrintable(resume)
    val nameHtml = s"$name.html"
    os.remove(os.pwd / "target" / nameHtml)
    os.write.over(os.pwd / "target" / nameHtml, html.render)
  }

  def main(args: Array[String]): Unit = {
    writeResume(Data.resume, "resume", new CleanTemplate(false))
    writeResume(Data.resume, "one_page", new CleanTemplate(true))
  }
}
