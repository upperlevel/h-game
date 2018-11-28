package xyz.upperlevel.hgame.world.sequence

import com.badlogic.gdx.Gdx
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class Sequence {
    private val incomingSteps = HashMap<Step, Trigger>()
    private val steps = ArrayList<Step>()
    private val children = ArrayList<Sequence>()

    /**
     * Appends an action to the current sequence.
     * To continue the sequence, the action is supposed to call the function [Sequence.Step.next].
     */
    fun append(action: (Step) -> Unit): Sequence {
        steps.add(object : Step(steps.size) {
            override fun start() {
                action(this)
            }
        })
        return this
    }

    fun act(action: () -> Unit): Sequence {
        append { step ->
            action()
            step.next(Triggers.NONE)
        }
        return this
    }

    fun pressKey(key: Int, action: () -> Unit): Sequence {
        append { step ->
            pressKey(key, action)
            action()
            step.next { Gdx.input.isKeyPressed(key) }
        }
        return this
    }

    fun pressKeyOnce(key: Int, action: () -> Unit): Sequence {
        append { step ->
            pressKeyOnce(key, action)
            action()
            step.next { Gdx.input.isKeyJustPressed(key) }
        }
        return this
    }

    fun delay(delay: Long): Sequence {
        append { step -> step.next(Triggers.sleep(delay)) }
        return this
    }

    private fun issueRepeat(root: Step, action: (Step) -> Unit, each: Long) {
        append { step ->
            action(root)
            if (!root.isNextCalled) {
                delay(each)
                issueRepeat(root, action, each)
                step.next(Triggers.NONE)
            } else {
                dismiss()
            }
        }
    }

    fun repeat(action: (Step) -> Unit, each: Long): Sequence {
        append { root ->
            // We create a sub-sequence that will handle the
            // repeating steps. If `next` is called within
            // the `action`, the first level sequence skips to the next step
            // and the inner sequence is dismissed.
            val inner = Sequence()
            inner.issueRepeat(root, action, each)
            children.add(inner)
            inner.play()
        }
        return this
    }

    fun repeat(action: (Step, Int) -> Unit, each: Long, times: Int): Sequence {
        val wrapped = AtomicInteger()
        repeat({ step ->
            action(step, wrapped.get())
            // If 'times' is negative, it'll repeat the loop infinitely,
            // The counter will increment starting from 0.
            if (wrapped.incrementAndGet() >= times && times >= 0) {
                step.next(Triggers.NONE)
            }
        }, each)
        return this
    }

    fun update() {
        for ((step, value) in HashMap(incomingSteps)) {
            if (value()) {
                step.start()
                incomingSteps.remove(step)
            }
        }
        children.forEach { it.update() }
    }

    fun play(): Sequence {
        if (steps.size > 0) {
            steps[0].start()
        }
        return this
    }

    fun dismiss() {
        children.forEach { it.dismiss() }
        sequences.remove(this)
    }

    abstract inner class Step(private val position: Int) {
        var isNextCalled: Boolean = false
            private set

        fun next(trigger: Trigger) {
            isNextCalled = true
            if (position >= steps.size - 1) {
                return
            }
            val next = steps[position + 1]
            incomingSteps[next] = trigger
        }

        abstract fun start()
    }

    companion object {
        private val sequences = ArrayList<Sequence>()
        private val logger = LogManager.getLogger()

        fun create(): Sequence {
            val sequence = Sequence()
            sequences.add(sequence)
            return sequence
        }

        fun updateAll() {
            for (sequence in ArrayList(sequences)) {
                sequence.update()
            }
        }

        fun dismissAll() {
            for (sequence in ArrayList(sequences)) {
                sequence.dismiss()
            }
        }
    }
}
