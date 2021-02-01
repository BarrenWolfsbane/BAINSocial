package tv.bain.bainsocial.utils

import android.os.Build

object Version {

    /** Lower than SDK 23 */
    fun underMarshmallow() = Build.VERSION.SDK_INT < Build.VERSION_CODES.M

    /** SDK 26 or higher*/
    fun oreoOrOver() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    /** SDK 28 or higher */
    fun pieOrOver() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    /** SDK 29 or higher */
    fun qOrOver() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}