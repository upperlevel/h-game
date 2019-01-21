import {World} from "./world";
import {Terrain} from "./terrain";

export class Text {
    world: World;

    sprite: PIXI.Text;
    data: Terrain.Text;

    debug?: PIXI.Graphics;

    constructor(world: World, data: Terrain.Text, debug: boolean = false) {
        this.world = world;
        this.data = data;

        this.sprite = new PIXI.Text(data.text, data.style);
        this.world.app.stage.addChild(this.sprite);

        if (debug) {
            this.debug = new PIXI.Graphics();
            this.world.app.stage.addChild(this.debug);
        }

        this.sprite.scale.x = this.sprite.scale.y = 1 / this.data.style.fontSize * this.data.height;
        this.sync();
    }

    set x(x: number) {
        this.data.x = x;
        this.sync();
    }

    set y(y: number) {
        this.data.y = y;
        this.sync();
    }

    set height(height: number) {
        this.data.height = height;
        this.sync();
    }

    set style(style: PIXI.TextStyleOptions) {
        this.data.style = style;
        this.sync();
    }

    private sync() {
        const pos = this.sprite.position;
        pos.x = this.data.x + (this.data.centered ? 0 : this.sprite.width / 2);
        pos.y = this.world.height - this.data.y;

        if (this.debug) {
            this.debug.clear();
            this.debug.lineStyle(1 / 48, 0x0000ff);
            const points = [
                new PIXI.Point(pos.x, pos.y),
                new PIXI.Point(pos.x + this.sprite.width, pos.y),
                new PIXI.Point(pos.x + this.sprite.width, pos.y + this.sprite.height),
                new PIXI.Point(pos.x, pos.y + this.sprite.height)
            ];
            //console.log(points);
            this.debug.drawPolygon(points);
        }
    }
}
