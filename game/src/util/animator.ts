import Texture = PIXI.Texture;

export interface Step {
    delay?: number;
    repeat?: boolean;
    frame?: {
        x: number;
        y: number;
        width: number;
        height: number;
    };
    on?(): void;
}

export interface Grid {
    speed?: number;
    repeat?: boolean;
    frames: {
        width: number;
        height: number;
        list: {x: number, y: number}[]
    }
    on?(frame: number): void;
}

export class Animator {
    readonly id: string;
    readonly spritesheet: string;

    steps: Step[] = [];

    constructor (id: string, spritesheet: string) {
        this.id = id;
        this.spritesheet = spritesheet;
    }

    generateFrames() {
        const spritesheet = PIXI.utils.TextureCache[this.spritesheet];
        const result: Texture[] = [];
        for (const step of this.steps) {
            if (step.frame) {
                const frame = new PIXI.Texture(spritesheet);
                frame.frame = new PIXI.Rectangle(step.frame.x, step.frame.y, step.frame.width, step.frame.height);
                result.push(frame);
            } else {
                if (frames.length == 0) {
                    throw "The first step must have a frame";
                }
                result.push(result[frames.length - 1]);
            }
        }
        return result;
    }

    add(step: Step): Animator {
        this.steps.push(step);
        return this;
    }

    grid(grid: Grid): Animator {
        for (let i = 0; i < grid.frames.list.length; i++) {
            const frame = grid.frames.list[i];
            this.add({
                delay: grid.speed || 1,
                repeat: grid.repeat || false,
                frame: {
                    x: frame.x * grid.frames.width,
                    y: frame.y * grid.frames.height,
                    width: grid.frames.width,
                    height: grid.frames.height,
                },
                on() {
                    if (grid.on) {
                        grid.on(i);
                    }
                }
            })
        }
        return this;
    }

    private setStep(step: Step, sprite: PIXI.extras.AnimatedSprite) {
        sprite.animationSpeed = step.delay || 1;
        sprite.loop = step.repeat || false;
    }

    play(sprite: PIXI.extras.AnimatedSprite) {
        sprite.stop();

        if (this.steps.length == 0) {
            throw "Playing Animator with no frames";
        }

        sprite.textures = this.generateFrames();

        this.setStep(this.steps[0], sprite);

        sprite.onFrameChange = frame => {
            const current = this.steps[frame];
            if (current.on) {
                current.on();
            }
            this.setStep(this.steps[(frame + 1) % this.steps.length], sprite);
        };

        sprite.play();
    }
}
