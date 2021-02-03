package tv.bain.bainsocial.utils

sealed class MyState {

    object IDLE : MyState()

    class LOADING(val msg: String = "Loading") : MyState()

    class FINISHED(val msg: String = "Complete") : MyState()

    class ERROR(val msg: String? = "Undefined error") : MyState()

}
