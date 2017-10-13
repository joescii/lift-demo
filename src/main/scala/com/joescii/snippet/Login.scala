package com.joescii.snippet

import net.liftweb.http.{S, SessionVar}

import scala.xml.NodeSeq

object User extends SessionVar[Option[String]](None)

object Login {
  private def loginPost: Unit = for {
    r <- S.request if r.post_?
    name <- S.param("name")
  } {
    User(Some(name))
  }

  def render: NodeSeq => NodeSeq = { template =>
    println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
    println(template)
    loginPost
    template
  }

}
