package me.mpasa.resume.model

/** Represents a link (anchor and URL)
  *
  * @param anchor the text shown in top of the link
  * @param url the real URL the link sends to
  */
final case class Link(anchor: String, url: String)
