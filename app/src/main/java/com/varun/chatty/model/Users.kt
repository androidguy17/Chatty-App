package com.varun.chatty.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Users(var uid:String,var Username:String, var ProfileImageurl:String):Parcelable{

    constructor():this("","","")



}