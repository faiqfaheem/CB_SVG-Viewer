package svg.viewer.svg.converter.reader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey (autoGenerate = true)var id:Int,
    @ColumnInfo(name = "file_uri") var fileUri: String?,

)