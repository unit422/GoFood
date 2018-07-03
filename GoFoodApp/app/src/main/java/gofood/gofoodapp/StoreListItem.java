package gofood.gofoodapp;

import java.io.Serializable;

/**
 * Created by dhoy__000 on 6/9/2018.
 */

public class StoreListItem implements Serializable {
    private String name, image;
    //private int ;

    public StoreListItem(String name,String image){
        this.name = name;
        this.image = image;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setImage(String Image){
        this.image = Image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

}