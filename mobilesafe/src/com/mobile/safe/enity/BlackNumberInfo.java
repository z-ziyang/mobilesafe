package com.mobile.safe.enity;

public class BlackNumberInfo {
	private Integer _id;
	private String number;
	private String mode;
	public BlackNumberInfo() {
		// TODO Auto-generated constructor stub
	}
	public BlackNumberInfo(String number, String mode) {
		// TODO Auto-generated constructor stub
		this.number = number;
		setMode(mode);
	}
	public Integer get_id() {
		return _id;
	}
	public void set_id(Integer _id) {
		this._id = _id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		if ("1".equals(mode) || "2".equals(mode) || "3".equals(mode)) {
			this.mode = mode;
		} else {
			this.mode = "1";
		}
	}
	@Override
	public String toString() {
		return "BlackNumberInfo [" + " number=" + number + ", mode=" + mode
				+ "]";
	}

}
