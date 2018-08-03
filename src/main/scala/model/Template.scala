package me.mpasa.resume.model

import scalatags.Text.TypedTag

/** A template is a way of representing a resume */
trait Template {
  def render(resume: Resume): TypedTag[String]
}
