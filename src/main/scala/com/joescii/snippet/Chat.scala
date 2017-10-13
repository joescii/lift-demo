package com.joescii.snippet

import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, SHtml, SessionVar}
import net.liftweb.util.ClearClearable

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._

object User extends SessionVar[Option[String]](None)

object Chat {
  private [this] def addLoginPostHandler: Unit = for {
    r <- S.request if r.post_?
    name <- S.param("name")
  } {
    User.set(Some(name))
  }

  def render: NodeSeq => NodeSeq = {
    addLoginPostHandler

    (if(User.get.isDefined) "#login [class+]" #> "hidden"
    else "#chat [class+]" #> "hidden") & ClearClearable
  }
}

object LoginForm {
  private [this] val jsToRunAfterSubmit: JsCmd = JsRaw(
    """$('#login').addClass('hidden');
      |$('#chat').removeClass('hidden');
    """.stripMargin
  )

  def render =
    "name=name" #> (SHtml.text("", name => User.set(Some(name))) ++ SHtml.hidden(() => jsToRunAfterSubmit)) andThen
      "#email-form-2" #> SHtml.makeFormsAjax
}