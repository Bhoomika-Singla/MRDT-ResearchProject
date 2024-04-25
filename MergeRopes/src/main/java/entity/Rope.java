package entity;

import util.Helper;

import java.util.Objects;

public class Rope {
    int length;
    String data;
    String userDesc;
    Rope left;
    Rope right;
    String hashVal;

    String contentHash;

    String identifier;

    public Rope(String data, String userDesc){
        this(data, null, null, userDesc);
    }

    public Rope(String data, Rope left, Rope right, String userDesc) {
        this.data = data;
        this.left = left;
        this.right = right;
        String hashedString = "";
        if(left != null)    hashedString = hashedString + left.hashVal;
        if(data.length() != 0)  hashedString += Helper.hashString(data);
        if(right != null)   hashedString = hashedString + right.hashVal;
//        this.hashVal = Helper.hashString(hashedString);
        this.hashVal = hashedString;
        this.setLength();
        this.setUserDesc(userDesc);
    }

    public String getUserDesc() {
        return userDesc;
    }

    public void setUserDesc(String userDesc) {
        this.userDesc = userDesc;
    }

    public String getHashVal() {
        return hashVal;
    }

    public void setLength() {
        if(this.left == null && this.right == null){
            this.length = this.data.length();
        } else if(this.left != null){
            Rope it = this.left.right;
            int totalLen = this.left.length;
            while(it != null){
                totalLen += it.length;
                it = it.right;
            }
            this.length = totalLen;
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        String hashedString = "";
        if(left != null)    hashedString = hashedString + left.hashVal;
        if(data.length() != 0)  hashedString += Helper.hashString(data);
        if(right != null)   hashedString = hashedString + right.hashVal;
//        this.hashVal = Helper.hashString(hashedString);
        this.hashVal = hashedString;
        this.setLength();
    }

    public Rope getLeft() {
        return left;
    }

    public void setLeft(Rope left) {
        this.left = left;
        this.setLength();
        String hashedString = "";
        if(left != null)    hashedString = hashedString + left.hashVal;
        if(data.length() != 0)  hashedString += Helper.hashString(data);
        if(right != null)   hashedString = hashedString + right.hashVal;
//        this.hashVal = Helper.hashString(hashedString);
        this.hashVal = hashedString;
    }

    public Rope getRight() {
        return right;
    }

    public void setRight(Rope right) {
        this.right = right;
        String hashedString = "";
        if(left != null)    hashedString = hashedString + left.hashVal;
        if(data.length() != 0)  hashedString += Helper.hashString(data);
        if(right != null)   hashedString = hashedString + right.hashVal;
//        this.hashVal = Helper.hashString(hashedString);
        this.hashVal = hashedString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rope)) return false;
        Rope rope = (Rope) o;
        return Objects.equals(hashVal, rope.hashVal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashVal);
    }

    public Rope split(int start, int end){

        if(this.length == end){
            return (this.left == null) ? cloneRope(this) : cloneRope(this.left);
        }

        if(this.length>end){
            Rope left = this.left;
            Rope right = this.right.split(start, end-length);
            Rope newRope = new Rope("","");
            newRope.setLeft(left);
            newRope.setRight(right);
            return newRope;
        } else{
            return this.left.split(start, end);
        }
    }

    //TODO : Will return rope starting from this index to end index
    public Rope split(int start){
        return null;
    }

    public static Rope cloneRope(Rope rope){
        if(rope == null)    return null;
        Rope clonedRope = new Rope(rope.getData(),rope.getUserDesc());
        clonedRope.setLeft(cloneRope(rope.getLeft()));
        clonedRope.setRight(cloneRope(rope.getRight()));
        return clonedRope;
    }
}
