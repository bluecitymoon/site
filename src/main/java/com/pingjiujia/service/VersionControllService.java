package com.pingjiujia.service;

import com.pingjiujia.admin.domain.ClientVersion;

public interface VersionControllService {

	public ClientVersion findLastestVersion();

}
