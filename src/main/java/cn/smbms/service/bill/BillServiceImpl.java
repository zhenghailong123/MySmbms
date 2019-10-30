package cn.smbms.service.bill;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.bill.BillDao;
/*import cn.smbms.dao.bill.BillDaoImpl;*/
import cn.smbms.pojo.Bill;
import cn.smbms.pojo.Provider;
import cn.smbms.tools.MybatisUtils;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("billService")
public class BillServiceImpl implements BillService {
	//private SqlSession session;
	@Autowired
	private BillDao billDao;

	@Override
	public boolean add(Bill bill) {
		// TODO Auto-generated method stub
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int addRows = session.getMapper(BillDao.class).add(bill);
			if(billDao.add(bill) > 0){
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
	public List<Bill> getBillList(Bill bill) {
		// TODO Auto-generated method stub
		List<Bill> billList = null;
		System.out.println("query productName ---- > " + bill.getProductName());
		System.out.println("query providerId ---- > " + bill.getProviderId());
		System.out.println("query isPayment ---- > " + bill.getIsPayment());
		try {
			//session = MybatisUtils.getSession();
			//billList = session.getMapper(BillDao.class).getBillList(bill);
			billList = billDao.getBillList(bill);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return billList;
	}

	@Override
	public boolean deleteBillById(String delId) {
		// TODO Auto-generated method stub
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int delRows = session.getMapper(BillDao.class).deleteBillById(delId);
			if(billDao.deleteBillById(delId) > 0) {
                flag = true;
            }
			//session.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//session.rollback();
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return flag;
	}

	@Override
	public Bill getBillById(String id) {
		// TODO Auto-generated method stub
		Bill bill = null;
		try{
			//session = MybatisUtils.getSession();
			//bill = session.getMapper(BillDao.class).getBillById(id);
			bill = billDao.getBillById(id);
		}catch (Exception e) {
			e.printStackTrace();
			bill = null;
		}finally{
			//MybatisUtils.closeSession(session);
		}
		return bill;
	}

	@Override
	public boolean modify(Bill bill) {
		// TODO Auto-generated method stub
		boolean flag = false;
		try {
			//session = MybatisUtils.getSession();
			//int upRows = session.getMapper(BillDao.class).modify(bill);
			if( billDao.modify(bill)> 0) {
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
