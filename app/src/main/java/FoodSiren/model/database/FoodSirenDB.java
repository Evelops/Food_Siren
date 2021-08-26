package FoodSiren.model.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import FoodSiren.model.data.Category;
import FoodSiren.model.data.Food;
import FoodSiren.viewmodel.CategoryDao;
import FoodSiren.viewmodel.FoodDao;

// 앱이 갖고 있는 내부 DB 정보

@Database(entities = {Food.class, Category.class}, version = 1, exportSchema = false)
public abstract class FoodSirenDB extends RoomDatabase {

    private static FoodSirenDB INSTANCE;

    public static FoodSirenDB getDatabase(final Context context) {
        if (INSTANCE == null) { // 싱글톤 패턴, 앱 내에서 DB가 딱 한번 생성됨
            synchronized (FoodSirenDB.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FoodSirenDB.class, "FoodSiren_db")
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    db.execSQL("insert into category (name) values ('전체');");
                                    db.execSQL("insert into category (name) values ('냉장실');");
                                    db.execSQL("insert into category (name) values ('냉동실');");
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract FoodDao foodDao();

    public abstract CategoryDao categoryDao();

}
