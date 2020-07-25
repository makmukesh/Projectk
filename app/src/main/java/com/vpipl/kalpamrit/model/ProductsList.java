package com.vpipl.kalpamrit.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by tuanhai on 11/3/15.
 */
public class ProductsList {
    private String ProductType = "";
    private String ID = "";
    private String code = "";
    private String UID = "0";
    private String Name = "";
    private String ImagePath = "";
    private String NewMRP = "0";
    private String NewDP = "0";
    private String BV = "0";
    private String Description = "";
    private String Detail = "";
    private String KeyFeature = "";
    private String Discount = "0";
    private String DiscountPer = "0";
    private String IsshipChrg = "";
    private String ShipCharge = "0";
    private String CatID = "";
    private String RandomNo = "";
    private String Qty = "1";
    private String BaseQty = "1";
    private String AvailFor = "";
    private String OrderFor = "";
    private String IsKit = "";
    private String ParentProductID = "0";
    private String sellerCode = "";
    private boolean isDisplayDiscount = false;

    private String selectedSizeId = "0";
    private String selectedSizeName = "NA";
    private String selectedColorId = "0";
    private String selectedColorName = "NA";

    private String DeliveryAddressID = "";
    private String DeliveryAddressFirstName = "";
    private String DeliveryAddressLastName = "";
    private String DeliveryAddress = "";
    private String DeliveryAddress1 = "";
    private String DeliveryAddress2 = "";
    private String DeliveryAddressCountryID = "";
    private String DeliveryAddressCountryName = "";
    private String DeliveryAddressStateCode = "";
    private String DeliveryAddressStateName = "";
    private String DeliveryAddressDistrict = "";
    private String DeliveryAddressCity = "";
    private String DeliveryAddressPinCode = "";
    private String DeliveryAddressEmail = "";
    private String DeliveryAddressMob = "";
    private String DeliveryAddressEntryType = "";

    private Bitmap Imagebitmap = null;

    private List<ProductSize> sizeList;
    private List<ProductColor> colorList;

    private List<SubProductsList> subProductList;

    private String Weight = "";

    public String getProductType() {
        return ProductType;
    }

    public void setProductType(String ProductType) {
        this.ProductType = ProductType;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getcode() {
        return code;
    }

    public void setcode(String code) {
        this.code = code;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String Weight) {
        this.Weight = Weight;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
    }

    public Bitmap getImagebitmap() {
        return Imagebitmap;
    }

    public void setImagebitmap(Bitmap Imagebitmap) {
        this.Imagebitmap = Imagebitmap;
    }

    public String getNewMRP() {
        return NewMRP;
    }

    public void setNewMRP(String NewMRP) {
        this.NewMRP = NewMRP;
    }

    public String getNewDP() {
        return NewDP;
    }

    public void setNewDP(String NewDP) {
        this.NewDP = NewDP;
    }

    public String getBV() {
        return BV;
    }

    public void setBV(String BV) {
        this.BV = BV;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDetail() {
        return Detail;
    }

    public void setDetail(String Detail) {
        this.Detail = Detail;
    }

    public String getKeyFeature() {
        return KeyFeature;
    }

    public void setKeyFeature(String KeyFeature) {
        this.KeyFeature = KeyFeature;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String Discount) {
        this.Discount = Discount;
    }

    public String getDiscountPer() {
        return DiscountPer;
    }

    public void setDiscountPer(String DiscountPer) {
        this.DiscountPer = DiscountPer;
    }

    public String getIsshipChrg() {
        return IsshipChrg;
    }

    public void setIsshipChrg(String IsshipChrg) {
        this.IsshipChrg = IsshipChrg;
    }

    public String getShipCharge() {
        return ShipCharge;
    }

    public void setShipCharge(String ShipCharge) {
        this.ShipCharge = ShipCharge;
    }

    public String getCatID() {
        return CatID;
    }

    public void setCatID(String CatID) {
        this.CatID = CatID;
    }

    public String getRandomNo() {
        return RandomNo;
    }

    public void setRandomNo(String RandomNo) {
        this.RandomNo = RandomNo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }

    public String getBaseQty() {
        return BaseQty;
    }

    public void setBaseQty(String BaseQty) {
        this.BaseQty = BaseQty;
    }

    public String getAvailFor() {
        return AvailFor;
    }

    public void setAvailFor(String AvailFor) {
        this.AvailFor = AvailFor;
    }

    public String getOrderFor() {
        return OrderFor;
    }

    public void setOrderFor(String OrderFor) {
        this.OrderFor = OrderFor;
    }

    public String getParentProductID() {
        return ParentProductID;
    }

    public void setParentProductID(String ParentProductID) {
        this.ParentProductID = ParentProductID;
    }

    public String getsellerCode() {
        return sellerCode;
    }

    public void setsellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public boolean getIsDisplayDiscount() {
        return isDisplayDiscount;
    }

    public void setIsDisplayDiscount(boolean isDisplayDiscount) {
        this.isDisplayDiscount = isDisplayDiscount;
    }

    public String getselectedSizeId() {
        return selectedSizeId;
    }

    public void setselectedSizeId(String selectedSizeId) {
        this.selectedSizeId = selectedSizeId;
    }

    public String getselectedSizeName() {
        return selectedSizeName;
    }

    public void setselectedSizeName(String selectedSizeName) {
        this.selectedSizeName = selectedSizeName;
    }

    public String getselectedColorId() {
        return selectedColorId;
    }

    public void setselectedColorId(String selectedColorId) {
        this.selectedColorId = selectedColorId;
    }

    public String getselectedColorName() {
        return selectedColorName;
    }

    public void setselectedColorName(String selectedColorName) {
        this.selectedColorName = selectedColorName;
    }

    public String getDeliveryAddressID() {
        return DeliveryAddressID;
    }

    public void setDeliveryAddressID(String DeliveryAddressID) {
        this.DeliveryAddressID = DeliveryAddressID;
    }

    public String getDeliveryAddressFirstName() {
        return DeliveryAddressFirstName;
    }

    public void setDeliveryAddressFirstName(String DeliveryAddressFirstName) {
        this.DeliveryAddressFirstName = DeliveryAddressFirstName;
    }

    public String getDeliveryAddressLastName() {
        return DeliveryAddressLastName;
    }

    public void setDeliveryAddressLastName(String DeliveryAddressLastName) {
        this.DeliveryAddressLastName = DeliveryAddressLastName;
    }

    public String getDeliveryAddress() {
        return DeliveryAddress;
    }

    public void setDeliveryAddress(String DeliveryAddress) {
        this.DeliveryAddress = DeliveryAddress;
    }

    public String getDeliveryAddress1() {
        return DeliveryAddress1;
    }

    public void setDeliveryAddress1(String DeliveryAddress1) {
        this.DeliveryAddress1 = DeliveryAddress1;
    }

    public String getDeliveryAddress2() {
        return DeliveryAddress2;
    }

    public void setDeliveryAddress2(String DeliveryAddress2) {
        this.DeliveryAddress2 = DeliveryAddress2;
    }

    public String getDeliveryAddressCountryID() {
        return DeliveryAddressCountryID;
    }

    public void setDeliveryAddressCountryID(String DeliveryAddressCountryID) {
        this.DeliveryAddressCountryID = DeliveryAddressCountryID;
    }

    public String getDeliveryAddressCountryName() {
        return DeliveryAddressCountryName;
    }

    public void setDeliveryAddressCountryName(String DeliveryAddressCountryName) {
        this.DeliveryAddressCountryName = DeliveryAddressCountryName;
    }

    public String getDeliveryAddressStateCode() {
        return DeliveryAddressStateCode;
    }

    public void setDeliveryAddressStateCode(String DeliveryAddressStateCode) {
        this.DeliveryAddressStateCode = DeliveryAddressStateCode;
    }

    public String getDeliveryAddressStateName() {
        return DeliveryAddressStateName;
    }

    public void setDeliveryAddressStateName(String DeliveryAddressStateName) {
        this.DeliveryAddressStateName = DeliveryAddressStateName;
    }

    public String getDeliveryAddressDistrict() {
        return DeliveryAddressDistrict;
    }

    public void setDeliveryAddressDistrict(String DeliveryAddressDistrict) {
        this.DeliveryAddressDistrict = DeliveryAddressDistrict;
    }

    public String getDeliveryAddressCity() {
        return DeliveryAddressCity;
    }

    public void setDeliveryAddressCity(String DeliveryAddressCity) {
        this.DeliveryAddressCity = DeliveryAddressCity;
    }

    public String getDeliveryAddressPinCode() {
        return DeliveryAddressPinCode;
    }

    public void setDeliveryAddressPinCode(String DeliveryAddressPinCode) {
        this.DeliveryAddressPinCode = DeliveryAddressPinCode;
    }

    public String getDeliveryAddressEmail() {
        return DeliveryAddressEmail;
    }

    public void setDeliveryAddressEmail(String DeliveryAddressEmail) {
        this.DeliveryAddressEmail = DeliveryAddressEmail;
    }

    public String getDeliveryAddressMob() {
        return DeliveryAddressMob;
    }

    public void setDeliveryAddressMob(String DeliveryAddressMob) {
        this.DeliveryAddressMob = DeliveryAddressMob;
    }

    public String getDeliveryAddressEntryType() {
        return DeliveryAddressEntryType;
    }

    public void setDeliveryAddressEntryType(String DeliveryAddressEntryType) {
        this.DeliveryAddressEntryType = DeliveryAddressEntryType;
    }


    public List<ProductSize> getSizeList() {
        return this.sizeList;
    }

    public void setSizeList(List<ProductSize> sizeList) {
        this.sizeList = sizeList;
    }

    public List<ProductColor> getColorList() {
        return this.colorList;
    }

    public void setColorList(List<ProductColor> colorList) {
        this.colorList = colorList;
    }

    public List<SubProductsList> getSubProductList() {
        return this.subProductList;
    }

    public void setSubProductList(List<SubProductsList> subProductList) {
        this.subProductList = subProductList;
    }
}
