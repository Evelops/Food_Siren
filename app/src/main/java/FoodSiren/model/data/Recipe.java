package FoodSiren.model.data;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Recipe implements Serializable {
    private String seq;
    private String name;
    private String method;
    private String type;
    private String image;
    private String ingredients;
    private ArrayList<String> manual = new ArrayList<>();

    @Override
    public String toString() {
        return "Recipe{" +
                "seq='" + seq + '\'' +
                ", name='" + name + '\'' +
                ", method='" + method + '\'' +
                ", type='" + type + '\'' +
                ", image='" + image + '\'' +
                ", ingredients='" + ingredients + '\'' +
                ", manual=" + manual +
                '}';
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getManual() {
        return manual;
    }

    public void setManual(ArrayList<String> manual) {
        this.manual = manual;
    }

    public void addManual(String manual) {
        this.manual.add(manual);
    }

    public boolean containsByName(String kwd) {
        if (name.contains(kwd))
            return true;
        else return name.length() == 0;
    }

    public boolean containsByType(String kwd) {
        return type.equals(kwd);
    }
}
