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

        this.sprite = new PIXI.Text(data.text, Object.assign(
            data.style,
            {
                fontSize: this.data.height * 128,
            })
        );
        this.world.app.stage.addChild(this.sprite);

        this.sprite.scale.x = this.sprite.scale.y = 1 / (this.data.height * 128) * this.data.height;

        if (debug) {
            this.debug = new PIXI.Graphics();
            this.world.app.stage.addChild(this.debug);
        }

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
        this.sprite.anchor.x = this.data.centered ? 0.5 : 0;

        const pos = this.sprite.position;
        pos.x = this.data.x;
        pos.y = this.world.height - this.data.y - this.data.height;

        if (this.debug) {
            this.debug.clear();
            this.debug.lineStyle(1 / 48, 0xff00ff);
            const points = [
                new PIXI.Point(pos.x, pos.y),
                new PIXI.Point(pos.x + this.sprite.width, pos.y),
                new PIXI.Point(pos.x + this.sprite.width, pos.y + this.sprite.height),
                new PIXI.Point(pos.x, pos.y + this.sprite.height),
                new PIXI.Point(pos.x, pos.y)
            ];
            //console.log(points);
            this.debug.drawPolygon(points);

            this.debug.beginFill(0xffff00);
            this.debug.drawCircle(pos.x, pos.y, 0.1);
        }
    }
}
