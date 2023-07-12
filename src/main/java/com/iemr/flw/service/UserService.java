package com.iemr.flw.service;

import com.iemr.flw.dto.UserServiceRoleDTO;

public interface UserService {

    UserServiceRoleDTO getUserRole(Integer userId, Integer roleId);
}
