package com.ruoyi.system.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysPostMapper;
import com.ruoyi.system.mapper.SysRoleMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.SysUserPostMapper;
import com.ruoyi.system.mapper.SysUserRoleMapper;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 用户 业务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysUserServiceImpl implements ISysUserService
{
    private static final Logger log = LoggerFactory.getLogger(SysUserServiceImpl.class);

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysPostMapper postMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysUserPostMapper userPostMapper;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 根据条件分页查询用户列表
     * 
     * @param user 用户信息
     * @return 用户信息集合信息
     */
    @Override
    @DataScope(deptAlias = "d", userAlias = "u")
    public List<SysUser> selectUserList(SysUser user)
    {
        return userMapper.selectUserList(user);
    }

    /**
     * 通过用户名查询用户
     * 
     * @param userName 用户名
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserByUserName(String userName)
    {
        return userMapper.selectUserByUserName(userName);
    }

    /**
     * 通过用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户对象信息
     */
    @Override
    public SysUser selectUserById(Long userId)
    {
        return userMapper.selectUserById(userId);
    }

    /**
     * 查询用户所属角色组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserRoleGroup(String userName)
    {
        List<SysRole> list = roleMapper.selectRolesByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysRole role : list)
        {
            idsStr.append(role.getRoleName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString()))
        {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 查询用户所属岗位组
     * 
     * @param userName 用户名
     * @return 结果
     */
    @Override
    public String selectUserPostGroup(String userName)
    {
        List<SysPost> list = postMapper.selectPostsByUserName(userName);
        StringBuffer idsStr = new StringBuffer();
        for (SysPost post : list)
        {
            idsStr.append(post.getPostName()).append(",");
        }
        if (StringUtils.isNotEmpty(idsStr.toString()))
        {
            return idsStr.substring(0, idsStr.length() - 1);
        }
        return idsStr.toString();
    }

    /**
     * 校验用户名称是否唯一
     * 
     * @param userName 用户名称
     * @return 结果
     */
    @Override
    public String checkUserNameUnique(String userName)
    {
        int count = userMapper.checkUserNameUnique(userName);
        if (count > 0)
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户名称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkPhoneUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return
     */
    @Override
    public String checkEmailUnique(SysUser user)
    {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        SysUser info = userMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验用户是否允许操作
     * 
     * @param user 用户信息
     */
    @Override
    public void checkUserAllowed(SysUser user)
    {
        if (StringUtils.isNotNull(user.getUserId()) && user.isAdmin())
        {
            throw new CustomException("不允许操作超级管理员用户");
        }
    }

    /**
     * 新增保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertUser(SysUser user)
    {
        // 新增用户信息
        int rows = userMapper.insertUser(user);
        // 新增用户岗位关联
        insertUserPost(user);
        // 新增用户与角色管理
        insertUserRole(user);
        return rows;
    }

    /**
     * 修改保存用户信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateUser(SysUser user)
    {
        Long userId = user.getUserId();
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 新增用户与角色管理
        insertUserRole(user);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPostByUserId(userId);
        // 新增用户与岗位管理
        insertUserPost(user);
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户状态
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserStatus(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户基本信息
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int updateUserProfile(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 修改用户头像
     * 
     * @param userName 用户名
     * @param avatar 头像地址
     * @return 结果
     */
    @Override
    public boolean updateUserAvatar(String userName, String avatar)
    {
        return userMapper.updateUserAvatar(userName, avatar) > 0;
    }

    /**
     * 重置用户密码
     * 
     * @param user 用户信息
     * @return 结果
     */
    @Override
    public int resetPwd(SysUser user)
    {
        return userMapper.updateUser(user);
    }

    /**
     * 重置用户密码
     * 
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    @Override
    public int resetUserPwd(String userName, String password)
    {
        return userMapper.resetUserPwd(userName, password);
    }

    /**
     * 新增用户角色信息
     * 
     * @param user 用户对象
     */
    public void insertUserRole(SysUser user)
    {
        Long[] roles = user.getRoleIds();
        if (StringUtils.isNotNull(roles))
        {
            // 新增用户与角色管理
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            for (Long roleId : roles)
            {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(user.getUserId());
                ur.setRoleId(roleId);
                list.add(ur);
            }
            if (list.size() > 0)
            {
                userRoleMapper.batchUserRole(list);
            }
        }
    }

    /**
     * 新增用户岗位信息
     * 
     * @param user 用户对象
     */
    public void insertUserPost(SysUser user)
    {
        Long[] posts = user.getPostIds();
        if (StringUtils.isNotNull(posts))
        {
            // 新增用户与岗位管理
            List<SysUserPost> list = new ArrayList<SysUserPost>();
            for (Long postId : posts)
            {
                SysUserPost up = new SysUserPost();
                up.setUserId(user.getUserId());
                up.setPostId(postId);
                list.add(up);
            }
            if (list.size() > 0)
            {
                userPostMapper.batchUserPost(list);
            }
        }
    }

    /**
     * 通过用户ID删除用户
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserById(Long userId)
    {
        // 删除用户与角色关联
        userRoleMapper.deleteUserRoleByUserId(userId);
        // 删除用户与岗位表
        userPostMapper.deleteUserPostByUserId(userId);
        userMapper.deleteRegisteredUser(userId);
        return userMapper.deleteUserById(userId);
    }

    /**
     * 批量删除用户信息
     * 
     * @param userIds 需要删除的用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteUserByIds(Long[] userIds)
    {
        for (Long userId : userIds)
        {
            checkUserAllowed(new SysUser(userId));
        }
        // 删除用户与角色关联
        userRoleMapper.deleteUserRole(userIds);
        // 删除用户与岗位关联
        userPostMapper.deleteUserPost(userIds);
        return userMapper.deleteUserByIds(userIds);
    }

    /**
     * 导入用户数据
     * 
     * @param userList 用户数据列表
     * @param isUpdateSupport 是否更新支持，如果已存在，则进行更新数据
     * @param operName 操作用户
     * @return 结果
     */
    @Override
    public String importUser(List<SysUser> userList, Boolean isUpdateSupport, String operName)
    {
        if (StringUtils.isNull(userList) || userList.size() == 0)
        {
            throw new CustomException("导入用户数据不能为空！");
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder failureMsg = new StringBuilder();
        String password = configService.selectConfigByKey("sys.user.initPassword");
        for (SysUser user : userList)
        {
            try
            {
                // 验证是否存在这个用户
                SysUser u = userMapper.selectUserByUserName(user.getUserName());
                if (StringUtils.isNull(u))
                {
                    user.setPassword(SecurityUtils.encryptPassword(password));
                    user.setCreateBy(operName);
                    this.insertUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 导入成功");
                }
                else if (isUpdateSupport)
                {
                    user.setUpdateBy(operName);
                    this.updateUser(user);
                    successNum++;
                    successMsg.append("<br/>" + successNum + "、账号 " + user.getUserName() + " 更新成功");
                }
                else
                {
                    failureNum++;
                    failureMsg.append("<br/>" + failureNum + "、账号 " + user.getUserName() + " 已存在");
                }
            }
            catch (Exception e)
            {
                failureNum++;
                String msg = "<br/>" + failureNum + "、账号 " + user.getUserName() + " 导入失败：";
                failureMsg.append(msg + e.getMessage());
                log.error(msg, e);
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据格式不正确，错误如下：");
            throw new CustomException(failureMsg.toString());
        }
        else
        {
            successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
        }
        return successMsg.toString();
    }

    @Override
    public int registeredUser(SysUserRegistered sysUserRegistered,int flag) {
        SysUser sysUser = new SysUser();
        sysUser.setUserName(sysUserRegistered.getName());
        sysUser.setPassword(sysUserRegistered.getPassword());
        sysUser.setNickName(sysUserRegistered.getName());
        if(flag==1){
            sysUser.setRemark("bigdata-push");
        }
        sysUser.setPhonenumber(sysUserRegistered.getPhone());
        sysUser.setEmail(sysUserRegistered.getEmail());
        sysUser.setLoginDate(null);
        sysUser.setLoginCount(0);
        //拼接字符串
        StringJoiner joiner = new StringJoiner(",","","");
        for(Long message:sysUserRegistered.getStockMessage()){
            joiner.add(Long.toString(message));
        }
        sysUserRegistered.setStockMessages(joiner.toString());
        joiner = new StringJoiner(",","","");
        for(Long remind:sysUserRegistered.getStockRemind()){
            joiner.add(Long.toString(remind));
        }
        sysUserRegistered.setStockReminds(joiner.toString());
        int sysUserRows = userMapper.insertUser(sysUser);
        if(sysUserRows>0){
            sysUserRegistered.setUserId(selectUserByUserName(sysUser.getUserName()).getUserId());
        }
        int sysRegisterRows = userMapper.registeredUser(sysUserRegistered);
        return sysRegisterRows>0 && sysUserRows>0? 1:0;
    }

    @Override
    public List<SysUserRegistered> selectAllRegisteredUser() {
        return userMapper.selectAllRegisteredUser();
    }

    @Override
    public SysUserRegistered selectRegisteredUser(String userName) {
        return userMapper.selectRegisteredUser(userName);
    }

    @Override
    public AjaxResult forgetPwd(SysUser user) {
        SysUser sysUser = userMapper.checkPhoneUnique(user.getPhonenumber());
        if(redisCache.getCacheObject(user.getCode())==null){
            return AjaxResult.error("验证码输入错误");
        }
        if(sysUser!=null){
            sysUser.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
            sysUser.setUpdateBy(sysUser.getUserName());
            return resetPwd(sysUser)==1? AjaxResult.success("重置密码成功"):AjaxResult.error("重置密码失败");
        }else {
            return AjaxResult.error("不存在该手机号");
        }
    }

    @Override
    public int editUser(SysUser user) {
        int row = userMapper.editUser(user);
        Map<String,Object> map = user.getParams();
        //拼接字符串
        if(map!=null && map.size()>0){
            for(String key:map.keySet()){
                if("stockMessages".equals(key) || "stockReminds".equals(key)){
                    if(map.get(key)!=null){
                        StringJoiner joiner = new StringJoiner(",","","");
                        List<Integer> array = (List<Integer>) map.get(key);
                        for(Integer a:array){
                            joiner.add(Integer.toString(a));
                        }
                        map.put(key,joiner.toString());
                    }
                }
            }
            map.put("userId",user.getUserId());
            row += userMapper.updateRegisteredUser(user.getParams());
        }
        return row;
    }

    @Override
    public AjaxResult editPwd(Long userId,String password, String oldPassword) {
        SysUser user = userMapper.selectUserById(userId);
        System.out.println("数据库密码："+user.getPassword());
        if(SecurityUtils.matchesPassword(oldPassword,user.getPassword())){
            SysUser resetUserPwd = new SysUser();
            resetUserPwd.setPassword(SecurityUtils.encryptPassword(password));
            resetUserPwd.setUserId(userId);
            return userMapper.updateUser(resetUserPwd)==1?AjaxResult.success("修改密码成功!"):AjaxResult.error("修改密码失败!");
        }else{
            return AjaxResult.error("密码不一致!");
        }
    }

    @Override
    public void updateLogin(SysUser sysUser) {
        if(sysUser.getLoginCount()==null){
            System.out.println("登录次数:"+sysUser.getLoginCount());
            sysUser.setLoginCount(1);
        }
        if(sysUser.getLoginDate()!=null){
            AtomicInteger a = new AtomicInteger(sysUser.getLoginCount());
            if(!DateUtils.isSameDay(sysUser.getLoginDate(),DateUtils.getNowDate())){
                sysUser.setLoginCount(a.incrementAndGet());
            }
        }
        sysUser.setLoginDate(DateUtils.getNowDate());
        userMapper.updateUser(sysUser);
    }

    @Override
    public List<SysUserPush> getPushInfo() {
        List<Long> userIds = userMapper.selectAllUser();
        List<SysUserPush> sysUserPushList = new ArrayList<>();
        for(Long userId:userIds){
            SysUser sysUser = userMapper.selectUserById(userId);
            SysUserPush sysUserPush = new SysUserPush(sysUser);
            //计算活跃度,登录次数/（前面两个时间段相减)
            long days = 0;
            if(sysUser.getLoginDate()!=null){
                days = DateUtils.getDifferenceDays(sysUser.getLoginDate(),sysUser.getCreateTime())+1;
            }
            double activity = 0.0;
            if(sysUserPush.getLoginCount()!=null && sysUserPush.getLoginCount()!=0){
                activity = (double)sysUserPush.getLoginCount()/days;
            }
            sysUserPush.setUserActivity(activity);
            //距离最后登录天数
            long last = 0;
            if(sysUser.getLoginDate()!=null){
                last = DateUtils.getDifferenceDays(DateUtils.getNowDate(),sysUser.getLoginDate());
            }
            sysUserPush.setDays((int) last);
            //活跃度超过50%，距离最后登录超过14天，就视为需要召回的用户
            sysUserPush.setRecall(activity > 0.5 && days > 14);
            sysUserPushList.add(sysUserPush);
        }
        return sysUserPushList;
    }
}
