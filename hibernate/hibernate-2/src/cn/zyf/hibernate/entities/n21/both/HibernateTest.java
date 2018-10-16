package cn.zyf.hibernate.entities.n21.both;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.jdbc.Work;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cn.zyf.hibernate.entities.News;
import cn.zyf.hibernate.entities.Pay;
import cn.zyf.hibernate.entities.Worker;
import cn.zyf.hibernate.entities.n21.Customer;
import cn.zyf.hibernate.entities.n21.Order;

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
	public void testDelete() {
		//在不设定级联关系的情况下，且 1 这一端的对象有 n 的对象在引用，则不能直接删除 1 这一端的对象
		Customer customer = session.get(Customer.class, 1);
		session.delete(customer);
	}
	
	@Test
	public void testUpdate() {
		Order order = session.get(Order.class, 1);
		order.getCustomer().setCustomerName("CC");
	}
	
	@Test
	public void testManyToOneGet() {
		//1.若查询 n 的一端的对象，默认情况下，只查询了 n 的一端的对象，没有查询关联的 1 的一端的对象
		Order order = session.get(Order.class, 1);
		System.out.println(order.getOrderName());
		
		System.out.println(order.getCustomer().getClass().getName());
		
		session.close();
		
		//2.在需要使用到关联的对象时，才发送对应的 SQL 语句
		Customer customer = session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		
		//3.在查询 Customer 对象时，由 n 的一端导航到 1 的一端时，
		//若此时 session 已经关闭，则默认情况下会发生 LazyInitializationException
		
		//4.获取 Order 对象时，默认情况下，其关联的对象是一个代理对象
	}
	
	@Test
	public void testManyToOne() {
		Customer customer = new Customer();
		customer.setCustomerName("BB");
		
		Order order1 = new Order();
		order1.setOrderName("ORDER-3");
		
		Order order2 = new Order();
		order2.setOrderName("ORDER-4");
		
		//设定关联关系
		order1.setCustomer(customer);
		order2.setCustomer(customer);
		
		//执行 save 操作:先插入 Customer 再插入 Order，3 条 insert
//		session.save(customer);
//		
//		session.save(order1);
//		session.save(order2);
		
		//先插入 Order，再插入 Customer 3 条 insert， 2 条 update
		//先插入 n 的一端，再插入 1 的一端，会多出 update 语句
		//因为在插入多的一端时，无法确定 1 的一端的外键值，所以只能再 1 的一端发送后进行 update 更新
		//推荐先插入 1 的一端 后插入 n 的一端
		session.save(order1);
		session.save(order2);
		
		session.save(customer);
	}
}
