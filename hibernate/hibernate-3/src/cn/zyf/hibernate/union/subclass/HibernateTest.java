package cn.zyf.hibernate.union.subclass;

import java.util.List;

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
	public void testUpdate() {
		String hql = "UPDATE Person p SET p.age = 20";
		session.createQuery(hql).executeUpdate();
	}
	
	/**
	 * 优点：
	 * 1.无需使用辨别者列
	 * 2.子类独有的字段可以添加非 null 约束
	 * 
	 * 缺点:
	 * 1.存在冗余的字段
	 * 2.若更新父表的字段则更新效率较低
	 */
	
	/**
	 * 查询:
	 * 1.查询父类记录，需把父表和子表记录汇总到一起再做查询，性能稍差
	 * 2.对于子类记录，只需要查一张数据表
	 */
	@Test
	public void testQuery() {
		List<Person> persons = session.createQuery("From Person").list();
		System.out.println(persons.size());
		
		List<Student> students = session.createQuery("From Student").list();
		System.out.println(students.size());
	}
	
	/**
	 * 插入操作：
	 * 1.对于子类对象只需把记录插入到一张数据表中
	 */
	@Test
	public void testSave() {
		Person person = new Person();
		person.setAge(11);
		person.setName("AA");
		
		session.save(person);
		
		Student student = new Student();
		student.setAge(22);
		student.setName("BB");
		student.setSchool("MIT");
		
		session.save(student);
	}
}
