package com.example.lr1.data //package цього класу

//Імпорт бібліотек
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Звернення до бази даних з елементами Book
@Database(entities = [Book::class], version = 1)
abstract class BookDatabase : RoomDatabase() {

    //Отримання Data Access Object для доступу до таблиці
    abstract fun bookDao(): BookDao

    companion object {
        //Створення елементу INSTANCE, як екземпляру елемента таблиці
        @Volatile
        private var INSTANCE: BookDatabase? = null

        //Метод для отримання існуючої бд
        fun getDatabase(context: Context): BookDatabase {
            //Якщо бд вже створена, повертаємо її
            return INSTANCE ?: synchronized(this) {
                //Якщо ні, то створюємо нову
                Room.databaseBuilder(
                    context.applicationContext,
                    BookDatabase::class.java,    //Клас
                    "book_database"        //Назва
                ).build().also { INSTANCE = it } //Збереження у INSTANCE
            }
        }
    }
}