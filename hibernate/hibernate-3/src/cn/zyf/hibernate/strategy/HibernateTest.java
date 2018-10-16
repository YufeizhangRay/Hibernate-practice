package cn.zyf.hibernate.strategy;

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

import cn.zyf.hibernate.strategy.Customer;
import cn.zyf.hibernate.strategy.Order;

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
	public void testManyToOneStrategy() {
//		Order order = session.get(Order.class, 1);
//		System.out.println(order.getCustomer().getCustomerName());
		
		List<Order> orders = session.createQuery("From Order o").list();
		
		for(Order order: orders) {
			if(order.getCustomer()!=null) {
				System.out.println(order.getCustomer().getCustomerName());
			}
		}
		
		//1.lazy 取值为 proxy 和 false 分别代表对对应的属性采取延迟检索和立即检索
		//2.fetch 取值为 join 表示使用迫切左外连接的方式初始化 n 关联的 1 的一端属性
		//3.batch-size 属性需要设置在 1 一端的 class 元素中:
		//<class name="Customer" table="CUSTOMERS" lazy="true" batch-size="5">
		//作用:一次初始化 1 的这一端代理对象的个数
	}
	
	@Test
	public void testSetFetch2() {
		Customer customer = session.get(Customer.class, 1);
		System.out.println(customer.getOrders().size());
	}
	
	@Test
	public void testSetFetch() {
		List<Customer> customers = session.createQuery("From Customer").list();
		
		System.out.println(customers.size());
		
		for(Customer customer: customers) {
			if(customer.getOrders() != null) {
				System.out.println(customer.getOrders().size());
			}
		}
		
		//Set 集合的 fetch 属性：决定初始化 Order 集合的方式
		//1.默认值为 select，通过正常的方式初始化 set 元素
		//2.可以取值为 subselect，通过子查询的方式来初始化所有的 set 集合
		//  作为 where 子句的 in 条件出现，子查询查询所有 1 的一端的 ID 
		//  此时 lazy 有效，但是 batch-size 失效
		//3.若取值为 join 则:
		//3.1在加载 1 的一端的对象时，使用迫切左外连接(使用左外连接进行查询，且把集合属性进行初始化)的方式检索 n 的一端的集合属性
		//3.2忽略 lazy 属性
		//3.3 HQL 查询忽略 fetch = join 的取值
	}
	
	@Test
	public void testSetBatcnSize() {
		List<Customer> customers = session.createQuery("From Customer").list();
		
		System.out.println(customers.size());
		
		for(Customer customer: customers) {
			if(customer.getOrders() != null) {
				System.out.println(customer.getOrders().size());
			}
		}
		
		// Set 元素的 batch-size 属性: 设定一次初始化的 Set 集合的数量
	}
	
	@Test
	public void testOneToManyLevelStrategy() {
		Customer customer = session.get(Customer.class, 1);
		System.out.println(customer.getCustomerName());
		
		System.out.println(customer.getOrders().size());
		Order order = new Order();
		order.setOrderId(1);
		System.out.println(customer.getOrders().contains(order));
		
		Hibernate.initialize(customer.getOrders());
		
		// Set 的 Lazy 属性
		//1. 1-n 或 n-n 的检索策略默认使用懒加载检索策略
		//2. 可以通过修改 Set 的 Lazy 属性来修改默认的检索策略
		//   默认为 true 不建议设置为 false
		//3. Lazy 还可以设置为 extra ，增强的延迟检索，该取值会尽可能的延迟初始化的时机
	}
	
	@Test
	public void testClassLevelStrategy() {
		Customer customer = session.load(Customer.class, 1);
		System.out.println(customer.getClass());
		
		System.out.println(customer.getCustomerId());
		System.out.println(customer.getCustomerName());
	}
}
