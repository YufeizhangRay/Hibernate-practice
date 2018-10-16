package cn.zyf.hibernate.one2one.primary;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HibernateTest {
	
	private SessionFactory sessionFactory;
	private Session session;
	private Transaction transaction;

	@BeforeEach
	public void init() {
		ServiceRegistry serviceRegistry  = new StandardServiceRegistryBuilder().configure().build();
		sessionFactory = new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
	}

	@AfterEach
	public void destory() {
		transaction.commit();
		session.close();
		sessionFactory.close();
	}
	
	@Test
	public void testGet2() {
		//在查询没有外键的实体对象时，使用的是左外连接查询，一并查询出其关联的对象
		//并已进行初始化
		Manager manager = session.get(Manager.class, 1);
		System.out.println(manager.getMgrName());
		System.out.println(manager.getDepartment().getDeptName());
	}
	
	@Test
	public void testGet() {
		//1.默认情况下对关联属性进行懒加载

		Department department = session.get(Department.class, 1);
		System.out.println(department.getDeptName());
		
		//2.所以会出现懒加载异常
//		Manager manager = department.getManager();
//		System.out.println(manager.getMgrName());
	}
	
	@Test
	public void testSave() {
		
		Department department = new Department();
		department.setDeptName("DEPT-DD");
		
		Manager manager = new Manager();
		manager.setMgrName("MGR-DD");
		
		//设定关系
		department.setManager(manager);
		manager.setDepartment(department);
		
		//保存操作
		//先插入哪一个都不会有多余的 update
		session.save(manager);
		session.save(department);
		
	}
}
