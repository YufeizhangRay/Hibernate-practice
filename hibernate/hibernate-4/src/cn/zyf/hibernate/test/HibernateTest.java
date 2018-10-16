package cn.zyf.hibernate.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

import javax.enterprise.inject.New;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import cn.zyf.hibernate.dao.DepartmentDao;
import cn.zyf.hibernate.entities.Department;
import cn.zyf.hibernate.entities.Employee;
import cn.zyf.hibernate.hibernate.HibernateUtils;


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
	
	
	public void testBatch() {
		session.doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				//通过 JDBC 原生的 API 进行操作，效率最高，速度最快！
				
			}
		});
	}
	
	@Test
	public void testManageSession() {
		
		//获取 Session
		//开启事务
		Session session = HibernateUtils.getInstance().getSession();
		System.out.println("-->"+session.hashCode());
		Transaction transaction = session.beginTransaction();
		
		DepartmentDao departmentDao = new DepartmentDao();
		
		Department department = new Department();
		department.setName("ray");
		
		departmentDao.save(department);
		departmentDao.save(department);
		departmentDao.save(department);
		
		//若 Session 是由 thread 来管理的，则在提交或回滚事务时，已经自动关闭session了
		transaction.commit();
		System.out.println(session.isOpen());
	} 
	
	@Test
	public void testUnipue() {
		String hql = "SELECT d.departmentName FROM Department d";
		List<String> list = session.createQuery(hql).getResultList();
		//set:无序，无重复
		//list:有序，有重复
		list = new ArrayList<>(new HashSet<>(list));
		System.out.println(list.size());
		System.out.println(list);
	}
	
	@Test
	public void testQueryIterate() {
		Department department1 = session.get(Department.class, 80);
		System.out.println(department1.getName());
		System.out.println(department1.getEmpolyees().size());
		
		Query<Employee> query = session.createQuery("FROM Employee e WHERE e.department.id = 80");
//		List<Employee> employees = query.getResultList();
//		System.out.println(employees.size());
		
		Iterator<Employee> empIterator = query.iterate();//仅作了解
		while (empIterator.hasNext()) {
			System.out.println(empIterator.next().getName());
		}
	}
	
	@Test
	public void testTimeStampCache() {
		Query<Employee> query = session.createQuery("From Employee");
		query.setCacheable(true);
		
		List<Employee> employees = query.getResultList();
		System.out.println(employees.size());
		
		Employee employee = session.get(Employee.class, 100);
		employee.setSalary(30000f);
		
		employees = query.getResultList();
		System.out.println(employees.size());
	}
	
	
	//查询缓存，依赖于二级缓存
	@Test
	public void testQueryCache() {
		Query<Employee> query = session.createQuery("From Employee");
		//开启查询二级缓存第一步
		query.setCacheable(true);
		//第二步需要在 cfg 文件中配置 <property name="cache.use_query_cache">true</property>
		
		List<Employee> employees = query.getResultList();
		System.out.println(employees.size());
		
		employees = query.getResultList();
		System.out.println(employees.size());
		
//		Criteria criteria = session.createCriteria(Employee.class);
//		criteria.setCacheable(true);
	}
	
	@Test
	public void testCollectionSecondLevelCache() {
		Department department1 = session.get(Department.class, 80);
		System.out.println(department1.getName());
		System.out.println(department1.getEmpolyees().size());
		
		transaction.commit();
		session.close();
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
		Department department2 = session.get(Department.class, 80);
		System.out.println(department2.getName());
		System.out.println(department2.getEmpolyees().size());
	}
	
	@Test
	public void testHibernateSecondLevelCache() {
		Employee employee1 = session.get(Employee.class, 100);
		System.out.println(employee1.getName());
		
		transaction.commit();
		session.close();
		
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();
		
		Employee employee2 = session.get(Employee.class, 100);
		System.out.println(employee2.getName());
	}
	
	
	//批量删除
	@Test
	public void testHQLUpdate() {
		String hql = "DELETE FROM Department d WHERE d.id > :id";
		
		int rs = session.createQuery(hql).setParameter("id",10).executeUpdate();
		
		System.out.println(rs);
	}
	
	@Test
	public void testNativeaSQL2() {
		String sql = "INSERT INTO zyf_department VALUES(?,?)";
		Query query = session.createNativeQuery(sql)
							.setParameter(1, 10)
							.setParameter(2, 1);
		int rs = query.executeUpdate();
		System.out.println(rs);
	}
	
	@Test
	public void testNativeaSQL() {
		String sql = "SELECT * FROM zyf_department WHERE id > ?";
		NativeQuery<Object[]> query = session.createNativeQuery(sql);
		//原生 SQL 占位符从 1 开始，hibernate 从 0 开始
		List<Object[]> list = query.setParameter(1, 10).getResultList();
		for(Object[] obj:list ) {
			System.out.println(obj);
		}
	}
	
	@Test
	public void testQBC2() {
		//1.创建 CriteriaBuilder 对象
			CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
				
			//2.创建 CriteriaQuery 对象
			CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
				
			//3.获取 Root 对象、
			Root<Employee> root=criteriaQuery.from(Employee.class);
			
			//4.构造查询条件
			Predicate predicate1 = criteriaBuilder.gt(root.get("email"), 100);
			Predicate predicate2 = criteriaBuilder.lt(root.get("id"),1);
			Predicate predicate3 = criteriaBuilder.equal(root.get("id"),1);
			Predicate predicate = criteriaBuilder.and(predicate1,predicate2,predicate3);
		//	Predicate predicate4 = criteriaBuilder.or(predicate,predicate3);
			
			criteriaQuery.select(root.get("id"));//若放入 root 则取出所有字段
			criteriaQuery.where(predicate);//默认 and 方法
			
			//5.获取 Query 对象
			Query<Employee> query = session.createQuery(criteriaQuery);
			
			//6.执行查询
			List<Employee> employees = query.getResultList();
			System.out.println(Arrays.toString(employees.toArray()));
	}
	
	@Test
	public void testQBC() {
		//1.创建 CriteriaBuilder 对象
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		//2.创建 CriteriaQuery 对象
		CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
		
		//3.获取 Root 对象
		Root<Employee> root=criteriaQuery.from(Employee.class);
		
		//4.添加查询条件：
		criteriaQuery.select(root);
		criteriaQuery.where(criteriaBuilder.like(root.get("email"), "ray"),criteriaBuilder.equal(root.get("id"),1));
		//select * from Employee where email like 'ray' and id = 1;
		criteriaQuery.where(criteriaBuilder.lt(root.get("salary"), 5000));
		
		//5.获取 Query 对象
		Query<Employee> query = session.createQuery(criteriaQuery);
		
		//6.执行查询
		List<Employee> employees = query.getResultList();
		System.out.println(Arrays.toString(employees.toArray()));
	}
	
	@Test
	public void testLeftJoin() {
		String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employee";
		Query<Department> query = session.createQuery(hql);
		
		List<Department> departments = query.getResultList();
		System.out.println(departments.size());
		
		for(Department department:departments) {
			System.out.println(department.getName()+", "+department.getEmpolyees().size());
		}
	}
	
	@Test
	public void testLeftJoinFetch2() {
		String hql = "SELECT e FROM Employee d INNER JOIN FETCH e.department";
		Query<Employee> query = session.createQuery(hql);
		
		List<Employee> employees = query.getResultList();
		System.out.println(employees.size());
		
		for(Employee employee:employees) {
			System.out.println(employee.getName()+"-"+employee.getDepartment().getName());
		}
	}
	
	@Test
	public void testLeftJoinFetch() {
		//String hql = "SELECT DISTINCT d FROM Department d LEFT JOIN FETCH d.employee";
		String hql = "FROM Department d LEFT JOIN FETCH d.employee";
		Query<Department> query = session.createQuery(hql);
		
		List<Department> departments = query.getResultList();
		System.out.println(departments.size());
		
		departments = new ArrayList<>(new LinkedHashSet<>(departments));
		
		for(Department department:departments) {
			System.out.println(department.getName()+"-"+department.getEmpolyees().size());
		}
	}
	
	//聚合函数 基本不用
	@Test
	public void testGroupBy() {
		String hql = "SELECT min(e.salary), max(e.salary) "
				+ "FROM Employee e "
				+ "GROUP BY e.department "
				+ "HAVING min(salary)>minSal";
		
		Query<Object[]> query = session.createQuery(hql).setParameter("minSal", 8000);
		
		List<Object[]> result = query.getResultList();
		
		for(Object[] emps:result) { 
			System.out.println(Arrays.asList(emps));
		}
	}
	
	//投影查询
	@Test
	public void testFieldQuery2() {
		String hql = "SELECT new Employee(e.email,e.salary,e.department) "
				+ "FROM Employee e "
				+ "WHERE e.department = :department";
		Query<Employee> query = session.createQuery(hql);
		
		Department department = new Department();
		department.setId(80);
		
		List<Employee> result = query.setParameter("department", department).getResultList();
		
		for(Employee emps :result) {
			System.out.println(emps.getId()+", "+emps.getEmail()
				+", "+emps.getSalary()+", "+emps.getDepartment());
		}
	}
	
	//对象查询
	@Test
	public void testFieldQuery() {
		String hql = "SELECT e.email, e.salary, e.deparment FROM Employee e.department = :department";
		Query<Object[]> query = session.createQuery(hql);
		
		Department department = new Department();
		department.setId(80);
		
		List<Object[]> result = query.setParameter("department", department).getResultList();
		
		for(Object[] emps :result) {
			System.out.println(Arrays.asList(emps));
		}
	}
	
	@Test
	public void testNamedQuery() {
		Query<Employee> query = session.getNamedQuery("salaryEmps");
		
		List<Employee> list = query.setParameter("minSal", 5000)
								.setParameter("maxSal", 10000).getResultList();
		System.out.println(list.size());
	}
	
	@Test
	public void testPageQuery() {
		String hql = "From Employee";
		Query<Employee> query = session.createQuery(hql);
		
		int pageNo = 3;//表示第几页
		int pageSize = 5;//每页的数量
		
		List<Employee> employees = query.setFirstResult((pageNo-1)*(pageSize))
										.setMaxResults(pageSize)
										.getResultList();
		System.out.println(employees);
	}
	
	@Test
	public void testHQLNameParameter() {
		
		//1.创建 Query 对象
		//基于命名参数
		String hql = "FROM Employee e WHERE e.salary > :sal AND e.email LIKE :email";
	    Query<Employee> query = session.createQuery(hql);
		
		//2.绑定参数
		query.setParameter("sal", 6000)
			.setParameter("email", "%%");
		//3.执行查询
		List<Employee> employees = query.getResultList();
		System.out.println(employees);
		
	}
	
	@Test
	public void testHQL() {
		
		//1.创建 Query 对象
		//基于位置的参数
		String hql = "FROM Employee e WHERE e.salary > ? AND e.email LIKE ? AND e.department = ? ORDER BY e.salary";
	    Query<Employee> query = session.createQuery(hql);
		
		//2.绑定参数
	    //HQL 调用 setXXX 支持方法链的风格 
	    Department department = new Department();
	    department.setId(80);
		query.setParameter(0, 6000)
			.setParameter(1, "%%")
			.setParameter(2, department);
	    
		//3.执行查询
		List<Employee> employees = query.getResultList();
		System.out.println(employees);
		
	}
	
}
