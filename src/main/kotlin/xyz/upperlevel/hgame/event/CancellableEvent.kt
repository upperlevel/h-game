package xyz.upperlevel.hgame.event

open class CancellableEvent : Event, Cancellable {
    override var isCancelled = false
}
