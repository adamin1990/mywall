package com.lt.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Picture implements Parcelable{
	public  String pid="";
	public String pname="";
	public  String paddr="";
	public  String cid="";
	public String pdescription="";

	public Picture() {
		super();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPaddr() {
		return paddr;
	}

	public void setPaddr(String paddr) {
		this.paddr = paddr;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getPdescription() {
		return pdescription;
	}

	public void setPdescription(String pdescription) {
		this.pdescription = pdescription;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
	
		dest.writeString(pid);
		dest.writeString(pname);
		dest.writeString(paddr);
		dest.writeString(cid);
		dest.writeString(pdescription); 
	}
public static final Parcelable.Creator<Picture> CREATOR=new Creator<Picture>() {
	
	@Override
	public Picture[] newArray(int size) {
		
		return new Picture[size];
	}
	
	@Override
	public Picture createFromParcel(Parcel source) {
		Picture p=new Picture();
		 p.pid  = source.readString();
         p.pname = source.readString();
         p.paddr = source.readString();
         p.cid = source.readString();
         p.pdescription = source.readString();
		return p;
	}
};
}
