package me.mpasa.resume.model

import java.time.YearMonth

/** Represents the start and optional end of something */
final case class Dates(start: YearMonth, end: Option[YearMonth] = None)

object Dates {

  /** Creates a new [[Dates]] object where both start and end have the same value */
  def single(startAndEnd: YearMonth) = Dates(startAndEnd, Some(startAndEnd))
}
