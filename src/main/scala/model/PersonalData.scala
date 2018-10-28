package me.mpasa.resume.model

import scalatags.Text.TypedTag

/** Represents all the data related to the personal information of a person */
final case class PersonalData(name: String,
                              lastName: String,
                              email: String,
                              github: Link,
                              twitter: Link,
                              webpage: Link,
                              description: TypedTag[String])