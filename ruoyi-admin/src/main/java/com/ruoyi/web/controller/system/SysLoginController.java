package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Set;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysUserRegistered;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysMenu;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginBody;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.web.service.SysLoginService;
import com.ruoyi.framework.web.service.SysPermissionService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysMenuService;

/**
 * 登录验证
 * 
 * @author ruoyi
 */
@RestController
public class SysLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysUserService userService;

    /**
     * 登录方法
     * 
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.login(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        SysUser user = userService.selectUserByUserName(loginBody.getUsername());
        //更新登录时间和登录次数
        userService.updateLogin(user);
        //因为登录逻辑过了之后，所以无需判空操作,主要是返回标识标定是系统用户还是前台用户
        String flag = "bigdata-push".equals(user.getRemark())? "user":"system";
        ajax.put("flag",flag);
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /*
    注册方法
    */
    @PostMapping("/registered")
    public AjaxResult registered(@RequestBody SysUserRegistered sysUserRegistered){
        SysUser sysUser = new SysUser();
        sysUser.setUserName(sysUserRegistered.getName());
        sysUser.setPhonenumber(sysUserRegistered.getPhone());
        sysUser.setEmail(sysUserRegistered.getEmail());
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(sysUserRegistered.getName())))
        {
            return AjaxResult.error("新增用户'" + sysUserRegistered.getName() + "'失败，登录账号已存在");
        }
        else if (StringUtils.isNotEmpty(sysUser.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(sysUser)))
        {
            return AjaxResult.error("新增用户'" + sysUserRegistered.getName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(sysUser.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(sysUser)))
        {
            return AjaxResult.error("新增用户'" + sysUserRegistered.getName() + "'失败，邮箱账号已存在");
        }
        //加密密码
        sysUserRegistered.setPassword(SecurityUtils.encryptPassword(sysUserRegistered.getPassword()));
        int result = userService.registeredUser(sysUserRegistered,1);
        return result>0 ? AjaxResult.success("注册成功"):AjaxResult.error("注册失败");
    }

    /**
     * 获取用户信息
     * 
     * @return 用户信息
     */
    @GetMapping("getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        LoginUser loginUser = tokenService.getLoginUser(ServletUtils.getRequest());
        // 用户信息
        SysUser user = loginUser.getUser();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(user.getUserId());
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
