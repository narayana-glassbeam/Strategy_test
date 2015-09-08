import _root_.akka.actor.ActorSystem
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy.Escalate
import akka.actor.SupervisorStrategy.Restart
import akka.actor.SupervisorStrategy.Resume
import akka.actor.SupervisorStrategy.Stop
import akka.actor.Terminated

import scala.concurrent.duration._

class ChildCount extends Actor{
	override val supervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 60 seconds) {
			case _:Exception => Escalate // there are no 'fatal' exceptions in the parsing stage
		}

	val grandChildCount:ActorRef = context.actorOf(Props[GrandChildCount],"GrandChildCount")

	def receive = {

		case cc:Int =>
			if(cc==3) {
					println("In child Throwing Arithmetic Exception for value "+cc+" and restarting child")
					throw new ArithmeticException()
				}
			else if(cc==6) {
					println("In Child Count "+cc+" Throwing Null Pointer Exception and Resuming childcount actor ")
					throw new NullPointerException
				}
			else {
					println("In Child Count "+cc)
				grandChildCount ! cc
				}

		case Terminated(x) => println(s"child actor $x dead")
	}
}
class GrandChildCount extends Actor{

	def receive = {
		case gcc:Int =>
			if(gcc==8)
			{
				println("In Grand child Escalating Exception to ChildCount actor to stop receiving further data ")
				throw new Exception
			}
			else{
				println("In Grand Child Count "+gcc)
			}
	}

}


class SupervisorCount extends Actor{

	override val supervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 60 seconds) {
			case _: ArithmeticException => Restart
				case _:NullPointerException => Resume
			case _: Exception => Stop // there are no 'fatal' exceptions in the parsing stage
		}


	val childCount:ActorRef = context.actorOf(Props[ChildCount],"ChildCount")

	def receive = {
		case "send" =>
		//child ! "stop"
		//anotherChild ! "stop"
		case sc:Int =>
			println("In Supervisor Count "+sc)
			childCount ! sc

		case Terminated(x) => println(s"child actor $x dead")
	}

}


object TestSupervision{

	val system = ActorSystem("Akka_Strategy")
	val supervisorCount = system.actorOf(Props[SupervisorCount], "SupervisorCount")

	def main(args: Array[String]): Unit = {

		for(i<-1 to 10)
		{

			supervisorCount ! i
		}

	}

}