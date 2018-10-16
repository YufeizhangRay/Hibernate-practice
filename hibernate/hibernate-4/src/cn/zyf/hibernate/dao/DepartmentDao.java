package cn.zyf.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import cn.zyf.hibernate.entities.Department;
import cn.zyf.hibernate.hibernate.HibernateUtils;

public class DepartmentDao {

	private SessionFactory sessionFactory;
	
	public void setSessionFaction(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public void save(Department department) {
		//内部获取 Session 对象
		//获取和当前对象绑定的 Session 对象
		//1.不需要从外部传入 Session 对象
		//2.多个 Dao 方法也可以使用同一个事物
		Session session = HibernateUtils.getInstance().getSession();
		session.save(department);
	}
	
	/**
	 * 若需要传入一个 Session 对象，则意味着上一层(Service)需要获得到 Session 对象
	 * 这说明上一层需要和 hibernate 的 API 紧密耦合。所以不推荐使用此方式
	 */
	public void save(Session session,Department department) {
		session.save(department);
	}
	
}
