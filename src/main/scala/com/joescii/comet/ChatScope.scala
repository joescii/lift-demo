package com.joescii.comet

import com.joescii.snippet.{ChatMessage, ChatServer}
import net.liftmodules.ng.AngularActor
import net.liftweb.http.CometListener


class ChatScope extends AngularActor with CometListener {
  override protected def registerWith = ChatServer

  override def lowPriority = {
    case m: ChatMessage =>
      println(s"Pushing $m")
      scope.emit("NewMessage", m)
  }
}