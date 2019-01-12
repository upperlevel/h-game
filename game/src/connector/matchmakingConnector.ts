import {Connector} from "./connector";

export class MatchmakingConnector extends Connector {
    constructor() {
        super("wss://hgame.gq/api/matchmaking");
    }

    // All messages sent within this protocol are JSON formatted.

    deserialize(message: string): any {
        return JSON.parse(message);
    }

    serialize(message: any): string {
        return JSON.stringify(message);
    }
}
