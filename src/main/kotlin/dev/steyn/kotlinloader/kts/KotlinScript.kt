package dev.steyn.kotlinloader.kts

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KotlinScript(
        val onMissing: Action = Action.WARN,
        val message: String = ""
) {

    enum class Action {
        IGNORE,
        WARN,
        DISABLE
    }
}
