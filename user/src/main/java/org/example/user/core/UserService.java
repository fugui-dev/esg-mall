package org.example.user.core;

import org.example.user.bean.SingleResponse;
import org.example.user.bean.cmd.UserQryCmd;
import org.example.user.bean.dto.UserDTO;

public interface UserService {

    SingleResponse<UserDTO> get(UserQryCmd userQryCmd);
}
