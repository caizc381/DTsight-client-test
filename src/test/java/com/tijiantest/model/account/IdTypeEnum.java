package com.tijiantest.model.account;
//
//Source code recreated from a .class file by IntelliJ IDEA
//(powered by Fernflower decompiler)
//


public enum IdTypeEnum {
 UNKOWN(0,"未知"),
 IDCARD(1, "身份证"),
 PASSPORT(2, "护照"),
 OTHERS(3, "其他");

 private String name;
 private int code;

 private IdTypeEnum(int code, String name) {
     this.name = name;
     this.code = code;
 }

 public String getName() {
     return this.name;
 }

 public void setName(String name) {
     this.name = name;
 }

 public int getCode() {
     return this.code;
 }

 public void setCode(int code) {
     this.code = code;
 }
}
