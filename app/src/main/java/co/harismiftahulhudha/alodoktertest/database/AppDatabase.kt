package co.harismiftahulhudha.alodoktertest.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import co.harismiftahulhudha.alodoktertest.database.dao.ContentDao
import co.harismiftahulhudha.alodoktertest.database.dao.UserDao
import co.harismiftahulhudha.alodoktertest.di.ApplicationScope
import co.harismiftahulhudha.alodoktertest.helpers.FormatStringHelper
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentImageModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.ContentModel
import co.harismiftahulhudha.alodoktertest.mvvm.models.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [ContentModel::class, ContentImageModel::class, UserModel::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun contentDao(): ContentDao

    class Callback @Inject constructor(
        private val database: Provider<AppDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val userDao = database.get().userDao()
            val contentDao = database.get().contentDao()

            applicationScope.launch {
                generateDataTesting(userDao, contentDao)
            }
        }

        private suspend fun generateDataTesting(userDao: UserDao, contentDao: ContentDao) {
            userDao.insert(UserModel("Haris Miftahul Hudha", "087855736502", "me@harismiftahulhudha.co", FormatStringHelper.getMD5("123456")))
            contentDao.insert(ContentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
            1))
            contentDao.insert(ContentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                1))
            contentDao.insert(ContentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                1))
            contentDao.insert(ContentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                1))
            contentDao.insert(ContentModel("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                1))
            contentDao.insertImage(ContentImageModel("https://static.vecteezy.com/system/resources/previews/000/163/725/non_2x/content-creator-vector.png", 1))
            contentDao.insertImage(ContentImageModel("https://www.burlingtonseo.org/wp-content/uploads/desktop.png", 1))
            contentDao.insertImage(ContentImageModel("https://static.vecteezy.com/system/resources/previews/000/163/379/original/viral-content-creator-vector.jpg", 1))

            contentDao.insertImage(ContentImageModel("https://i1.wp.com/jayamotorbkt.com/wp-content/uploads/2020/10/vectorstock_21249960-Converted.png?resize=800%2C445&ssl=1", 2))
            contentDao.insertImage(ContentImageModel("https://i.pinimg.com/736x/0e/ff/be/0effbebe03b600ba59950d3584529fa3.jpg", 2))
            contentDao.insertImage(ContentImageModel("https://www.creativefabrica.com/wp-content/uploads/2020/04/06/Content-Creator-Flat-Vector-Illustration-Graphics-3804862-1-1-580x387.jpg", 1))

            contentDao.insertImage(ContentImageModel("https://static.vecteezy.com/system/resources/previews/000/163/379/original/viral-content-creator-vector.jpg", 3))
            contentDao.insertImage(ContentImageModel("https://www.creativefabrica.com/wp-content/uploads/2020/04/06/Content-Creator-Flat-Vector-Illustration-Graphics-3804862-1-1-580x387.jpg", 3))
            contentDao.insertImage(ContentImageModel("https://static.vecteezy.com/system/resources/previews/000/163/725/non_2x/content-creator-vector.png", 3))

            contentDao.insertImage(ContentImageModel("https://i.pinimg.com/736x/0e/ff/be/0effbebe03b600ba59950d3584529fa3.jpg", 4))
            contentDao.insertImage(ContentImageModel("https://www.creativefabrica.com/wp-content/uploads/2020/04/06/Content-Creator-Flat-Vector-Illustration-Graphics-3804862-1-1-580x387.jpg", 4))
            contentDao.insertImage(ContentImageModel("https://static.vecteezy.com/system/resources/previews/000/163/725/non_2x/content-creator-vector.png", 4))

            contentDao.insertImage(ContentImageModel("https://static.vecteezy.com/system/resources/previews/000/163/379/original/viral-content-creator-vector.jpg", 5))
            contentDao.insertImage(ContentImageModel("https://i.pinimg.com/736x/0e/ff/be/0effbebe03b600ba59950d3584529fa3.jpg", 5))
            contentDao.insertImage(ContentImageModel("https://www.creativefabrica.com/wp-content/uploads/2020/04/06/Content-Creator-Flat-Vector-Illustration-Graphics-3804862-1-1-580x387.jpg", 5))
        }
    }
}