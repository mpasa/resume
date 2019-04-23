package me.mpasa.resume.model

/** Represents the publication of a scientific paper
  *
  * @param title the title of the paper
  * @param date when the paper was published
  * @param coAuthors a list of coauthors. Empty if it was written by a single person
  * @param abs the abstract, a short description of what the paper is about
  */
final case class Publication(title: String, date: Dates, coAuthors: Seq[String], abs: String)
