package com.varun.chatty.model

class ChatMessage(val id:String,val text:String,val fromId:String,val toID:String,val timestamp:Long){

    constructor():this(id="",text="",fromId="",toID="",timestamp= 0)
}
