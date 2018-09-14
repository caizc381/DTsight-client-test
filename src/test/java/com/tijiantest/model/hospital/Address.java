package com.tijiantest.model.hospital;

//
//Source code recreated from a .class file by IntelliJ IDEA
//(powered by Fernflower decompiler)
//

public class Address {
 private Integer id;
 private String province;
 private String city;
 private String district;
 private String address;
 private String latitude;
 private String longitude;

 public Address() {
 }

 public Integer getId() {
     return this.id;
 }

 public void setId(Integer id) {
     this.id = id;
 }

 public String getProvince() {
     return this.province;
 }

 public void setProvince(String province) {
     this.province = province;
 }

 public String getCity() {
     return this.city;
 }

 public void setCity(String city) {
     this.city = city;
 }

 public String getDistrict() {
     return this.district;
 }

 public void setDistrict(String district) {
     this.district = district;
 }

 public String getAddress() {
     return this.address;
 }

 public void setAddress(String address) {
     this.address = address;
 }

 public String getLatitude() {
     return this.latitude;
 }

 public void setLatitude(String latitude) {
     this.latitude = latitude;
 }

 public String getLongitude() {
     return this.longitude;
 }

 public void setLongitude(String longitude) {
     this.longitude = longitude;
 }

 public String getFullAddress() {
     StringBuilder sb = new StringBuilder();
     if(this.province != null) {
         sb.append(this.province);
     }

     if(this.city != null && this.province != null && !this.city.equals(this.province)) {
         sb.append(this.city);
     }

     if(this.district != null) {
         sb.append(this.district);
     }

     if(this.address != null) {
         sb.append(this.address);
     }

     return sb.toString();
 }
}
