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
        this.data.style = Object.assign(data.style, {fontSize: this.data.height * 128});

        this.sprite = new PIXI.Text(data.text, this.data.style);
        this.world.app.stage.addChild(this.sprite);

        this.sprite.scale.x = this.sprite.scale.y = 1 / (this.data.height * 128) * this.data.height;
        this.sync();

        if (debug) {
            this.debug = new PIXI.Graphics();
            this.world.app.stage.addChild(this.debug);
        }
    }

    set text(text: string) {
        this.data.text = text;
        this.sprite.text = text;
    }

    set style(style: PIXI.TextStyleOptions) {
        this.data.style = Object.assign(this.data.style, style);
        this.sprite.style = new PIXI.TextStyle(this.data.style);
    }

    set x(x: number) {
        this.data.x = x;
        this.sync();
    }

    set y(y: number) {
        this.data.y = y;
        this.sync();
    }

    set centered(centered: boolean) {
        this.data.centered = centered;
        this.sync();
    }

    get height(): number {
        return this.data.height;
    }

    set height(height: number) {
        this.data.height = height;
        this.sync();
    }

    private sync() {
        this.sprite.x = this.data.x;
        this.sprite.y = this.world.height - this.data.y - this.data.height;
        this.sprite.anchor.x = this.data.centered ? 0.5 : 0;

        if (this.debug) {
            const x = this.sprite.x;
            const y = this.sprite.y;

            this.debug.clear();
            this.debug.lineStyle(1 / 48, 0xff00ff);
            const points = [
                new PIXI.Point(x, y),
                new PIXI.Point(x + this.sprite.width, y),
                new PIXI.Point(x + this.sprite.width, y + this.sprite.height),
                new PIXI.Point(x, y + this.sprite.height),
                new PIXI.Point(x, y)
            ];
            //console.log(points);
            this.debug.drawPolygon(points);

            this.debug.beginFill(0xffff00);
            this.debug.drawCircle(x, y, 0.1);
        }
    }
}
