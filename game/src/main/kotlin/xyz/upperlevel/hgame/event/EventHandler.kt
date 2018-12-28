package xyz.upperlevel.hgame.event


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention
annotation class EventHandler(val priority: Byte = EventPriority.NORMAL)
