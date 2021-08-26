package FoodSiren.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import FoodSiren.model.data.Food;

// DAO = Data Access Object, Model에게 접근

@Dao
public interface FoodDao {
    @Query("SELECT * FROM food ORDER BY expDate")
    LiveData<List<Food>> getAllUpdatedFoods();  // 특정 메소드의 타입에 LiveData를 씌워두면
    // 해당 데이터가 변경될 때(= 메소드의 리턴 값이 달라짐) 마다
    // 이를 사용하는 곳에서 자동으로 메소드를 다시 호출하게 함

    @Query("SELECT * FROM food ORDER BY expDate")
    List<Food> getAll();

    @Insert
    void insert(Food food);

    @Update
    void update(Food food);

    @Delete
    void delete(Food food);
}