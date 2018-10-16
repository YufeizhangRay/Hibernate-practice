package cn.zyf.hibernate.helloworld;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.junit.jupiter.api.Test;

class HibernateTest {

	@Test
	void test() {
		
		//1.创建一个 SessiionFactory 对象: 通过他建立一个与数据库连接的会话 Session
		SessionFactory sessionFactory = null;
		//还需要一个配置类，封装了有我们配置文件的配置信息
		//返回回来的 configuration 包含配置文件的具体信息
		Configuration configuration = new Configuration().configure();
		//所有配置或服务必须注册到一个服务注册类才能生效
		ServiceRegistry serviceRegistry =  configuration.getStandardServiceRegistryBuilder().build();
		
		sessionFactory = new MetadataSources(serviceRegistry).buildMetadata().buildSessionFactory();
		
		// 2.创建一个 Session 对象
		Session session = sessionFactory.openSession();
		
		// 3.开启事务
		Transaction transaction = session.beginTransaction();
	
		
		
		// 4.执行数据库操作 
		//面向对象的方式来操作数据库 没有 SQL 语句
//		News news = new News("Java", "zyf", new Date());
		//对象为临时状态 
//		session.save(news);
		//save 方法发起一条 insert 语句，事务提交之后才真正作用到数据库中
		//对象为持久化状态：同时存在于 Session 的缓存中与数据库中
		//hibernate 不允许修改持久状态对象的 ID 值
		
		//get/load 方法:通过 id，获取数据库对应的数据，转化成对象(持久态)，放到 Session 缓存中
		
		//get 方法会立即加载对象，发起 SQL. 
		//返回值就是类对象本身
		//若无对象则返回 null
		
		//load 方法不会立即加载对象，对象被使用才会加载对象，发起 SQL，即延迟加载(懒加载). 
		//返回的是对象的代理(加载对象的子类)
		//若无对象抛出懒加载异常
		
		//flush 方法:检查 Session 缓存和数据库记录是否一致，若不一致则发出一条 UPDATE 语句更新数据库数据
		//可以省略 flush 方法，commit 方法真正提交事务之前，会自动调用该方法
		//refresh 方法不进行判断，而是直接发出一条 SQL 语句， 若发现数据不一致，会修改 Session 缓存的数据
		//clear 方法会清空 Session 缓存
		News news2 = session.get(News.class, 1);
		System.out.println(news2);
		
		// 5.提交事务
		//事务提交只针对持久对象，若不一致则会发出 UPDATE 语句更新数据库数据
		//若提交时 Session 缓存被清空，则不作为
		transaction.commit();
		
		// 6.关闭 Session
		session.close();
		//对象为游离状态
		//数据库中存在，Session 缓存不存在也是游离状态
		//对游离对象设置 ID 无效，数据库会自行生成
		//临时和游离状态的对象可以被垃圾回收
		
		//update 方法会先将对象放入 Session 缓存中(变为持久对象)，再更新数据库对应的数据
		//更新一个持久化对象不需要显示调用 update，因为 commit 方法中已经进行了 flush，会自动调用 update
		//若关闭了 Session 又打开了一个新的 Session，前一个 Session 的对象对于新的 Session 就是游离的，则必须显示的调用 update 以持久化对象
		//用 update 方法更新游离对象，无论java对象和数据库对应记录是否一样，都会发送 update 语句,
		//可以用在 .hbm.xml 文件中的 class标签中用 select-before-update="true" 避免误发 但是通常不使用
		//若数据库中没有没有对应的记录，则抛出异常
		
		//delete 方法会先删除 Session 缓存中的记录，再删除数据库中的对应记录，对象变为临时态
		//删除对象
		//删除持久对象 
		//删除数据库里对应的记录
		//删除对象数据库中没有，抛出异常
		//默认的删除会把缓存和数据库记录都删除，但是会保留对象的 ID,妨碍后面重复利用这个对象
		//可以在 cfg.xml 文件中进行配置 <property name="hibernate.use_identifier_rollback">ture</property>，
		//在对象被删除之后 ID 设置为 null
		//
		
		//saveOrUpdate 方法，对象为临时态则调用 save，游离态则调用 update
		
		//Session 缓存不允许存在重复对象
		
		// 7.关闭 SessionFactroy 对象
		sessionFactory.close();
		
	}

}
