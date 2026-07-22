package rpt.tool.hybridwalk.utils.data.database.mappers

interface ModelMapper<Source, Destination> {
    val destination: Class<Destination>

    fun map(source: Source): Destination
}