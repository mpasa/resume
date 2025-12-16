package me.mpasa.resume.model

import scalatags.Text.TypedTag

/** Represents an item of work experience of a person */
final case class Job(
    company: String,
    title: String,
    dates: Dates,
    description: TypedTag[String],
    extra: TypedTag[String]
) extends ExperienceItem {

  def descriptionAll = Seq(description, extra)
}
