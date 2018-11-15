package xyz.upperlevel.hgame.scenario;

public class Storyline {
    public Scenario scenario;

    public Storyline() {
        scenario = new DystopianScene(); // first part of the game
    }

    public void update() {
        scenario.update();
    }

    public void render() {
        scenario.render();
    }
}
