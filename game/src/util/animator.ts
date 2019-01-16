import Texture = PIXI.Texture;
import AnimatedSprite = PIXI.extras.AnimatedSprite;

export class Animator {
    readonly id: string;

    private framesRetriever: () => Texture[];
    frames?: Texture[];

    settings: (sprite: AnimatedSprite) => void;

    constructor(id: string, framesRetriever: () => Texture[], settings: (sprite: AnimatedSprite) => void) {
        this.id = id;
        this.framesRetriever = framesRetriever;
        this.settings = settings;
    }

    onLoad() {
        this.frames = this.framesRetriever();
    }

    bind(sprite: AnimatedSprite): AnimatedSprite {
        sprite.textures = this.frames!;
        this.settings(sprite);
        return sprite;
    }
}

