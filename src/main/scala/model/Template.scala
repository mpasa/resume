package me.mpasa.resume.model

import scalatags.Text.TypedTag

/** A template is a way of representing a resume */
trait Template {

  // A sequence of styles used by the template
  def styles: Seq[TypedTag[String]]

  // A sequence of styles used by the template only in the printable version
  def stylesPrintable: Seq[TypedTag[String]]

  // Renders a full version of the resume without anything more in the page (ready to be printed out)
  def renderPrintable(resume: Resume): TypedTag[String]

  // Renders a resume within another page
  def render(resume: Resume): TypedTag[String]
}
