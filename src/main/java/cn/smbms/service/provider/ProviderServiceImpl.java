package cn.smbms.service.provider;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.bill.BillDao;
/*import cn.smbms.dao.bill.BillDaoImpl;*/
import cn.smbms.dao.provider.ProviderDao;
/*import cn.smbms.dao.provider.ProviderDaoImpl;*/
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.tools.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("providerService")
public class ProviderServiceImpl implements ProviderService {
	@Autowired
	private ProviderDao providerDao;
	@Autowired
	private BillDao  billDao;
	@Override
	public boolean add(Provider provider) {
		// TODO Auto-generated method stub
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int addRows = session.getMapper(ProviderDao.class).add(provider);
			if(providerDao.add(provider) > 0){
				flag = true;
			}
			//session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			//session.rollback();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}

	@Override
	public List<Provider> getProviderList(String proName,String proCode) { ;
		List<Provider> providerList = null;
		System.out.println("query proName ---- > " + proName);
		System.out.println("query proCode ---- > " + proCode);
		try {
			//session = MybatisUtils.getSession();
			//providerList = session.getMapper(ProviderDao.class).getProviderList(proName,proCode);
			providerList = providerDao.getProviderList(proName,proCode);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return providerList;
	}

	/**
	 * 业务：根据ID删除供应商表的数据之前，需要先去订单表里进行查询操作
	 * 若订单表中无该供应商的订单数据，则可以删除
	 * 若有该供应商的订单数据，则不可以删除
	 * 返回值billCount
	 * 1> billCount == 0  删除---1 成功 （0） 2 不成功 （-1）
	 * 2> billCount > 0    不能删除 查询成功（0）查询不成功（-1）
	 * 
	 * ---判断
	 * 如果billCount = -1 失败
	 * 若billCount >= 0 成功
	 */
	@Override
	public int deleteProviderById(String delId) {
		// TODO Auto-generated method stub
		int billCount = -1;
		try {
			//session = MybatisUtils.getSession();
			//billCount = session.getMapper(BillDao.class).getBillCountByProviderId(delId);
			billCount = billDao.getBillCountByProviderId(delId);
			if(billCount == 0){
				providerDao.deleteProviderById(delId);
			}
			//session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			billCount = -1;
			//session.rollback();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return billCount;
	}

	@Override
	public Provider getProviderById(String id) {
		Provider provider = null;
		try{
			//session = MybatisUtils.getSession();
			//provider = session.getMapper(ProviderDao.class).getProviderById(id);
			provider = providerDao.getProviderById(id);
		}catch (Exception e) {
			e.printStackTrace();
			provider = null;
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return provider;
	}

	@Override
	public boolean modify(Provider provider) {
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int upRows = session.getMapper(ProviderDao.class).modify(provider);
			if(providerDao.modify(provider) > 0){
				flag = true;
			}
			//session.commit();
		} catch (Exception e) {
			//session.rollback();
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}

}
