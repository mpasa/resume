package model

import scalatags.Text.TypedTag

/** Represents a certification. It's somehow similar to an education but more specific
  * For example: a coursera course, a language programming certificate...
  */
final case class Certification(name: String,
                               dates: Dates,
                               organization: String,
                               skills: TypedTag[String])