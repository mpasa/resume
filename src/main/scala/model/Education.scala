package me.mpasa.resume.model

import scalatags.Text.TypedTag

/** Represents a person's education (high school, bachelor's degree...) */
final case class Education(
    dates: Dates,
    title: String,
    organization: String,
    skills: TypedTag[String],
    notes: Option[TypedTag[String]]
)
