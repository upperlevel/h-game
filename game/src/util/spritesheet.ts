export namespace SpritesheetUtil {
    import Texture = PIXI.Texture;
    import BaseTexture = PIXI.BaseTexture;

    export function horizontal(spritesheet: BaseTexture, frameWidth: number, frameHeight: number, y: number, count: number): Texture[] {
        const frames = [];
        for (let x = 0; x < count && x < spritesheet.width / frameWidth; x++) {
            const texture = new PIXI.Texture(spritesheet);
            texture.frame = new PIXI.Rectangle(x * frameWidth, y * frameHeight, frameWidth, frameHeight);
            frames.push(texture);
        }
        return frames;
    }

}
