package com.ssm.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ssm.crud.bean.Employee;
import com.ssm.crud.bean.Msg;
import com.ssm.crud.service.EmployeeService;

@Controller
public class EmployeeController {

	@Autowired
	EmployeeService empService;
	
	/*@RequestMapping(value="/emps")
	public String getEmps(@RequestParam(value="pn",defaultValue="1")Integer pn,Model model) {
		PageHelper.startPage(pn, 5);
		List<Employee> empList = empService.getAllEmps();
		PageInfo page = new PageInfo(empList,5);
		model.addAttribute("pageInfo", page);
		return "list";
	}*/
	@RequestMapping(value="/emps")
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn",defaultValue="1")Integer pn) {
		PageHelper.startPage(pn, 5);
		List<Employee> empList = empService.getAllEmps();
		PageInfo page = new PageInfo(empList,5);
		return Msg.success().add("pageInfo", page);
	}
	
	//用户保存
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmployee(@Valid Employee employee,BindingResult result) {
		if(result.hasErrors()) {
			Map<String,Object> map = new HashMap<String, Object>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("错误字段："+fieldError.getField());
				System.out.println("错误信息："+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields",map);
		}else {
			empService.saveEmp(employee);
			return Msg.success();
		}
	}
	
	//校验用户名是否可用
	@ResponseBody
	@RequestMapping(value="/checkUser")
	public  Msg checkUser(@RequestParam("empName")String empName) {
		//先校验用户名是否合法
		String reg = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(reg)) {
			return Msg.fail().add("va_msg","用户名不合法！");
		}
		boolean b = empService.checkUser(empName);
		if(b) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg","用户名已存在！");
		}
	}
	
	//校验邮箱
	@ResponseBody
	@RequestMapping(value="/checkEmail")
	public Msg checkEmail(@RequestParam("email")String email) {
		String reg = "(^([a-zA-Z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$)";
		if(!email.matches(reg)) {
			return Msg.fail().add("va_msg","邮箱格式不正确！");
		}else {
			return Msg.success();
		}
		
	}
	
	//根据id查找用户进行修改
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		Employee employee = empService.getEmp(id);
		return Msg.success().add("emp", employee);
		
	}
	
	//用户修改保存
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Employee employee) {
		empService.updateEmp(employee);
		return Msg.success();
	}
	
	//用户删除
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg delEmp(@PathVariable("ids")String ids) {
		//批量删除
		if(ids.contains("-")) {
			String[] arrs = ids.split("-");
			List<Integer> list = new ArrayList<Integer>();
			for (String id : arrs) {
				list.add(Integer.parseInt(id));
			}
			empService.deleteBatch(list);
		}else {
			Integer id = Integer.parseInt(ids);
			empService.deleteEmp(id);
		}
		
		return Msg.success();
		
	}
}
