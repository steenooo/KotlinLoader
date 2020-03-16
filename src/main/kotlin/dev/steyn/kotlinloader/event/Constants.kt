package dev.steyn.kotlinloader.event

object Constants {


    const val B_EVENT_DESCRIPTOR = "Lorg/bukkit/event/Event;"

    const val B_EVENT_NAME = "org/bukkit/event/Event"

    const val B_EVENT_CLASS = "org.bukkit.event.Event"

    const val HANDLERLIST_DESCRIPTOR = "Lorg/bukkit/event/HandlerList;"
    const val GETHANDLERLIST_DESCRIPTOR = "()Lorg/bukkit/event/HandlerList;"
    const val GETHANDLERS_DESCRIPTOR = "()Lorg/bukkit/event/HandlerList;"

   const val HANDLERLIST_FIELD_BACKUP = "\$HANDLER_LIST"
   const val HANDLER_LIST_NAME = "HANDLER_LIST"

    const val HANDLERLIST_NAME = "org/bukkit/event/HandlerList"

    const val GETHANDLERLIST_METHODNAME = "getHandlerList"
    const val GETHANDLERS_METHODNAME = "getHandlers"

    const val CINIT_METHOD = "<cinit>"
    const val CINIT_METHOD_DESC  = "()V"
    const val EVENT = "dev/steyn/kotlinloader/api/event/Event"
    const val EVENT_DESC = "Ldev/steyn/kotlinloader/api/event/Event;"
}