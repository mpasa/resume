package me.mpasa.resume.model

/** Represents the full resume of a person
  *
  * Order in the sequence fields in important. It represents the order in which they will be shown
  */
final case class Resume(
    personalData: PersonalData,
    experience: Seq[ExperienceItem],
    education: Seq[Education],
    certifications: Seq[Certification],
    languages: Seq[Language],
    publications: Seq[Publication]
)
