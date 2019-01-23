import {World} from "./world";
import {Terrain} from "./terrain";

export class Text {
    world: World;

    sprite: PIXI.Text;
    data: Terrain.Text;

    debug?: PIXI.Graphics;

    constructor(world: World, data: Terrain.Text, debug: boolean = true) {
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

    get text() {
        return this.data.text;
    }

    set text(text: string) {
        this.data.text = text;
        this.sprite.text = text;
    }

    get style(): PIXI.TextStyleOptions {
        return this.data.style;
    }

    set style(style: PIXI.TextStyleOptions) {
        this.data.style = Object.assign(this.data.style, style);
        this.sprite.style = new PIXI.TextStyle(this.data.style);
    }

    get x() {
        return this.data.x;
    }

    set x(x: number) {
        this.data.x = x;
        this.sync();
    }

    get y() {
        return this.data.y;
    }

    set y(y: number) {
        this.data.y = y;
        this.sync();
    }

    get isCentered() {
        return this.data.isCentered
    }

    set isCentered(isCentered: boolean) {
        this.data.isCentered = isCentered;
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
        this.sprite.anchor.x = this.data.isCentered ? 0.5 : 0;
    }

    remove() {
        this.sprite.parent.removeChild(this.sprite);
    }
}
