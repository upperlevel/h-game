package xyz.upperlevel.hgame

import com.badlogic.gdx.Gdx

fun runSync(r: () -> Unit) {
    Gdx.app.postRunnable(r)
}
