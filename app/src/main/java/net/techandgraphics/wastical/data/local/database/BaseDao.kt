package net.techandgraphics.wastical.data.local.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import androidx.room.Upsert

interface BaseDao<Table : Any> {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(table: List<Table>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(table: Table)

  @Update suspend fun update(table: Table)

  @Update suspend fun update(table: List<Table>)

  @Upsert suspend fun upsert(table: Table)

  @Upsert suspend fun upsert(table: List<Table>)

  @Delete suspend fun delete(table: Table)

  @Delete suspend fun delete(table: List<Table>)
}
