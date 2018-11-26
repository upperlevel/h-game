package xyz.upperlevel.hgame.scenario.character;


public interface Character {
    /**
     * The name of the character.
     */
    String getName();

    /**
     * The {@code Character}'s texture path.
     */
    String getTexturePath();

    /**
     * Generates an instance of the {@link Character}'s {@link Actor}.
     * The {@link Actor} is the object that will populate the Scenario.
     */
    Actor personify(int id);
}
