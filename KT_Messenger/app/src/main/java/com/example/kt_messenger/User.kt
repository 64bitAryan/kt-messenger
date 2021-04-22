import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profielImageUrl: String ): Parcelable {
    constructor(): this("","","")
}