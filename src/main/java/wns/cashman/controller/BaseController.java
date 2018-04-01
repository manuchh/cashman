package wns.cashman.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class BaseController {
	
	@ExceptionHandler(Exception.class)
	public String handleException(HttpServletRequest req, Exception exception, Model model) {
		model.addAttribute("message", exception.getMessage());
		model.addAttribute("error", "Not Found");
		return "error";
	}

}
