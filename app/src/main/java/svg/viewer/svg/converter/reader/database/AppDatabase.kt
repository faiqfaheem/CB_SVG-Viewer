package svg.viewer.svg.converter.reader.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object{
        var INSTANCE: AppDatabase? = null

         fun getAppDatabase(context: Context): AppDatabase? {
            if (INSTANCE == null || !INSTANCE!!.isOpen) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "favourite_files.db")
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
}