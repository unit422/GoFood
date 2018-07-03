package gofood.gofoodapp;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Created by akshay on 4/4/17.
 */

public class ShoppingItem implements Serializable{

    private String name, productID,price, quantity,image;
    //private int ;

    public ShoppingItem(String productId, String name, String price, String quantity,String image){
        this.productID = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public void setQuantity(String quantity){
        this.quantity = quantity;
    }
    public void setImage(String Image){
        this.image = Image;
    }
    public void setTitle(String title){
        this.name = title;
    }

    public void setPrice(String price){
        this.price = price;
    }
    public void setProductID(String productID){
        this.productID = productID;
    }

    public String getProductID() {
        return productID;
    }

    public String getTitle() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        //return NumberFormat.getCurrencyInstance().format(price);
        return price;
    }
}
