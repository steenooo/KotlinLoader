package dev.steyn.kotlinloader.api.event

interface Cancellable : org.bukkit.event.Cancellable {

    companion object {

        @JvmSynthetic
        fun default() : Cancellable = object : Cancellable {

            private var cancel: Boolean = false
            override fun setCancelled(cancel: Boolean) {
                this.cancel = cancel
            }

            override fun isCancelled(): Boolean = cancel
        }

    }
}