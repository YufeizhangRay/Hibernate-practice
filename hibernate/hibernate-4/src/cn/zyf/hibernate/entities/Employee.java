package cn.zyf.hibernate.entities;

public class Employee {

	private Integer id;
	private String name;
	private Float salary;
	private String email;
	
	private Department department;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getSalary() {
		return salary;
	}

	public void setSalary(Float salary) {
		this.salary = salary;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + "]";
	}

	public Employee(String email,Float salary, Department department) {
		super();
		this.salary = salary;
		this.email = email;
		this.department = department;
	}
	
	public Employee() {
		// TODO Auto-generated constructor stub
	}
}
