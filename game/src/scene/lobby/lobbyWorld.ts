import {CurrentLobbyInfoPacket, LobbyPlayerInfo} from "@common/matchmaking/protocol";
import {World} from "../../world/world";
import {HGame} from "../../index";
import {Player} from "../../entity/player/player";
import {LobbyPlayerHud} from "./lobbyPlayerHud";
import {EntityTypes} from "../../entity/entityTypes";

export class LobbyWorld extends World {
    static GROUND_HEIGHT = 2;

    game: HGame;
    players: Player[] = [];

    constructor(game: HGame) {
        super(game.app, {
            id: "lobby",
            width: 13,
            height: 7,
            platforms: [
                {
                    x: 0,
                    y: 0,
                    width: 13,
                    height: 1,
                    texture: "assets/game/dirt.png"
                },
                {
                    x: 0,
                    y: LobbyWorld.GROUND_HEIGHT - 1,
                    width: 13,
                    height: 1,
                    texture: "assets/game/grass.png"
                }
            ],
            texts: [],
            emitters: [],
            decorations: [
                {
                    x: 0,
                    y: 2,
                    width: 3,
                    height: 3,
                    texture: "assets/game/tree.png"
                },
                {
                    x: 6,
                    y: 2,
                    width: 3,
                    height: 3,
                    texture: "assets/game/tree.png"
                }
            ]
        });
        this.game = game;
    }

    private applyPlayerInfo(player: Player, info: LobbyPlayerInfo) {
        // TODO player.setType
        player.name = info.name;

        const hud = player.huds[0] as LobbyPlayerHud;
        hud.ready = info.ready;
        hud.me = info.you;
        hud.leader = info.admin;
    }

    onInfo(packet: CurrentLobbyInfoPacket) {
        for (let i = this.players.length - 1; i >= packet.players.length - 1; i--) {
            this.despawn(this.players[i]);
            this.players.splice(i, 1);
        }

        for (let i = 0; i < packet.players.length; i++) {
            const info = packet.players[i];
            let player = this.players[i];

            if (!player) {
                player = new Player(
                    this,
                    Player.createBody(this),
                    false,
                    EntityTypes.get(info.character || "santy")!,
                    {gameHud: false}
                );
                player.huds.push(new LobbyPlayerHud(player));

                this.spawn(player);
                this.players.push(player);
            }

            this.applyPlayerInfo(player, info);
        }

        const step = this.width / (this.players.length + 1);
        let distance = step;

        for (const player of this.players) {
            player.x = distance;
            player.y = LobbyWorld.GROUND_HEIGHT;
            distance += step;
        }
    }
}
