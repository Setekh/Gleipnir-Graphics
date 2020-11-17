package eu.corvus.corax

import org.koin.core.context.GlobalContext

val Koin by lazy {
    GlobalContext.get().koin
}