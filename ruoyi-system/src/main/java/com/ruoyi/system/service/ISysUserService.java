package com.ruoyi.system.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.SysUserPush;
import com.ruoyi.system.domain.SysUserRegistered;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 业务层
 * 
 * @author ruoyi
 */
public interface ISysUserService
{
    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    public List<SysUser> selectUserList(SysUser user);

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    public SysUser selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public SysUser selectUserById(Long userId);

    /**
     * 根据用户ID查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    public String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     * 
     * @param userName 用户名称
     * @return 结果
     */
    public String checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkPhoneUnique(SysUser user);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    public String checkEmailUnique(SysUser user);

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    public void checkUserAllowed(SysUser user);

    /**
     * 新增用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int insertUser(SysUser user);

    /**
     * 修改用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUser(SysUser user);

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserStatus(SysUser user);

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int updateUserProfile(SysUser user);

    /**
     * 修改用户头像
     * 
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    public boolean updateUserAvatar(String userName, String avatar);

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    public int resetPwd(SysUser user);

    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(String userName, String password);

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteUserById(Long userId);

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    public int deleteUserByIds(Long[] userIds);

    /**
     * 导入用户数据
     * 
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName);

    /*
    * 新增注册方法,0代表系统用户，1代表前台用户
    * */
    public int registeredUser(SysUserRegistered sysUserRegistered,int flag);

    /*查询所有用户*/
    public List<SysUserRegistered> selectAllRegisteredUser();

    /*查询某个用户*/
    public SysUserRegistered selectRegisteredUser(@Param("userName") String userName);

    /*忘记密码操作*/
    public AjaxResult forgetPwd(SysUser user);

    /*修改用户信息*/
    int editUser(SysUser user);

    /*修改密码*/
    AjaxResult editPwd(Long userId,String password,String oldPassword);
    /*修改用户登录时间和登录次数*/
    void updateLogin(SysUser sysUser);
    /*
    * 获取用户的推送信息
    * */
    List<SysUserPush> getPushInfo();
    /*获取用户关注的所有股票代码*/
    List<String> getUserStocks();
}
