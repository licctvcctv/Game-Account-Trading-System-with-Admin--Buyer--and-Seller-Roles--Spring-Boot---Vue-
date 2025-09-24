package com.xiaobaitiao.springbootinit.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaobaitiao.springbootinit.annotation.AuthCheck;
import com.xiaobaitiao.springbootinit.common.BaseResponse;
import com.xiaobaitiao.springbootinit.common.DeleteRequest;
import com.xiaobaitiao.springbootinit.common.ErrorCode;
import com.xiaobaitiao.springbootinit.common.JwtKit;
import com.xiaobaitiao.springbootinit.common.ResultUtils;
import com.xiaobaitiao.springbootinit.config.WxOpenConfig;
import com.xiaobaitiao.springbootinit.constant.UserConstant;
import com.xiaobaitiao.springbootinit.exception.BusinessException;
import com.xiaobaitiao.springbootinit.exception.ThrowUtils;
import com.xiaobaitiao.springbootinit.model.dto.user.*;
import com.xiaobaitiao.springbootinit.model.entity.User;
import com.xiaobaitiao.springbootinit.model.vo.LoginUserVO;
import com.xiaobaitiao.springbootinit.model.vo.UserVO;
import com.xiaobaitiao.springbootinit.service.UserService;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import me.chanjar.weixin.common.bean.oauth2.WxOAuth2AccessToken;
import me.chanjar.weixin.mp.api.WxMpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import static com.xiaobaitiao.springbootinit.service.impl.UserServiceImpl.SALT;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^1\\d{10}$");
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^(\\d{15}|\\d{17}[\\dXx])$");

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;
    @Autowired
    private JwtKit jwtKit;
//    @Resource
//    private CaptchaService captchaService;

    // region 登录相关

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//        CaptchaVO captchaVO = new CaptchaVO();
//        captchaVO.setCaptchaVerification(userRegisterRequest.getCaptchaVerification());
//        ResponseModel response = captchaService.verification(captchaVO);
//        if(!response.isSuccess()) {
//            //验证码校验失败，返回信息告诉前端
//            //repCode  0000  无异常，代表成功
//            //repCode  9999  服务器内部异常
//            //repCode  0011  参数不能为空
//            //repCode  6110  验证码已失效，请重新获取
//            //repCode  6111  验证失败
//            //repCode  6112  获取验证码失败,请联系管理员
//            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "验证码错误请重试");
//        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String userPhone = userRegisterRequest.getUserPhone();
        String realName = userRegisterRequest.getRealName();
        String idCardNumber = userRegisterRequest.getIdCardNumber();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, userPhone, realName, idCardNumber)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, userPhone, realName, idCardNumber);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO user = userService.userLogin(userAccount, userPassword, request);
        LoginUserVO userVO = new LoginUserVO();
        BeanUtils.copyProperties(user, userVO);
        // 生成token
        String token = jwtKit.generateToken(user);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("token",token);
        return ResultUtils.successDynamic(userVO,hashMap);


    }
    /**
     * 用户登录（微信开放平台）
     */
    @GetMapping("/login/wx_open")
    public BaseResponse<LoginUserVO> userLoginByWxOpen(HttpServletRequest request, HttpServletResponse response,
            @RequestParam("code") String code) {
        WxOAuth2AccessToken accessToken;
        try {
            WxMpService wxService = wxOpenConfig.getWxMpService();
            accessToken = wxService.getOAuth2Service().getAccessToken(code);
            WxOAuth2UserInfo userInfo = wxService.getOAuth2Service().getUserInfo(accessToken, code);
            String unionId = userInfo.getUnionId();
            String mpOpenId = userInfo.getOpenid();
            if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
            }
            return ResultUtils.success(userService.userLoginByMpOpen(userInfo, request));
        } catch (Exception e) {
            log.error("userLoginByWxOpen error", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "登录失败，系统错误");
        }
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(user));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        // 默认密码 12345678
        String defaultPassword = "12345678";
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + defaultPassword).getBytes());
        user.setUserPassword(encryptPassword);
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
            HttpServletRequest request) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        if (StringUtils.isNotBlank(user.getUserPhone()) && !PHONE_PATTERN.matcher(user.getUserPhone()).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }
        if (StringUtils.isNotBlank(user.getRealName())) {
            if (user.getRealName().length() < 2 || user.getRealName().length() > 30) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "姓名长度应在2-30个字符内");
            }
        }
        if (StringUtils.isNotBlank(user.getIdCardNumber())) {
            if (!ID_CARD_PATTERN.matcher(user.getIdCardNumber()).matches()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "身份证号码格式错误");
            }
            user.setIdCardNumber(user.getIdCardNumber().toUpperCase());
        }
        if (StringUtils.isNotBlank(user.getUserPhone())) {
            QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("userPhone", user.getUserPhone()).ne("id", user.getId());
            if (userService.count(phoneWrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号已被使用");
            }
        }
        if (StringUtils.isNotBlank(user.getIdCardNumber())) {
            QueryWrapper<User> idWrapper = new QueryWrapper<>();
            idWrapper.eq("idCardNumber", user.getIdCardNumber()).ne("id", user.getId());
            if (userService.count(idWrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "身份证号已被使用");
            }
        }
        if (userUpdateRequest.getSellPermission() != null) {
            if (userUpdateRequest.getSellPermission() == 1) {
                user.setSellApplyStatus(2);
            } else if (userUpdateRequest.getSellApplyStatus() == null) {
                user.setSellApplyStatus(0);
            }
        }
        if (userUpdateRequest.getRentPermission() != null) {
            if (userUpdateRequest.getRentPermission() == 1) {
                user.setRentApplyStatus(2);
            } else if (userUpdateRequest.getRentApplyStatus() == null) {
                user.setRentApplyStatus(0);
            }
        }
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
        BaseResponse<User> response = getUserById(id, request);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        return ResultUtils.success(userPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
            HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long size = userQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<User> userPage = userService.page(new Page<>(current, size),
                userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
        List<UserVO> userVO = userService.getUserVO(userPage.getRecords());
        userVOPage.setRecords(userVO);
        return ResultUtils.success(userVOPage);
    }

    // endregion

    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest
     * @param request
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest,
            HttpServletRequest request) {
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        String phone = userUpdateMyRequest.getUserPhone();
        String realName = userUpdateMyRequest.getRealName();
        String idCardNumber = userUpdateMyRequest.getIdCardNumber();
        if (StringUtils.isNotBlank(phone) && !PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号格式错误");
        }
        if (StringUtils.isNotBlank(realName)) {
            if (realName.length() < 2 || realName.length() > 30) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "姓名长度应在2-30个字符内");
            }
        }
        if (StringUtils.isNotBlank(idCardNumber) && !ID_CARD_PATTERN.matcher(idCardNumber).matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "身份证号码格式错误");
        }
        if (StringUtils.isNotBlank(phone) && !phone.equals(loginUser.getUserPhone())) {
            QueryWrapper<User> phoneWrapper = new QueryWrapper<>();
            phoneWrapper.eq("userPhone", phone).ne("id", loginUser.getId());
            if (userService.count(phoneWrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "手机号已被使用");
            }
        }
        if (StringUtils.isNotBlank(idCardNumber) && !idCardNumber.equalsIgnoreCase(StringUtils.defaultString(loginUser.getIdCardNumber()))) {
            QueryWrapper<User> idWrapper = new QueryWrapper<>();
            idWrapper.eq("idCardNumber", idCardNumber.toUpperCase()).ne("id", loginUser.getId());
            if (userService.count(idWrapper) > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "身份证号已被使用");
            }
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        if (StringUtils.isNotBlank(idCardNumber)) {
            user.setIdCardNumber(idCardNumber.toUpperCase());
        }
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        if (StringUtils.isNotBlank(user.getUserName())) {
            loginUser.setUserName(user.getUserName());
        }
        if (StringUtils.isNotBlank(user.getUserAvatar())) {
            loginUser.setUserAvatar(user.getUserAvatar());
        }
        if (StringUtils.isNotBlank(phone)) {
            loginUser.setUserPhone(phone);
        }
        if (StringUtils.isNotBlank(realName)) {
            loginUser.setRealName(realName);
        }
        if (StringUtils.isNotBlank(idCardNumber)) {
            loginUser.setIdCardNumber(idCardNumber.toUpperCase());
        }
        if (StringUtils.isNotBlank(user.getUserProfile())) {
            loginUser.setUserProfile(user.getUserProfile());
        }
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 更新个人余额
     */
    @PostMapping("/balance/update")
    public BaseResponse<Boolean> updateMyBalance(@RequestBody UserBalanceUpdateRequest balanceUpdateRequest,
            HttpServletRequest request) {
        if (balanceUpdateRequest == null || balanceUpdateRequest.getBalance() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        BigDecimal newBalance = balanceUpdateRequest.getBalance();
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "余额不能为负数");
        }
        User loginUser = userService.getLoginUser(request);
        User updateUser = new User();
        updateUser.setId(loginUser.getId());
        updateUser.setBalance(newBalance);
        boolean result = userService.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        loginUser.setBalance(newBalance);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        return ResultUtils.success(true);
    }

    /**
     * 申请出售/出租权限
     */
    @PostMapping("/seller/apply")
    public BaseResponse<Boolean> applySeller(@RequestBody SellerApplyRequest sellerApplyRequest,
            HttpServletRequest request) {
        if (sellerApplyRequest == null || StringUtils.isBlank(sellerApplyRequest.getApplyType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        String type = sellerApplyRequest.getApplyType().toUpperCase();
        User updateUser = new User();
        updateUser.setId(loginUser.getId());
        switch (type) {
            case "SELL":
                if (loginUser.getSellPermission() != null && loginUser.getSellPermission() == 1) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "已拥有出售权限");
                }
                if (loginUser.getSellApplyStatus() != null && loginUser.getSellApplyStatus() == 1) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "出售权限审核中");
                }
                updateUser.setSellApplyStatus(1);
                break;
            case "RENT":
                if (loginUser.getRentPermission() != null && loginUser.getRentPermission() == 1) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "已拥有出租权限");
                }
                if (loginUser.getRentApplyStatus() != null && loginUser.getRentApplyStatus() == 1) {
                    throw new BusinessException(ErrorCode.OPERATION_ERROR, "出租权限审核中");
                }
                updateUser.setRentApplyStatus(1);
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "申请类型错误");
        }
        boolean result = userService.updateById(updateUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        if ("SELL".equals(type)) {
            loginUser.setSellApplyStatus(1);
        } else {
            loginUser.setRentApplyStatus(1);
        }
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        return ResultUtils.success(true);
    }
}
