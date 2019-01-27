import EventEmitter = PIXI.utils.EventEmitter;

/**
 * The Connector is a class that manages the connection.
 *
 * It's able to fire three events that you can listen for through its EventEmitter:
 * - connect
 * - disconnect
 * - message
 */
export class Connector {
    url: string;
    protected events: EventEmitter;

    connected = false;
    protected connection?: WebSocket;

    private pending: any[] = [];

    constructor(path: string) {
        this.url = this.getRelativeUrl(path);

        this.events = new EventEmitter();

        this.events.on("connect", () => {
            console.log(`[Web-socket] [${this.url}] CONNECTED`);
            this.connected = true;
        });

        this.events.on("disconnect", () => {
            console.log(`[Web-socket] [${this.url}] DISCONNECTED`);
            this.connected = false
        });
    }

    private getRelativeUrl(path: string): string {
        const location = window.location;
        return ((location.protocol === "https:") ? "wss://" : "ws://") + location.hostname +  ":" + location.port + path;
    }

    connect() {
        this.connection = new WebSocket(this.url);

        this.connection.onopen    = this.onConnect.bind(this);
        this.connection.onclose   = this.onDisconnect.bind(this);
        this.connection.onmessage = this.onMessage.bind(this);
    }

    isConnected() {
        return this.connected;
    }

    disconnect() {
        this.connection!.close();
    }

    send(message: any) {
        console.log(`[Web-socket] [${this.url}] SENT`, message);

        this.connection!.send(this.serialize(message));
    }

    private onMessage(raw: any) {
        const message = this.deserialize(raw.data);

        // If there is no event listener for messages we queue them.
        if (this.events.listeners("message").length == 0) {
            console.log(`[Web-socket] [${this.url}] ${raw} QUEUED: `, message);

            this.pending.push(message);
            return;
        }

        this.events.emit("message", message);
    }

    subscribe(event: string, callback: (message: any) => void, context: any, once: boolean = false) {
        if (once) {
            this.events.once(event, callback, context);
        } else {
            this.events.on(event, callback, context);
        }

        // If the event subscribed is a message event then we can emit all pending messages.
        if (event == "message") {
            for (const message of this.pending) {
                this.events.emit("message", message);
            }
            this.pending = [];
        }
    }

    unsubscribe(event: string, callback: (message: any) => void, context: any, once: boolean = false) {
        this.events.removeListener(event, callback, context, once);
    }

    protected onConnect() {
        this.events.emit("connect");
    }

    protected onDisconnect() {
        this.events.emit("disconnect");
    }

    serialize(message: any): string {
        return message.toString();
    }

    deserialize(message: string): any {
        return message;
    }
}
