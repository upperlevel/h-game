package xyz.upperlevel.hgame.event;

public class CancellableEvent implements Event, Cancellable {
    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
