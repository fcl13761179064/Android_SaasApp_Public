package com.ayla.hotelsaas.bean

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/*
    编组入口白名单
 */
data class MarshallEntryBean(
    val deviceCategory: String,
    val createTime: Long,
    val nickname: String,
    val cuId: Int,
    val deviceId: String,
    val deviceUseType: String,
    val firmwareVersion: String,
    val macAddress: String,
    val connectionStatus: String,
    val subDeviceKey: String,
    val nodeType: String,
    val pid: String,
    val pointName: String,
    val regionId: Long,
    val scopeId: Long,
    val regionName: String,
    val h5Url: String,
    val actualIcon: String,
    val bindType:Int
)

data class GroupRequestBean(
    val groupDeviceList: List<GroupDeviceBean>,
    val groupName: String,
    val subGroupIdList: List<String>,
    val scopeId: Long,
    val rootGatewayDeviceId: String? = null,
    val groupType: Int, //组类型：1 设备组，组元素只有设备； 2 组组， 组元素只有组；3 混合组：组元素既有设备也有组
    val regionId: Long,
    val groupId: String? = null,
    val siteType: Int = GroupSiteTypeEnum.EDGE.code,  //组存储位置，1： 边缘，2：云端
    val filter: String? = GroupFilterEnum.FILTER_COLOR_LIGHT.value
)

@Parcelize
data class GroupDeviceBean(
    val deviceId: String,
    val subDeviceKey: String?
):Parcelable

/**
 * 组存储位置，1： 边缘，2：云端
 */
enum class GroupSiteTypeEnum(val code: Int) {
    EDGE(1),
    CLOUD(2)
}


/**
 * 组类型：1 设备组，组元素只有设备；
 * 2 组组， 组元素只有组；
 * 3 混合组：组元素既有设备也有组
 * 4:门面组，该类型组会隐藏内部实际逻辑（目前只有调光开关使用）
 */
enum class GroupTypeEnum(val code: Int) {
    DEVICE_GROUP(1),
    GROUP_GROUP(2),
    MIXED_GROUP(3),
    FACADE_GROUP(4)
}

/**
 * group Abilities中包含 color，传limitGroupDeviceEndpoint1st，否则limitGroupDeviceEndpoint2nd
 */
enum class GroupFilterEnum(val value: String) {
    FILTER_COLOR_LIGHT("limitGroupDeviceEndpoint1st"),
    FILTER_WHITE_LIGHT("limitGroupDeviceEndpoint2nd")
}

@Parcelize
data class GroupDetailBean(
   // val groupAbilities: List<GroupAbility>,
    val groupDeviceList: List<GroupDeviceItem>,
    val productLabels: String?,
    val rootGatewayDeviceId: String?,
    /** @see ProductLabelEnum 编组分类*/
    val connectionStatus: String?,
    val groupId: String?,
    val groupName: String?,
    val groupType: Int?,
    val gatewayDeviceId: String?,
    val siteType: Int?,
    val scopeId: String?,
    val regionId: String?,
    val roomName: String?
) : Parcelable {
   // fun hasSwitchAbility() = groupAbilities.any { it.abilityCode == Constance.SWITCH_ABILITY_CODE }
   // fun isSwitchGroup() = Boolean.equals(productLabels?.contains(ProductLabelEnum.SWITCH.code))
}

/**
 * 编组分类 1 - 照明类 2- 普通开关类 3 - 调光开关
 */
enum class ProductLabelEnum(val code: String) {
    LIGHT("1000"),
    SWITCH("2000"),
    DLT("3000")
}

@Parcelize
data class SubGroupBean(
    val connectionStatus: String?,
    val createTime: Long?,
    val gatewayDeviceId: String,
    val groupDeviceList: List<GroupItem>?,
    val groupId: String,
    val groupName: String,
    val groupType: Int,
    val id: String?,
    val modifyTime: Int?,
    val productLabels: String?,
    val roomId: String?,
    val roomName: String?,
    val siteType: Int?,
    val subGroupIdList: List<String>?
) : Parcelable

@Parcelize
data class GroupAbility(
    val abilityCode: String,
    val abilityType: Int,
    val abilityValueType: Int,
    val abilityValues: List<AbilityValue>,
    val description: String,
    override var displayName: String,
    val version: String
) : Parcelable, IAbilityItem


/**
 * remark:abilitySubCode和setup一组，联动传参格式为："v1:"${abilitySubCode}":setup的值}"
 * 否则为“v1:${value}”
 */
@Parcelize
data class AbilityValue(
    val abilitySubCode: String?,
    val dataType: Int,
    val value: String?,
    val displayName: String,
    val setup: SetupBean?
) : Parcelable

@Parcelize
data class SetupBean(
    val max: String?,
    val min: String?,
    val step: String?,
    val unit: String?,
    val unitName: String?
) : Parcelable

interface IAbilityItem {
    var displayName: String
}


@Parcelize
data class  GroupDeviceItem(
    val deviceId: String?,
    val cuId: Long?,
    val deviceCategory: String?,
    val nickname:String?,
    val connectionStatus: String?,
    val deviceUseType: String?,
    val pid: String?,
    val bindType: String?,
    val subDeviceKey: String?,
    val regionId: String?,
    val regionName: String?,
    val actualIcon: String?,
    val hasH5: String?,
    val domain: String?,
    val h5Url: String?,
    val hasBind: Int?,
    val isPurposeDevice: Int?,
):Parcelable


@Parcelize
data class CreateMarshallSuccessInfo(
    val groupId: String,
    val groupType: Int,
    val rootGatewayDeviceId: String,
    val groupName:String,
    val productLabels: String,
    val scopeId: String,
    val regionId: String,
    val groupDeviceList: List<GroupDeviceBean>,
    val subGroupDetailList: String?,
    val connectionStatus: String?,
    val siteType: Int,
    val groupAbilities: List<GroupAbility>,
    val createTime: Long,
    val modifyTime: Long
):Parcelable
