package com.ayla.hotelsaas.bean

import java.io.Serializable

/**
 * 房间管理bean
 */
data class RoomBean(
    /**
     * id : 1298099766328098903
     * contentName : 施工房间1号
     */
    var id: Long = 0,
    var roomName: String = ""
):Serializable