package me.mpasa.resume

import ammonite.ops._

object Main {
  def main(args: Array[String]): Unit = {
    val resume = Data.resume
    val html = CleanTemplate.renderPrintable(resume)
    rm(pwd / 'target / "resume.html")
    write.over(pwd / 'target / "resume.html", html.render)
  }
}