package model

import scalatags.Text.TypedTag

/** Represents an item of work experience of a person */
final case class Job(company: String,
                     title: String,
                     dates: Dates,
                     description: TypedTag[String])