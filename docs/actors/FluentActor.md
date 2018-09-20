# FluentActor

## Function

FluentActor is used to dispatch request from REST Api to Akka system by request message.

## Message for FluentActor

### Fluent request message

Fluent request message contains the following fields:

 1. actorpath [String] : the actorpath of the system
 2. actorclass [String] : the actor class of the actor to be invoked
 3. message type [String] :  the messsge type passed to the actor
 4. message body [String] : the json format of the message body which will be passed to actor.

### FluentActor behavior

FluentActor will receive the request message and query the actorpath, if the actor path has no actor exists,
FluentAcot will create a new actor use atorclass and place the actor to the path
and then format a message use the information in request body, pass the message to the actor.


