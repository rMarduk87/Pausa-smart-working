package rpt.tool.hybridwalk.utils.data.database.mappers

import androidx.room.Ignore
import rpt.tool.hybridwalk.utils.data.AppModel
import rpt.tool.hybridwalk.utils.data.DbModel

@Suppress("UNCHECKED_CAST")
abstract class Mappable {
    @Ignore
    open var mappers: ArrayList<rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper<rpt.tool.hybridwalk.utils.data.database.mappers.Mappable, *>> = arrayListOf()

    @Ignore
    inline fun <reified T> map(): T {
        return mappers.singleOrNull { it.destination == T::class.java }?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }

    open fun <T : AppModel> toAppModel(): T {
        return mappers.singleOrNull { it.destination.superclass == AppModel::class.java }
            ?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }

    open fun <T : DbModel> toDBModel(): T {
        return mappers.singleOrNull { it.destination.superclass == DbModel::class.java }
            ?.map(this) as? T
            ?: throw IllegalArgumentException("Mapper not found!")
    }
}

@Ignore
fun <T : rpt.tool.hybridwalk.utils.data.database.mappers.Mappable> T.addMapper(mapper: rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper<T, *>) {
    this.mappers.add(mapper as rpt.tool.hybridwalk.utils.data.database.mappers.ModelMapper<rpt.tool.hybridwalk.utils.data.database.mappers.Mappable, *>)
}