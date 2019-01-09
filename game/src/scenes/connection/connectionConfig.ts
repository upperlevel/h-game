import {Connector} from "../../connector/connector";

export interface ConnectionConfig {
    /**
     * The connector to use to establish the connection.
     */
    connector: Connector,

    /**
     * The scene that will be set when the connection was successful.
     */
    nextScene: string,

    nextSceneParams?: any;
}
