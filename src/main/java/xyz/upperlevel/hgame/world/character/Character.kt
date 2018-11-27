package xyz.upperlevel.hgame.world.character


interface Character {
    /**
     * The name of the character.
     */
    val name: String

    /**
     * The `Character`'s texture path.
     */
    val texturePath: String

    /**
     * Generates an instance of the [Character]'s [Actor].
     * The [Actor] is the object that will populate the Scenario.
     */
    fun personify(id: Int): Actor
}
