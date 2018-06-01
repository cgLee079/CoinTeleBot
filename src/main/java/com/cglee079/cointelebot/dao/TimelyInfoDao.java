package com.cglee079.cointelebot.dao;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.cglee079.cointelebot.log.Log;
import com.cglee079.cointelebot.model.TimelyInfoVo;

@Repository
public class TimelyInfoDao {
	final static String namespace = "com.cglee079.cointelebot.mapper.TimelyInfoMapper";

	@Autowired
	private SqlSessionTemplate sqlSession;
	
	public boolean insert(TimelyInfoVo timelyInfo){
		try { return sqlSession.insert(namespace + ".insert", timelyInfo) == 1; }
		catch (Exception e){
			Log.i("ERROR\t:\t" + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public TimelyInfoVo get(String coinId, String date, String market) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("coinId", coinId);
		map.put("date", date);
		map.put("market", market);
		return sqlSession.selectOne(namespace + ".get", map);
	}

	public List<TimelyInfoVo> list(String coinId, String market, int cnt) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("coinId", coinId);
		map.put("market", market);
		map.put("cnt", cnt);
		return sqlSession.selectList(namespace + ".list", map);
	}

}
