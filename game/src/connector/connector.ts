import * as Phaser from "phaser";
import EventEmitter = Phaser.Events.EventEmitter;

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
    events: EventEmitter;

    connected = false;
    private connection?: WebSocket;

    constructor(url: string) {
        this.url = url;
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
        console.log(`[Web-socket] [${this.url}] OUT ${JSON.stringify(message)}`);
        this.connection!.send(this.outboundMessage(message));
    }

    private onMessage(raw: any) {
        const message = this.inboundMessage(raw.data);
        console.log(`[Web-socket] [${this.url}] IN  ${JSON.stringify(message)}`);
        this.events.emit("message", message);
    }

    protected onConnect() {
        this.events.emit("connect");
    }

    protected onDisconnect() {
        this.events.emit("disconnect");
    }

    protected inboundMessage(message: string): any {
        return message;
    }

    protected outboundMessage(message: any): string {
        return message.toString();
    }
}
