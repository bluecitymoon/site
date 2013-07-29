package com.pingjiujia.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.pingjiujia.admin.domain.ClientVersion;
import com.pingjiujia.dao.ClientVersionDao;

@Service("txjjVersionControllService")
public class VersionControllServiceImpl implements VersionControllService {
	
	@Resource(name = "txjjClientVersionDao")
	private ClientVersionDao clientVersionDao;

	@Override
	public ClientVersion findLastestVersion() {
		return clientVersionDao.findLastestVersion();
	}

}
