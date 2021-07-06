package com.econo21.pinning.location;

import android.os.Parcel;
import android.os.Parcelable;

public class Document implements Parcelable {
    private String place_name;
    private String distance;
    private String place_url;
    private String category_name;
    private String address_name;
    private String road_address_name;
    private String id;
    private String phone;
    private String category_group_code;
    private String category_group_name;
    private String x;
    private String y;

    public String getPlaceName() {
        return place_name;
    }

    public void setPlaceName(String placeName) {
        this.place_name = placeName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getPlaceUrl() {
        return place_url;
    }

    public void setPlaceUrl(String placeUrl) {
        this.place_url = placeUrl;
    }

    public String getCategoryName() {
        return category_name;
    }

    public void setCategoryName(String categoryName) {
        this.category_name = categoryName;
    }

    public String getAddressName() {
        return address_name;
    }

    public void setAddressName(String addressName) {
        this.address_name = addressName;
    }

    public String getRoadAddressName() {
        return road_address_name;
    }

    public void setRoadAddressName(String roadAddressName) {
        this.road_address_name = roadAddressName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategoryGroupCode() {
        return category_group_code;
    }

    public void setCategoryGroupCode(String categoryGroupCode) {
        this.category_group_code = categoryGroupCode;
    }

    public String getCategoryGroupName() {
        return category_group_name;
    }

    public void setCategoryGroupName(String categoryGroupName) {
        this.category_group_name = categoryGroupName;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.place_name);
        dest.writeString(this.distance);
        dest.writeString(this.place_url);
        dest.writeString(this.category_name);
        dest.writeString(this.address_name);
        dest.writeString(this.road_address_name);
        dest.writeString(this.id);
        dest.writeString(this.phone);
        dest.writeString(this.category_group_code);
        dest.writeString(this.category_group_name);
        dest.writeString(this.x);
        dest.writeString(this.y);
    }

    public Document() {
    }

    protected Document(Parcel in) {
        this.place_name = in.readString();
        this.distance = in.readString();
        this.place_url = in.readString();
        this.category_name = in.readString();
        this.address_name = in.readString();
        this.road_address_name = in.readString();
        this.id = in.readString();
        this.phone = in.readString();
        this.category_group_code = in.readString();
        this.category_group_name = in.readString();
        this.x = in.readString();
        this.y = in.readString();
    }

    public String toString(){
        return this.place_name + this.address_name;
    }

    public static final Parcelable.Creator<Document> CREATOR = new Parcelable.Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel source) {
            return new Document(source);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };
}
