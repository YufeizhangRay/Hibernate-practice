package cn.zyf.hibernate.entities;

public class Pay {

	private int monthlyPay;
	private int yearPay;
	private int vocationWithPay;
	
	private Worker worker;
	
	public int getMonthlyPay() {
		return monthlyPay;
	}
	
	public void setMonthlyPay(int monthlyPay) {
		this.monthlyPay = monthlyPay;
	}
	
	public int getYearPay() {
		return yearPay;
	}
	
	public void setYearPay(int yearlyPay) {
		this.yearPay = yearlyPay;
	}
	
	public int getVocationWithPay() {
		return vocationWithPay;
	}
	
	public void setVocationWithPay(int vocationWithPay) {
		this.vocationWithPay = vocationWithPay;
	}

	public Worker getWorker() {
		return worker;
	}

	public void setWorker(Worker worker) {
		this.worker = worker;
	}
	

}
