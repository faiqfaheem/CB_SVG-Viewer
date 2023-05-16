package svg.viewer.svg.converter.reader.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertAll(users: User)


//    @Query("SELECT * FROM user where file_uri=:fileUri and file_type = :fileType")
//    fun getFilePath(fileUri:String , fileType: String): List<User>
//
    @Query("SELECT * FROM user")
    fun getAllUri(): List<User>
//
//    @Query("SELECT * FROM user where file_type = :fileType ")
//    fun getAllUriOnType(fileType:String): List<User>
//
    @Query("DELETE FROM user WHERE file_uri = :fileUri")
    fun deletePath(fileUri: String)
//
//    @Query("UPDATE user set file_type = :fileType where file_uri = :fileUri ")
//    fun updateType(fileUri: String , fileType: String)
//
    @Query("SELECT * FROM user where file_uri = :fileUri")
    fun getPathCheck(fileUri: String ): List<User>


}