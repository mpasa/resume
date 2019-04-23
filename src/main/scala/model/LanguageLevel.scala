package me.mpasa.resume.model

/** Represents a level in a foreign language */
abstract class LanguageLevel(val name: String, val description: String)

/** CFE levels */
case object A1 extends LanguageLevel("A1", "Beginner")
case object A2 extends LanguageLevel("A2", "Elementary")
case object B1 extends LanguageLevel("B1", "Intermediate")
case object B2 extends LanguageLevel("B2", "Upper-Intermediate")
case object C1 extends LanguageLevel("C1", "Advanced")
case object C2 extends LanguageLevel("C2", "Proficiency")
