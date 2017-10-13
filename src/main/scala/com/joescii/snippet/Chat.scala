package com.joescii.snippet

import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.{CometListener, ListenerManager, S, SHtml, SessionVar}
import net.liftweb.util.ClearClearable
import net.liftweb.util.Helpers._
import net.liftmodules.ng.{Angular, AngularActor}
import net.liftmodules.ng.Angular._
import net.liftweb.actor.LiftActor
import net.liftweb.common.{Box, Empty, Full, SimpleActor}

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
      .defFutureAny("messages", ChatServer.sendAndGetFuture(GetMessages).map(Box.legacyNullTest(_)))
      .defStringToAny("submit", message => {
        ChatServer ! ChatMessage(User.get.get, message)
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

case object GetMessages

object ChatServer extends LiftActor with ListenerManager {
  override protected def createUpdate = messages

  private [this] var messages: List[ChatMessage] = List(ChatMessage("initial", "state"))

  override protected def lowPriority = {
    case m: ChatMessage =>
      println(s"ChatServer received $m")
      sendListenersMessage(m)

    case GetMessages => reply(messages)
  }
}

