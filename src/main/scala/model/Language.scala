package model

/** Represents a language (native or foreign) spoken by a person */
sealed trait Language

/** A native language learnt by a person since childhood */
final case class NativeLanguage(name: String) extends Language

/** A language learnt in a non-native context.
  *
  * It's separated into different skills using the European
  * [[https://en.wikipedia.org/wiki/Common_European_Framework_of_Reference_for_Languages CEF]] levels
  */
final case class ForeignLanguage(name: String,
                                 reading: LanguageLevel,
                                 listening: LanguageLevel,
                                 writing: LanguageLevel,
                                 speaking: LanguageLevel) extends Language