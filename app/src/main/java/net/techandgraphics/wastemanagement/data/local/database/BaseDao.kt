package net.techandgraphics.wastemanagement.data.local.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Upsert

interface BaseDao<Table : Any> {
  @Insert suspend fun insert(table: List<Table>)

  @Insert suspend fun insert(table: Table)

  @Update suspend fun update(table: Table)

  @Update suspend fun update(table: List<Table>)

  @Upsert suspend fun upsert(table: Table)

  @Upsert suspend fun upsert(table: List<Table>)

  @Delete suspend fun delete(table: Table)

  @Delete suspend fun delete(table: List<Table>)
}
