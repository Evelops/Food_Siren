package FoodSiren.model.data;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Entity // Food 테이블
public class Food implements Serializable, Comparable<Food> {

    @PrimaryKey(autoGenerate = true)
    // Food 테이블의 기본 키, autoGenerate한 성질을 갖게되어 객체 생성 시 id 값을 주지 않아도 DB에서 알아서 생성함
    private int id;

    private String name;

    private String imagePath;

    private int cnt;
    private int progress;

    private String regDate;
    private String expDate;

    private Boolean SwitchBoolean;
    private Boolean isNoticed = false;

    private String categoryName;

    public Food() {

    }

    public Food(String imagePath, String name, String regDate, String expDate, int cnt, String category_name) {
        this.imagePath = imagePath;
        this.name = name;
        this.regDate = regDate;
        this.expDate = expDate;
        this.cnt = cnt;
        this.SwitchBoolean = false;
        this.categoryName = category_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        return "Food{" +
                "name=" + name + "," +
                "imagePath=" + imagePath +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public Boolean getSwitchBoolean() {
        return SwitchBoolean;
    }

    public void setSwitchBoolean(Boolean SwitchBoolean) {
        this.SwitchBoolean = SwitchBoolean;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getNoticed() {
        return isNoticed;
    }

    public void setNoticed(Boolean noticed) {
        isNoticed = noticed;
    }

    public int getDiffDaysRegToCurrent() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        Date regDate = fm.parse(this.regDate);

        long diffInMillies = Math.abs(currentDate.getTime() - regDate.getTime());
        int diffDaysRegToCurrent = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return diffDaysRegToCurrent;
    }

    public int getDiffDaysCurrentToExp() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        Date expDate = fm.parse(this.expDate);

        long diffInMillies = Math.abs(expDate.getTime() - currentDate.getTime());
        int diffDaysCurrentToExp = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return diffDaysCurrentToExp;
    }

    public boolean isExpOver() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = new Date();
        Date expDate = fm.parse(this.expDate);

        return currentDate.getTime() >= expDate.getTime();
    }

    public int calcProgress() throws ParseException {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMdd");
        Date regDate = fm.parse(this.regDate);
        Date currentDate = new Date();
        Date expDate = fm.parse(this.expDate);

        long diffInMilliesRegToExp = Math.abs(expDate.getTime() - regDate.getTime());
        int diffDaysRegToExp = (int) TimeUnit.DAYS.convert(diffInMilliesRegToExp, TimeUnit.MILLISECONDS);

        long diffInMilliesRegToCurrent = Math.abs(currentDate.getTime() - regDate.getTime());
        int diffDaysRegToCurrent = (int) TimeUnit.DAYS.convert(diffInMilliesRegToCurrent, TimeUnit.MILLISECONDS);

        int progress = (diffDaysRegToCurrent * 100) / diffDaysRegToExp;

        return progress;
    }


    //검색기능을 위해 사용되는 matches 개념의 함수
    public boolean contains(String kwd) {
        if (name.contains(kwd))
            return true;
        else if (name.length() == 0)
            return true;
        else return categoryName.equals(kwd);
    }

    @Override
    public int compareTo(Food food) {
        return this.expDate.compareTo(food.expDate);
    }
}
