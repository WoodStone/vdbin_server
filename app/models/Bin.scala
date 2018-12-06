package models

import java.util.UUID

import scalikejdbc._
import skinny.orm.{SkinnyCRUDMapperWithId}

case class BinUUID(uuid: UUID)

case class Bin(uuid: UUID, timestamp: String, data: String, src: String)

object Bin extends SkinnyCRUDMapperWithId[UUID, Bin] {
  override lazy val defaultAlias = createAlias("Bin")
  private[this] lazy val b = defaultAlias

  override def primaryKeyFieldName: String = "uuid"

  override def useExternalIdGenerator: Boolean = true

  override def generateId: UUID = UUID.randomUUID()

  override def idToRawValue(id: UUID): UUID = id

  override def rawValueToId(value: Any): UUID = UUID.fromString(value.toString)

  override def extract(rs: WrappedResultSet, rn: ResultName[Bin]) = new Bin(
    rawValueToId(rs.any(rn.uuid)),
    rs.string(rn.timestamp),
    rs.string(rn.data),
    rs.string(rn.src)
  )

  def create(bin: Bin): UUID = {
    createWithNamedValues(
      column.timestamp -> bin.timestamp,
      column.data -> bin.data,
      column.src -> bin.src
    )
  }

}
