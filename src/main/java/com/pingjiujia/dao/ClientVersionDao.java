package com.pingjiujia.dao;

import com.pingjiujia.admin.domain.ClientVersion;

public interface ClientVersionDao {

	ClientVersion findLastestVersion();
       
}
