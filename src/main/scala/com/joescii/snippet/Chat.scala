package com.joescii.snippet

import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{S, SHtml, SessionVar}
import net.liftweb.util.ClearClearable
import net.liftweb.util.Helpers._
import net.liftmodules.ng.Angular
import net.liftmodules.ng.Angular._
import net.liftweb.common.{Empty, Full}

import scala.xml.NodeSeq

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

case class ChatMessage(name: String, msg: String) extends NgModel

object ChatService {
  def render = renderIfNotAlreadyDefined(
    angular.module("ChatServer").factory("chatService", Angular.jsObjFactory()
      .defAny("messages", Full(List(ChatMessage("joe", "server"))))
      .defStringToAny("submit", message => {
        println(s"Received $message")
        Empty
      })
  ))
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