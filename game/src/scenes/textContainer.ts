import * as Phaser from "phaser";
import Scene = Phaser.Scene;
import Container = Phaser.GameObjects.Container;
import Text = Phaser.GameObjects.Text;

export class TextContainer extends Container {
    padding: number;
    private nextY: number;

    constructor(scene: Scene, x: number, y: number, padding: number) {
        super(scene, x, y);
        this.active = true;

        this.padding = padding;
        this.nextY = y;
    }

    addLine(text: string, center: boolean = true, style: any): Text {
        const line = this.scene.add.text(0, this.nextY, text, style);

        if (center) {
            line.x -= line.displayWidth / 2.0;
        }

        this.nextY += line.displayHeight + this.padding;

        super.add(line);
        return line;
    }
}
