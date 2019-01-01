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
	
	//�û�����
	@RequestMapping(value="/emp",method=RequestMethod.POST)
	@ResponseBody
	public Msg saveEmployee(@Valid Employee employee,BindingResult result) {
		if(result.hasErrors()) {
			Map<String,Object> map = new HashMap<String, Object>();
			List<FieldError> errors = result.getFieldErrors();
			for (FieldError fieldError : errors) {
				System.out.println("�����ֶΣ�"+fieldError.getField());
				System.out.println("������Ϣ��"+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields",map);
		}else {
			empService.saveEmp(employee);
			return Msg.success();
		}
	}
	
	//У���û����Ƿ����
	@ResponseBody
	@RequestMapping(value="/checkUser")
	public  Msg checkUser(@RequestParam("empName")String empName) {
		//��У���û����Ƿ�Ϸ�
		String reg = "(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		if(!empName.matches(reg)) {
			return Msg.fail().add("va_msg","�û������Ϸ���");
		}
		boolean b = empService.checkUser(empName);
		if(b) {
			return Msg.success();
		}else {
			return Msg.fail().add("va_msg","�û����Ѵ��ڣ�");
		}
	}
	
	//У������
	@ResponseBody
	@RequestMapping(value="/checkEmail")
	public Msg checkEmail(@RequestParam("email")String email) {
		String reg = "(^([a-zA-Z0-9_\\.-]+)@([\\da-z\\.-]+)\\.([a-z\\.]{2,6})$)";
		if(!email.matches(reg)) {
			return Msg.fail().add("va_msg","�����ʽ����ȷ��");
		}else {
			return Msg.success();
		}
		
	}
	
	//����id�����û������޸�
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id) {
		Employee employee = empService.getEmp(id);
		return Msg.success().add("emp", employee);
		
	}
	
	//�û��޸ı���
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Employee employee) {
		empService.updateEmp(employee);
		return Msg.success();
	}
	
	//�û�ɾ��
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg delEmp(@PathVariable("ids")String ids) {
		//����ɾ��
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
