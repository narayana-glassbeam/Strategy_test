# Strategy_test
Akka Supervisor Strategy Example 

Hi Bharath,
          Pleased to do this POC on Akka Supervision Exception Strategy. I got good exposure and clarity 
          on Exception handling thing.
          Akka Exception Strategy working is as follows :-
          
          1.Only Parent actor should have SupervisorStrategy to act upon child actors exceptions.
            The Actor itself cant have supervisor strategy.
          
          2.Tested Escalation Exception Strategy when Esacalte Exception Happens It is completey up to the
            parent supervisor or who handles that exception what decision to take like in our case 
            
            1.If Restarted the grand child actor, The grand child actor is loosing data between the period of
            restart and continnuing to receive data.
      
            2.If Stopped, the GrandChild Actor then the grand child actor dies.
            
            3.If Resume the grand child actor then it is not loosing any data 
            
  
