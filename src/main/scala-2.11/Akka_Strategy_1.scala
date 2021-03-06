import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy.Stop
import akka.actor.Terminated

import scala.concurrent.duration._

class ChildCount_1 extends Actor{
	override val supervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 60 seconds) {
			case _:Exception => Stop // there are no 'fatal' exceptions in the parsing stage
		}

	//val grandChildCount:ActorRef = context.actorOf(Props[GrandChildCount],"GrandChildCount")

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
				//grandChildCount ! cc
			}

		case Terminated(x) => println(s"child actor $x dead")
	}
}
/*class GrandChildCount extends Actor{

	def receive = {
		case gcc:Int =>
			if(gcc==8)
			{
				println("In Grand child Escalating Exception to ChildCount actor to Restart to recieve further data "+gcc)
				throw new Exception
			}
			else{
				println("In Grand Child Count "+gcc)
			}
	}

}*/


class SupervisorCount_1 extends Actor{

	/*override val supervisorStrategy =
		OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 60 seconds) {
			case _: ArithmeticException => Restart
			case _:NullPointerException => Resume
			case _: Exception => Resume // there are no 'fatal' exceptions in the parsing stage
		}*/


	val childCount:ActorRef = context.actorOf(Props[ChildCount_1],"ChildCount")

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


object AkkaSupervision{

	val system = ActorSystem("Akka_Strategy")
	val supervisorCount = system.actorOf(Props[SupervisorCount_1], "SupervisorCount")

	def main(args: Array[String]): Unit = {

		for(i<-1 to 30)
		{

			supervisorCount ! i
		}

	}

}