package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的明文密码md5加密处理
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    public void save(EmployeeDTO employeeDTO) {
        System.out.println("当前线程的id"+Thread.currentThread().getId());
        //调用持有层mapper把数据插入，dto转成实体类里实体
        Employee employee=new Employee();
        //设置属性值，对象属性拷贝简化代码量
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO,employee);//前提：属性名一致a
        //剩余不一致手动设置
        //设置账号的状态：默认正常，1表示正常，0表示锁定
        employee.setStatus(StatusConstant.ENABLE);//常量类，方便维护
        //设置密码，默认密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));//密码转MD5
        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //TODO后期改 为当前登录用户的Id,动态获取
        //设置当前记录创建人id
        employee.setCreateUser(BaseContext.getCurrentId());//从线程存储空间中取出
        //设置当前记录修改人id
        employee.setUpdateUser(BaseContext.getCurrentId());
        //调用持久层mapper把数据插入
        employeeMapper.insert(employee);



    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
      //select * from employee limit 0,10
      //开始分页查询pagehelper插件
      //基于mybaits的拦截器实现，对mysql进行动态拼接，计算
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee>page=employeeMapper.pageQuery(employeePageQueryDTO);
       long total= page.getTotal();
       List<Employee> records= page.getResult();
        return new PageResult(total,records);

    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    public void startOrStop(Integer status, Long id) {
       //update employee set status=? where id =?
//      Employee employee= new Employee();//方法一，直接创建实体类对象
//      employee.setStatus(status);
//      employee.setId(id);
//方法二，用builder
Employee employee= Employee.builder()
                .status(status).id(id).build();


       employeeMapper.update(employee);
    }

}
