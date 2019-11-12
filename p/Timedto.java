package dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Timedto implements Serializable {


    @SerializedName("id")
    private String id;

    @SerializedName("time")
    private String time;

    @SerializedName("shift")
    private String shift;

    @SerializedName("category_id")
    private String category_id;

    @SerializedName("subcategory_id")
    private String subcategory_id;

	private boolean isSelected = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
}
