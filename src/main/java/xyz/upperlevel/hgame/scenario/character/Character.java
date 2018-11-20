package xyz.upperlevel.hgame.scenario.character;


public interface Character {
    /**
     * The {@code Character}'s name.
     * Lorenzo Rutayisire's name is Lorenzo.
     */
    String getName();

    /**
     * The {@code Character}'s surname.
     * Lorenzo Rossi's surname is Rossi.
     */
    String getSurname();

    /**
     * The {@code Character}'s nickname.
     * Lorenzo Rutayisire's nickname is Lory or Ruta.
     */
    String getNickname();

    /**
     * The {@code Character}'s formal name.
     */
    default String getFormalName() {
        return getName() + " " + getSurname();
    }

    /**
     * The {@code Character}'s texture path.
     */
    String getTexturePath();

    /**
     * Generates an instance of the {@link Character}'s {@link Actor}.
     * The {@link Actor} is the object that will populate the Scenario.
     */
    Actor personify();
}
